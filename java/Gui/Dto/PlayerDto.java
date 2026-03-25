package Gui.Dto;

import java.time.LocalDate;

public class PlayerDto {
    public int id;
    public String firstname;
    public String lastname;
    public int fide_rating;
    public FideTitle fide_title;
    public char gender;
    public LocalDate birthdate;
    public PlayerStatus status;

    public PlayerDto(int id, String firstname, String lastname, int fide_rating, FideTitle fide_title, char gender, LocalDate birthdate, PlayerStatus status) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fide_rating = fide_rating;
        this.fide_title = fide_title;
        this.gender = gender;
        this.birthdate = birthdate;
        this.status = status;
    }

    public enum FideTitle {
        GRANDMASTER("gm"), INTERNATIONAL_MASTER("im"), FIDE_MASTER("fm"), FIDE_CANDIDATE_MASTER("cm"), WOMAN_GRANDMASTER("wgm"), WOMAN_INTERNATIONAL_MASTER("wim"), WOMAN_FIDE_MASTER("wfm"), WOMAN_CANDIDATE_MASTER("wcm"), NONE("none");

        final String key;

        FideTitle(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public static FideTitle fromKeyOrName(String value) {
        for (FideTitle fideTitle : FideTitle.values()) {
            if (value.toLowerCase().equals(fideTitle.getKey()) || value.toUpperCase().equals(fideTitle.name())) {
                return fideTitle;
            }
        }
        return null;
    }

    public enum PlayerStatus {
        DISQUALIFIED, FINISHED_GAMES, PLAYING, APPLIED;
    }

    @Override
    public String toString() {
        return lastname + ", " + firstname;
    }
}
