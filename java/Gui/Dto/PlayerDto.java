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

    public PlayerDto(int id, String firstname, String lastname, int fide_rating, FideTitle fide_title, char gender, LocalDate birthdate) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fide_rating = fide_rating;
        this.fide_title = fide_title;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public enum FideTitle {
        GRANDMASTER("GM"), INTERNATIONAL_MASTER("IM"), FIDE_MASTER("FM"), FIDE_CANDIDATE_MASTER("FM"), WOMAN_GRANDMASTER("WGM"), WOMAN_INTERNATIONAL_MASTER("WIM"), WOMAN_FIDE_MASTER("WFM"), WOMAN_CANDIDATE_MASTER("WCM"), NONE("NONE");

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
// @todo Bei Anmeldungen DTO hinzufügen
//    public enum PlayerStatus {
//        APPLIED(0), PLAYING(1), FINISHED_GAMES(2), DISQUALIFIED(3);
//        final int key;
//        PlayerStatus(int key) {
//            this.key = key;
//        }
//    }

    @Override
    public String toString() {
        return lastname + ", " + firstname;
    }
}
