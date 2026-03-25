package Gui.Dto;

import java.time.LocalDate;

public class TournamentDto {
    public int id;
    public String name;
    public LocalDate date;
    public String city;
    public int base_consider_time;
    public int move_consider_time;
    public TournamentStatus status;

    public TournamentDto(int id, String name, LocalDate date, String city, int base_consider_time, int move_consider_time, TournamentStatus status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.city = city;
        this.base_consider_time = base_consider_time;
        this.move_consider_time = move_consider_time;
        this.status = status;
    }

    public enum TournamentStatus {
        PLANNED, ACTIVE, COMPLETED;
    }
}
