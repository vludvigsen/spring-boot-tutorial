package boot.service;

import boot.dto.ScheduleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class TVService {

    private AsyncRestTemplate restTemplate = new AsyncRestTemplate();

    public DeferredResult<Date> nextOnAir(String showTitle) {
        final DeferredResult<Date> result = new DeferredResult<>();
        final DeferredResult<ScheduleDTO[]> svt1 = getScheduledPrograms("svt1");
        svt1.setResultHandler(svt1Result -> {
            List<ScheduleDTO> svt1List = Arrays.asList((ScheduleDTO[]) svt1Result);
            svt1List.stream().filter(program -> program.title.equalsIgnoreCase(showTitle))
                    .findFirst()
                    .ifPresent(program -> result.setResult(program.broadcastStartTime));
        });
        return result;
    }

    public DeferredResult<ScheduleDTO[]> getScheduledPrograms(String channel) {
        final DeferredResult<ScheduleDTO[]> deferredResult = new DeferredResult<>();
        final ListenableFuture<ResponseEntity<ScheduleDTO[]>> future = restTemplate.getForEntity("http://www.svt.se/play4api/channel/{channel}/schedule", ScheduleDTO[].class, channel);
        future.addCallback(
                result -> deferredResult.setResult(result.getBody()),
                exception -> deferredResult.setErrorResult(exception.getMessage()));
        return deferredResult;
    }

}
