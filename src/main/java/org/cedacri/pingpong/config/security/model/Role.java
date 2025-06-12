package org.cedacri.pingpong.config.security.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.cedacri.pingpong.config.security.model.enums.RoleEnum;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleEnum name;
}
