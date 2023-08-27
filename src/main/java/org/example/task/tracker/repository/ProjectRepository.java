package org.example.task.tracker.repository;

import org.example.task.tracker.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);

    Stream<Project> streamAllBy();

    Stream<Project> findAllByNameStartsWithIgnoreCase(String prefixName);
}
