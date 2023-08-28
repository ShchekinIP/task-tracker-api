package org.example.task.tracker.repository;

import org.example.task.tracker.model.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStateRepository extends JpaRepository<TaskState, Long> {
    Optional<TaskState> findTaskStateByProjectIdAndNameContainsIgnoreCase(Long projectId, String name);
}
