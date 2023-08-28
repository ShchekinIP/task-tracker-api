package org.example.task.tracker.controller;

import lombok.RequiredArgsConstructor;
import org.example.task.tracker.controller.helper.ControllerHelper;
import org.example.task.tracker.repository.ProjectRepository;
import org.example.task.tracker.dto.ProjectDto;
import org.example.task.tracker.dto.ResponseDto;
import org.example.task.tracker.factories.ProjectDtoFactory;
import org.example.task.tracker.model.Project;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectDtoFactory projectDtoFactory;
    private final ProjectRepository projectRepository;
    private final ControllerHelper controllerHelper;

    @PutMapping("")
    public ProjectDto createOrUpdateProject(
            @RequestParam(value = "id", required = false) Optional<Long> id,
            @RequestParam(value = "name", required = false) Optional<String> name) {

        name = name.filter(prefixName -> !prefixName.trim().isEmpty());

        if (!id.isPresent() && !name.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name can't be empty");
        }

        Project project = id.map(controllerHelper::getProject)
                .orElseGet(() -> Project.builder().build());

        name.ifPresent(projectName -> {
            projectRepository.findByName(projectName)
                    .filter(x -> Objects.equals(x.getId(), project.getId()))
                    .ifPresent(x -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getProjectAlreadyExistMessage(projectName));
                    });
            project.setName(projectName);
        });

        return projectDtoFactory.makeProjectDto(projectRepository.saveAndFlush(project));
    }


    @PostMapping("/createProject")
    public ProjectDto createProject(@RequestParam String name) {
        if (name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name can't be empty");
        }
        projectRepository.findByName(name).ifPresent(project -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getProjectAlreadyExistMessage(name));
        });
        Project project = projectRepository.saveAndFlush(
                Project.builder()
                        .name(name)
                        .createdAt(Instant.now())
                        .build()
        );
        return projectDtoFactory.makeProjectDto(project);
    }

    @PatchMapping("/editProject/{id}")
    public ProjectDto editProject(@RequestParam String name, @PathVariable("id") Long id) {
        Project project = controllerHelper.getProject(id);
        projectRepository.findByName(name)
                .filter(x -> Objects.equals(x.getId(), id))
                .ifPresent(x -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getProjectAlreadyExistMessage(name));
                });
        project.setName(name);
        projectRepository.saveAndFlush(project);
        return projectDtoFactory.makeProjectDto(project);
    }

    @Transactional
    @GetMapping("/fetchProjects")
    public List<ProjectDto> fetchProjects(@RequestParam(value = "prefixName", required = false) Optional<String> optionalPrefixName) {
        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());
        return optionalPrefixName
                .map(projectRepository::findAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy)
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/deleteProject/{id}")
    public ResponseDto deleteProject(@PathVariable("id") Long id) {
        controllerHelper.getProject(id);
        projectRepository.deleteById(id);
        return ResponseDto.makeDefault(true);
    }

    private String getProjectAlreadyExistMessage(String projectName) {
        return String.format("Project with name [%s] already exist", projectName);
    }
}
