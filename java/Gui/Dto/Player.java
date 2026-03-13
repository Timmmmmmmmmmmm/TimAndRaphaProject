package Gui.Dto;

import java.time.LocalDate;

public class Player {
    public int id;
    public String firstname;
    public String lastname;
    public int fideRating;
    public FideTitle fideTitle;
    public char gender;
    public LocalDate birthdate;
    public PlayerStatus playerStatus;

    public Player(int id, String firstname, String lastname, int fideRating, FideTitle fideTitle, char gender, LocalDate birthdate, PlayerStatus playerStatus) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fideRating = fideRating;
        this.fideTitle = fideTitle;
        this.gender = gender;
        this.birthdate = birthdate;
        this.playerStatus = playerStatus;
    }

    public enum FideTitle {
        GRANDMASTER, INTERNATIONAL_MASTER, FIDE_MASTER, FIDE_CANDIDATE_MASTER, WOMAN_GRANDMASTER, WOMAN_INTERNATIONAL_MASTER, WOMAN_FIDE_MASTER, WOMAN_CANDIDATE_MASTER, NONE;
    }

    public enum PlayerStatus {
        DISQUALIFIED, FINISHED_GAMES, PLAYING, APPLIED;
    }

}
