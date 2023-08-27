package org.example.task.tracker.factories;

import org.example.task.tracker.dto.ProjectDto;
import org.example.task.tracker.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoFactory {
    public ProjectDto makeProjectDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
