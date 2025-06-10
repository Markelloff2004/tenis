package org.cedacri.pingpong.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class Player
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "Name cannot be null or blank")
    @Pattern(regexp = "^[A-Za-z.-]+$", message = "Name must contain only letters")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Surname cannot be null or blank")
    @Pattern(regexp = "^[A-Za-z.-]+$", message = "Surname must contain only letters")
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

    //    @Size(max = 5)
    @NotNull(message = "Select a hand playing style")
//    @Column(name = "hand", length = 5)
    @Column(name = "hand")
    @Pattern(regexp = "^(?i)(RIGHT|LEFT)$", message = "Hand must be either 'RIGHT' or 'LEFT'")
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

    @ManyToMany(mappedBy = "players", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<BaseTournament> tournaments = new HashSet<>();

    public Player(String name, String surname, String address, String email, String hand)
    {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.hand = hand;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    public void registerToTournament(BaseTournament tournamentToAdd) {
        tournaments.add(tournamentToAdd);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
