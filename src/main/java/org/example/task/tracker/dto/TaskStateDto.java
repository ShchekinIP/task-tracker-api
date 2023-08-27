package org.example.task.tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TaskStateDto {
    private long id;

    private String name;

    private long ordinal;

    @JsonProperty("created_at")
    private Instant createdAt;
}
