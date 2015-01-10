package boot.service;

import boot.dto.ScheduleDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.ObservableResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

@Component
public class TVIntegration {

    private RestTemplate restTemplate = new RestTemplate();

    @HystrixCommand
    public Observable<ScheduleDTO[]> getSchedule(final String channel) {
        return new ObservableResult<ScheduleDTO[]>() {
            @Override
            public ScheduleDTO[] invoke() {
                return restTemplate.getForObject("http://www.svt.se/play4api/channel/{channel}/schedule", ScheduleDTO[].class, channel);
            }
        };
    }

}
