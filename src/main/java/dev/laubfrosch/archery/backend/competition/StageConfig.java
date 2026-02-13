package dev.laubfrosch.archery.backend.competition;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StageConfig {

    private Integer distanceMeters;
    private Integer shootingTimeSeconds;
    private UUID targetFaceId;
    private Integer maxSetsPerMatch;
    private Integer pointsToWinMatch;
    private String drawMode;
    private Integer matchPointsWin;
    private Integer matchPointsDraw;
    private Integer matchPointsLoss;
    private Integer teammembersPerMatch;
    private Integer arrowsPerTeammember;
}
