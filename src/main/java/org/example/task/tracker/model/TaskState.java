package org.example.task.tracker.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @Column(unique = true)
    private String name;

    private long ordinal;

    private Instant createdAt = Instant.now();

    @OneToMany
    @JoinColumn(name = "task_state_id")
    private List<Task> tasks = new ArrayList<>();
}
