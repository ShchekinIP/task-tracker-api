package org.example.task.tracker.controller;

import lombok.RequiredArgsConstructor;
import org.example.task.tracker.controller.helper.ControllerHelper;
import org.example.task.tracker.dto.ResponseDto;
import org.example.task.tracker.dto.TaskStateDto;
import org.example.task.tracker.factories.TaskStateDtoFactory;
import org.example.task.tracker.model.Project;
import org.example.task.tracker.model.TaskState;
import org.example.task.tracker.repository.TaskStateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/task-states")
@RequiredArgsConstructor
public class TaskStateController {
    private final TaskStateDtoFactory taskStateDtoFactory;
    private final TaskStateRepository taskStateRepository;
    private final ControllerHelper controllerHelper;


    @GetMapping("/{project_id}/task-states")
    public List<TaskStateDto> getTaskStates(@PathVariable(name = "project_id") Long projectId) {
        Project project = controllerHelper.getProject(projectId);
        return project.getTaskStates()
                .stream()
                .map(taskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{project_id}/create-task-state")
    public TaskStateDto createTaskState(@PathVariable(name = "project_id") Long projectId,
                                        @RequestParam(name = "task_state_name") String taskStateName) {

        validateName(taskStateName);

        Project project = controllerHelper.getProject(projectId);

        Optional<TaskState> optionalTaskState = Optional.empty();
        for (TaskState taskState : project.getTaskStates()) {
            if (taskState.getName().equals(taskStateName)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("task state with name [%s] already exists", taskStateName));
            }
            if (!taskState.getRightTaskState().isPresent()) {
                optionalTaskState = Optional.of(taskState);
                break;
            }
        }

        TaskState taskState = taskStateRepository.saveAndFlush(TaskState.builder().name(taskStateName).build());
        optionalTaskState.ifPresent(it -> {
            taskState.setRightTaskState(it);
            it.setLeftTaskState(it);
            taskStateRepository.saveAndFlush(it);
        });

        final TaskState savedTaskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateDtoFactory.makeTaskStateDto(savedTaskState);
    }

    @PatchMapping("/{task_state_id}")
    public TaskStateDto updateTaskState(@PathVariable(name = "task_state_id") Long taskStateId,
                                        @RequestParam(name = "task_state_name") String taskStateName) {

        validateName(taskStateName);

        TaskState taskState = controllerHelper.getTaskState(taskStateId);
        taskStateRepository.findTaskStateByProjectIdAndNameContainsIgnoreCase(taskState.getProject().getId(), taskStateName)
                .filter(anotherTaskState -> Objects.equals(anotherTaskState.getId(), taskStateId))
                .ifPresent(anotherTaskState -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("task state with name [%s] already exists", taskStateName));
                });

        taskState.setName(taskStateName);
        taskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateDtoFactory.makeTaskStateDto(taskState);

    }

    @PatchMapping("/{task_state_id}/changePosition")
    public TaskStateDto changeTaskStatePosition(@PathVariable(name = "task_state_id") Long taskStateId,
                                                @RequestParam(name = "left_task_state_id") Optional<Long> leftTaskStateId) {

        TaskState changeTaskState = controllerHelper.getTaskState(taskStateId);

        Project project = changeTaskState.getProject();

        Optional<Long> oldLeftTaskStateId = changeTaskState.getLeftTaskState().map(TaskState::getId);

        if (oldLeftTaskStateId.equals(leftTaskStateId)) {
            return taskStateDtoFactory.makeTaskStateDto(changeTaskState);
        }

        Optional<TaskState> newLeftTaskState = leftTaskStateId.map(it -> {
            if (taskStateId.equals(it)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "left task state id equals changed task id");
            }
            TaskState leftTaskStateEntity = controllerHelper.getTaskState(it);

            if (!Objects.equals(project.getId(), leftTaskStateEntity.getProject().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task state can be changed within same project");
            }
            return leftTaskStateEntity;
        });

        Optional<TaskState> newRightTaskState;

        if (!newLeftTaskState.isPresent()) {
            newRightTaskState = project.getTaskStates()
                    .stream()
                    .filter(anotherTaskState -> !anotherTaskState.getLeftTaskState().isPresent())
                    .findAny();
        } else {
            newRightTaskState = newLeftTaskState.get().getRightTaskState();
        }

        replaceTaskStates(changeTaskState);


        if (newLeftTaskState.isPresent()) {
            TaskState leftTaskState = newLeftTaskState.get();
            leftTaskState.setRightTaskState(changeTaskState);
            changeTaskState.setLeftTaskState(leftTaskState);


        } else {
            changeTaskState.setLeftTaskState(null);
        }

        if (newRightTaskState.isPresent()) {
            TaskState rightTaskState = newRightTaskState.get();
            rightTaskState.setLeftTaskState(changeTaskState);
            changeTaskState.setRightTaskState(rightTaskState);


        } else {
            changeTaskState.setRightTaskState(null);
        }

        taskStateRepository.saveAndFlush(changeTaskState);

        newLeftTaskState.ifPresent(taskStateRepository::saveAndFlush);
        newRightTaskState.ifPresent(taskStateRepository::saveAndFlush);

        return taskStateDtoFactory.makeTaskStateDto(changeTaskState);
    }


    @DeleteMapping("/{task_state_id}")
    public ResponseDto deleteTaskState(@PathVariable(name = "task_state_id") Long taskStateId) {

        TaskState changeTaskState = controllerHelper.getTaskState(taskStateId);

        replaceTaskStates(changeTaskState);

        taskStateRepository.saveAndFlush(changeTaskState);
        taskStateRepository.delete(changeTaskState);

        return ResponseDto.builder().answer(true).build();

    }

    private void replaceTaskStates(TaskState changeTaskState) {
        Optional<TaskState> oldLeftTaskState = changeTaskState.getLeftTaskState();
        Optional<TaskState> oldRightTaskState = changeTaskState.getRightTaskState();

        oldLeftTaskState.ifPresent(it -> {
            it.setRightTaskState(oldRightTaskState.orElse(null));
            taskStateRepository.saveAndFlush(it);
        });

        oldRightTaskState.ifPresent(it -> {
            it.setLeftTaskState(oldLeftTaskState.orElse(null));
            taskStateRepository.saveAndFlush(it);
        });
    }

    private void validateName(String taskStateName) {
        if (taskStateName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "task state name can't be empty");
        }
    }

}
