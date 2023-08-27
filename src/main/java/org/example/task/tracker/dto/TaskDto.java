package org.example.task.tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TaskDto {
    private long id;

    private String name;

    @JsonProperty("created_at")
    private Instant createdAt;

    private String description;
}
