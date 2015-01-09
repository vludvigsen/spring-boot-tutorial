package boot.service;

import boot.dto.ScheduleDTO;
import boot.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import java.util.Arrays;
import java.util.List;

@Service
public class TVService {

    @Autowired
    private TVIntegration tvIntegration;

    private AsyncRestTemplate restTemplate = new AsyncRestTemplate();

    public DeferredResult<ScheduleDTO> nextOnAir(String showTitle) {
        // Create a result to return immediately, and to set callbacks on
        final DeferredResult<ScheduleDTO> result = new DeferredResult<>();

        // Start a request for all scheduled programs on svt1 today
        final DeferredResult<ScheduleDTO[]> svt1 = getScheduledProgramsHystrix("svt1");

        // When the svt1 schedule is ready, invoke the callback provided
        svt1.setResultHandler(svt1Result -> {
            List<ScheduleDTO> svt1List = Arrays.asList((ScheduleDTO[]) svt1Result);

            svt1List.stream()
                    // filter the list of programs to only include our title
                    .filter(program -> program.title.equalsIgnoreCase(showTitle))
                    // select the first one
                    .findFirst()
                    // if there was a first one, set it as the result
                    .ifPresent(result::setResult);

            // If no result was set earlier, set an error result
            if (!result.hasResult()) {
                result.setErrorResult(new NotFoundException(String.format("The program %s was not found", showTitle)));
            }
        });

        // Return the result (to be populated at a later time)
        return result;
    }

    public DeferredResult<ScheduleDTO[]> getScheduledPrograms(String channel) {
        // Create a result to return immediately, and to set callbacks on
        final DeferredResult<ScheduleDTO[]> deferredResult = new DeferredResult<>();

        // Request the data from SVT API as a future
        final ListenableFuture<ResponseEntity<ScheduleDTO[]>> future =
                restTemplate.getForEntity("http://origin-www.svt.se/play4api/channel/{channel}/schedule", ScheduleDTO[].class, channel);

        // Set the actions to perform when the SVT API either completes or fails
        future.addCallback(
                result -> deferredResult.setResult(result.getBody()),
                exception -> deferredResult.setErrorResult(exception.getMessage()));

        // return the result (to be populated by the callbacks above at a later time)
        return deferredResult;
    }

    public DeferredResult<ScheduleDTO[]> getScheduledProgramsHystrix(String channel) {
        // Create a result to return immediately, and to set callbacks on
        final DeferredResult<ScheduleDTO[]> deferredResult = new DeferredResult<>();

        // Fetch an observable result of our Hystrix request
        Observable<ScheduleDTO[]> observable = tvIntegration.getSchedule(channel);

        // subscribe to the observable and invoke the methods on next
        observable.subscribe(deferredResult::setResult,
                deferredResult::setErrorResult);

        return deferredResult;
    }
}
