package boot.service;

import boot.dto.ScheduleDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class TVService {

    private RestTemplate restTemplate = new RestTemplate();

    public Date nextOnAir(String showTitle) {
        // add logic to determine if the given show is on TV today
        return null;
    }

    public ScheduleDTO[] getScheduledPrograms(String channel) {
        return restTemplate.getForObject("http://www.svt.se/play4api/channel/{channel}/schedule", ScheduleDTO[].class, channel);
    }

}
