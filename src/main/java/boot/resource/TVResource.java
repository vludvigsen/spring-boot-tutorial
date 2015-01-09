package boot.resource;

import boot.dto.ScheduleDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TVResource {

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/schedule/{channel}")
    public ScheduleDTO[] getSchedule(@PathVariable String channel) {
        return restTemplate.getForObject("http://www.svt.se/play4api/channel/{channel}/schedule", ScheduleDTO[].class, channel);
    }

}
