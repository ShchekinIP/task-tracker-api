package org.example.task.tracker.repository;

import org.example.task.tracker.model.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStateRepository extends JpaRepository<TaskState, Long> {
}
