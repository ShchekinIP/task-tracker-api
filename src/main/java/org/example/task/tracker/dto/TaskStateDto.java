package org.example.task.tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TaskStateDto {
    private long id;

    private String name;

    @JsonProperty("left_task_state_id")
    private Long leftTaskStateId;

    @JsonProperty("right_task_state_id")
    private Long rightTaskStateId;

    @JsonProperty("created_at")
    private Instant createdAt;

    private List<TaskDto> tasks;
}
