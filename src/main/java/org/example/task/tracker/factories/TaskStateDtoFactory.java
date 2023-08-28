package org.example.task.tracker.factories;

import lombok.RequiredArgsConstructor;
import org.example.task.tracker.dto.TaskStateDto;
import org.example.task.tracker.model.TaskState;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskStateDtoFactory {
    private final TaskDtoFactory taskDtoFactory;
    public TaskStateDto makeTaskStateDto(TaskState taskState) {
        return TaskStateDto.builder()
                .id(taskState.getId())
                .name(taskState.getName())
                .leftTaskStateId(taskState.getLeftTaskState().map(TaskState::getId).orElse(null))
                .rightTaskStateId(taskState.getRightTaskState().map(TaskState::getId).orElse(null))
                .createdAt(taskState.getCreatedAt())
                .tasks(taskState.getTasks().stream().map(taskDtoFactory::makeTaskDto).collect(Collectors.toList()))
                .build();
    }
}
