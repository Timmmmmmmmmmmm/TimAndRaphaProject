package Gui.Dto;

import java.time.LocalDateTime;

public class RoundDto {
    public int id;
    public int round_number;
    public RoundStatus status;
    public LocalDateTime begin;

    public RoundDto(int id, int round_number, RoundStatus status, LocalDateTime begin) {
        this.id = id;
        this.round_number = round_number;
        this.status = status;
        this.begin = begin;
    }

    public enum RoundStatus {
        PLANNED, RUNNING, COMPLETED;
    }
}
