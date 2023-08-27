package org.example.task.tracker.factories;

import org.example.task.tracker.dto.TaskStateDto;
import org.example.task.tracker.model.TaskState;
import org.springframework.stereotype.Component;

@Component
public class TaskStateDtoFactory {
    public TaskStateDto makeTaskStateDto(TaskState taskState) {
        return TaskStateDto.builder()
                .id(taskState.getId())
                .name(taskState.getName())
                .ordinal(taskState.getOrdinal())
                .createdAt(taskState.getCreatedAt())
                .build();
    }
}
