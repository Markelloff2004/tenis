package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.cedacri.pingpong.entity.Tournament;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotBlank(message = "Name cannot be null or blank")
    @Pattern(regexp = "^[A-Za-z.-]+$", message = "Name must contain only letters")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 100)
    @NotBlank(message = "Surname cannot be null or blank")
    @Pattern(regexp = "^[A-Za-z.-]+$", message = "Name must contain only letters")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @NotBlank(message = "Address cannot be null or blank")
    @Column(name = "address", nullable = false)
    @Size(max = 50)
    private String address;

    @Size(max = 100)
    @NotBlank(message = "Email cannot be null or blank")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @Min(value = 0, message = "Rating cannot be negative")
    @Column(name = "rating", nullable = false, columnDefinition = "int default 0")
    private Integer rating = 0;

    @Size(max = 5)
    @NotNull(message = "Select a hand playing style")
    @Column(name = "hand", length = 5)
    private String hand;

    @Min(value = 0, message = "Won matches cannot be negative")
    @Column(name = "won_matches", nullable = false, columnDefinition = "int default 0")
    private Integer wonMatches = 0;

    @Min(value = 0, message = "Lost matches cannot be negative")
    @Column(name = "lost_matches", nullable = false, columnDefinition = "int default 0")
    private Integer lostMatches = 0;

    @Min(value = 0, message = "Goals scored cannot be negative")
    @Column(name = "goals_scored", nullable = false, columnDefinition = "int default 0")
    private Integer goalsScored = 0;

    @Min(value = 0, message = "Goals lost cannot be negative")
    @Column(name = "goals_lost", nullable = false, columnDefinition = "int default 0")
    private Integer goalsLost = 0;

    @ManyToMany(mappedBy = "players", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Tournament> tournaments = new HashSet<>();

    public Player(String name, String surname, LocalDate birthDate, String email, String address, LocalDate createdAt, Integer rating, String hand,
                  Integer wonMatches, Integer lostMatches, Integer goalsScored, Integer goalsLost) {

        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.address = address;
        this.email = email;
        this.createdAt = createdAt;
        this.rating = rating;
        this.hand = hand;
        this.wonMatches = wonMatches;
        this.lostMatches = lostMatches;
        this.goalsScored = goalsScored;
        this.goalsLost = goalsLost;
    }

    public Player(Integer rating, String name, String surname, LocalDate birthDate, Integer wonMatches, Integer lostMatches) {
        this.rating = rating;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.wonMatches = wonMatches;
        this.lostMatches = lostMatches;
    }

    public Player(Long id, String name, String surname, LocalDate birthDate, String email, String address, Integer rating,
                  String hand, Integer wonMatches, Integer lostMatches, Integer goalsScored, Integer goalsLost) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.address = address;
        this.rating = rating;
        this.hand = hand;
        this.wonMatches = wonMatches;
        this.lostMatches = lostMatches;
        this.goalsScored = goalsScored;
        this.goalsLost = goalsLost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
