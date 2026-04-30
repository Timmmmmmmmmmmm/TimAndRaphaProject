package gui.dto;

import gui.util.FideTitle;

public class GameInitDto {
    public boolean isSimple;
    public int base_consider_time;
    public int move_consider_time;
    public String whiteFirstname;
    public String whiteLastname;
    public int whiteRating;
    public FideTitle whiteTitle;
    public String blackFirstname;
    public String blackLastname;
    public int blackRating;
    public FideTitle blackTitle;

    public GameInitDto(int base_consider_time, int move_consider_time) {
        isSimple = true;
        this.base_consider_time = base_consider_time;
        this.move_consider_time = move_consider_time;
    }

    public GameInitDto(int base_consider_time, int move_consider_time, String whiteFirstname, String whiteLastname, int whiteRating,
                       FideTitle whiteTitle, String blackFirstname, String blackLastname, int blackRating, FideTitle blackTitle) {
        isSimple = false;
        this.base_consider_time = base_consider_time;
        this.move_consider_time = move_consider_time;
        this.whiteFirstname = whiteFirstname;
        this.whiteLastname = whiteLastname;
        this.whiteRating = whiteRating;
        this.whiteTitle = whiteTitle;
        this.blackFirstname = blackFirstname;
        this.blackLastname = blackLastname;
        this.blackRating = blackRating;
        this.blackTitle = blackTitle;
    }
}
