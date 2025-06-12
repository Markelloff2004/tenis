package org.cedacri.pingpong.model.tournament;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tournaments_robin")
public class TournamentRoundRobin extends BaseTournament {

}
