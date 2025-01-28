package org.cedacri.pingpong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Score {

    @NotNull
    @Column(name = "top_player_score", nullable = false)
    private int topPlayerScore;

    @NotNull
    @Column(name = "bottom_player_score", nullable = false)
    private int bottomPlayerScore;

}
