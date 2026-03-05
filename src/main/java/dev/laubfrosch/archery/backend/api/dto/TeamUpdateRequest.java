package dev.laubfrosch.archery.backend.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamUpdateRequest {

    private String id;
    private String name;
    private String contactEmail;
    private Short expectedMembers;
    private RegistrationUpdateRequest tournamentRegistration;
    private List<MemberUpdateRequest> members;

    public record RegistrationUpdateRequest(
        Instant registration,
        Instant payment,
        Instant arrival,
        String note
    ) {}

    public record MemberUpdateRequest(
        String id,
        String firstName,
        String lastName,
        Short number
    ) {}
}
