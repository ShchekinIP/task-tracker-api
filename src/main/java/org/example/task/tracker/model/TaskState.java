package org.example.task.tracker.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Table(name = "task_state")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private TaskState leftTaskState;

    @OneToOne
    private TaskState rightTaskState;

    private String name;

    private Instant createdAt = Instant.now();

    @ManyToOne
    private Project project;

    @OneToMany
    @JoinColumn(name = "task_state_id")
    private List<Task> tasks = new ArrayList<>();

    public Optional<TaskState> getLeftTaskState() {
        return Optional.ofNullable(leftTaskState);
    }

    public Optional<TaskState> getRightTaskState() {
        return Optional.ofNullable(rightTaskState);
    }
}
