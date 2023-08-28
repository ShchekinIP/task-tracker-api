package org.example.task.tracker.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "task")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private Instant createdAt = Instant.now();

    private String description;

}
