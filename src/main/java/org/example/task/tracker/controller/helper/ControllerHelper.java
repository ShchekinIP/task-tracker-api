package org.example.task.tracker.controller.helper;

import lombok.RequiredArgsConstructor;
import org.example.task.tracker.controller.TaskStateController;
import org.example.task.tracker.model.Project;
import org.example.task.tracker.model.TaskState;
import org.example.task.tracker.repository.ProjectRepository;
import org.example.task.tracker.repository.TaskStateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Component
@Transactional
public class ControllerHelper {
    private final ProjectRepository projectRepository;
    private final TaskStateRepository taskStateRepository;


    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Project with id [%d] not found", id)));
    }

    public TaskState getTaskState(Long taskStateId) {
        return taskStateRepository.findById(taskStateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Task State with id [%d] not found", taskStateId)));
    }
}
