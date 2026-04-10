package gui.dto;

import gui.DatabaseConnection;
import gui.util.DateUtil;
import gui.util.FideTitle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerDto {
    public final int id;
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

    public static List<PlayerDto> getAsList(String sql) {
        List<HashMap<String, String>> playersHashMap = DatabaseConnection.executeSql(sql);
        if (playersHashMap == null || playersHashMap.isEmpty()) {
            return null;
        }
        List<PlayerDto> playerList = new ArrayList<>();

        for (HashMap<String, String> player : playersHashMap) {
            try {
                playerList.add(new PlayerDto(
                        Integer.parseInt(player.get("id")),
                        player.get("firstname"),
                        player.get("lastname"),
                        Integer.parseInt(player.get("fide_rating")),
                        fromKeyOrName(player.get("fide_title")),
                        player.get("gender").charAt(0),
                        DateUtil.parseLocalDate(player.get("birthdate"))
                ));
            } catch (Exception ignored) {
            }
        }

        return playerList;
    }

    public static FideTitle fromKeyOrName(String value) {
        for (FideTitle fideTitle : FideTitle.values()) {
            if (value.toUpperCase().equals(fideTitle.getKey()) || value.toUpperCase().equals(fideTitle.name())) {
                return fideTitle;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return lastname + ", " + firstname;
    }
}
