package dev.laubfrosch.archery.backend.tournament.creation;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TournamentCreateResponse {

    private UUID tournamentId;
    private String name;
}