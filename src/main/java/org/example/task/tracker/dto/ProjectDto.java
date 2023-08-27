package org.example.task.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ProjectDto {
    private long id;

    private String name;

     private Instant createdAt;


}
