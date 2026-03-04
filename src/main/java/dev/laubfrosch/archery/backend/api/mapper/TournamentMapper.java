package dev.laubfrosch.archery.backend.api.mapper;

import dev.laubfrosch.archery.backend.api.dto.TournamentCreateRequest;
import dev.laubfrosch.archery.backend.api.dto.TournamentOverviewResponse;
import dev.laubfrosch.archery.backend.tournament.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface TournamentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "generated", ignore = true)
    Tournament toEntity(TournamentCreateRequest req);

    @Mapping(target = "teamCount", ignore = true)
    TournamentOverviewResponse toOverview(Tournament tournament);
}
