package boot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleDTO {
    public String title;
    public Date broadcastStartTime;
    public Date broadcastEndTime;
    public String description;
    public String shortDescription;
}
