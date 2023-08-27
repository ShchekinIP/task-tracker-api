package org.example.task.tracker.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "project")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    private Instant createdAt = Instant.now();

    @OneToMany
    @JoinColumn(name = "project_id")
    private List<TaskState> taskStates = new ArrayList<>();
}
