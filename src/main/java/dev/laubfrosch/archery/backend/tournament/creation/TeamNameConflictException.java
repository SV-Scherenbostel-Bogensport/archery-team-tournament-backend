package dev.laubfrosch.archery.backend.tournament.creation;

public class TeamNameConflictException extends RuntimeException {

    public TeamNameConflictException(String name) {
        super("Team '" + name + "' existiert bereits in diesem Turnier.");
    }
}