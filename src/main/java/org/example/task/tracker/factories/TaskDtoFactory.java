package org.example.task.tracker.factories;

import org.example.task.tracker.dto.TaskDto;
import org.example.task.tracker.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {
    public TaskDto makeTaskDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .name(task.getName())
                .createdAt(task.getCreatedAt())
                .description(task.getDescription())
                .build();
    }
}
