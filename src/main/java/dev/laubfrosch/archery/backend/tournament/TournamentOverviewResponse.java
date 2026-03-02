package dev.laubfrosch.archery.backend.tournament;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class TournamentOverviewResponse {

    private UUID id;
    private String name;
    private String location;
    private LocalDate date;
    private LocalTime startTime;
    private boolean allowRegistration;
    private Integer teamCount;
    private Integer maxSlots;
    private boolean generated;
}