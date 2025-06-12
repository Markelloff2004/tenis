package org.cedacri.pingpong.model.match;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Score {

    @Min(value = 0, message = "Score cannot be negative")
    @Column(name = "top_player_score")
    private int topPlayerScore;

    @Min(value = 0, message = "Score cannot be negative")
    @Column(name = "bottom_player_score")
    private int bottomPlayerScore;


    @Override
    public String toString() {
        return "{" + topPlayerScore + "-" + bottomPlayerScore + '}';
    }
}
