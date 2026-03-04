package dev.laubfrosch.archery.backend.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class TournamentCreateRequest {

    @NotBlank
    private String name;

    private String description;

    private String location;

    private String address;

    private LocalDate date;

    private LocalTime startTime;

    private Short maxSlots;

    private LocalDate registrationDeadline;

    private boolean allowRegistration = false;

    @Email
    private String contactEmail;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Ungültiges Hex-Format")
    private String primaryColor;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Ungültiges Hex-Format")
    private String secondaryColor;

    private List<@Valid TeamCreateRequest> teams;

    public record TeamCreateRequest(
        @NotBlank String name,
        @Email String contactEmail,
        Short expectedMembers,
        @Valid RegistrationCreateRequest registration,
        List<@Valid MemberCreateRequest> members
    ) {}

    public record MemberCreateRequest(
        @NotBlank String firstName,
        String lastName,
        Short number
    ) {}

    public record RegistrationCreateRequest(
        Instant registration,
        Instant payment,
        Instant arrival,
        String note
    ) {}
}