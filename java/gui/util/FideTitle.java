package gui.util;

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