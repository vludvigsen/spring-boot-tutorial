package boot.service;

import boot.dto.ScheduleDTO;
import boot.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class TVService {

    private AsyncRestTemplate restTemplate = new AsyncRestTemplate();

    public DeferredResult<ScheduleDTO> nextOnAir(String showTitle) {

        DeferredResult<ScheduleDTO> deferredResult = new DeferredResult<>();

        final DeferredResult<List<ScheduleDTO>> scheduledPrograms = getScheduledPrograms("svt1");

        scheduledPrograms.setResultHandler(svt1Programs -> {
            ((List<ScheduleDTO>) svt1Programs).stream()
                    .filter(program -> program.title.equalsIgnoreCase(showTitle))
                    .findFirst()
                    .ifPresent(deferredResult::setResult);
            if(!scheduledPrograms.hasResult()) {
                scheduledPrograms.setErrorResult(new NotFoundException(String.format("The program %s was not found", showTitle)));
            }
        });

        return deferredResult;

    }

    public DeferredResult<List<ScheduleDTO>> getScheduledPrograms(String channel) {

        DeferredResult<List<ScheduleDTO>> deferredResult = new DeferredResult<>();
        final ListenableFuture<ResponseEntity<ScheduleDTO[]>> listenableFuture = restTemplate.getForEntity("http://www.svt.se/play4api/channel/{channel}/schedule", ScheduleDTO[].class, channel);

        listenableFuture.addCallback(result -> {
            List<ScheduleDTO> list = Arrays.asList(result.getBody());
            deferredResult.setResult(list);

        }, deferredResult::setErrorResult);

        return deferredResult;
    }

}
