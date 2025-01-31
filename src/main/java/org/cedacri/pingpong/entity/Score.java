package org.cedacri.pingpong.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Score {

    @Column(name = "top_player_score")
    private int topPlayerScore;

    @Column(name = "bottom_player_score")
    private int bottomPlayerScore;

    @Override
    public String toString() {
        return "{" + topPlayerScore + "-" + bottomPlayerScore + '}';
    }
}
