package dev.laubfrosch.archery.backend.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TournamentCreateResponse {

    private UUID tournamentId;
    private String name;
}