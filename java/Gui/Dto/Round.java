package Gui.Dto;

import java.time.LocalDateTime;

public class Round {
    public int id;
    public int roundNumber;
    public RoundStatus roundStatus;
    public LocalDateTime begin;

    public Round(int id, int roundNumber, RoundStatus roundStatus, LocalDateTime begin) {
        this.id = id;
        this.roundNumber = roundNumber;
        this.roundStatus = roundStatus;
        this.begin = begin;
    }

    public enum RoundStatus {
        PLANNED, RUNNING, FINISHED;
    }
}
