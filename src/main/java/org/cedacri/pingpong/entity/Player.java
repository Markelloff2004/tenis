package org.cedacri.pingpong.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 100)
    @NotNull
    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "address", nullable = false)
    @Size(max = 50)
    private String address;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "rating", nullable = false, columnDefinition = "int default 0")
    private Integer rating = 0;

    @Size(max = 5)
    @Column(name = "hand", length = 5)
    private String hand;

    @Column(name = "won_matches", nullable = false, columnDefinition = "int default 0")
    private Integer wonMatches = 0;

    @Column(name = "lost_matches", nullable = false, columnDefinition = "int default 0")
    private Integer lostMatches = 0;

    @Column(name = "goals_scored", nullable = false, columnDefinition = "int default 0")
    private Integer goalsScored = 0;

    @Column(name = "goals_lost", nullable = false, columnDefinition = "int default 0")
    private Integer goalsLost = 0;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "tournament_players",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "tournament_id")
    )
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
