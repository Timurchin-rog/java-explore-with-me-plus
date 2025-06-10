package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "hits")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String app;

    String uri;

    String ip;

    @Column(name = "time_request")
    String timestamp;
}
