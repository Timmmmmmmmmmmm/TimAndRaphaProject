package gui.util;

public enum FideTitle {
    GRANDMASTER("GM", "Grandmaster"),
    INTERNATIONAL_MASTER("IM", "International Master"),
    FIDE_MASTER("FM", "FIDE Master"),
    FIDE_CANDIDATE_MASTER("FM", "FIDE Candidate Master"),
    WOMAN_GRANDMASTER("WGM", "Woman Grandmaster"),
    WOMAN_INTERNATIONAL_MASTER("WIM", "Woman International Master"),
    WOMAN_FIDE_MASTER("WFM", "Woman FIDE Master"),
    WOMAN_CANDIDATE_MASTER("WCM", "Woman Candidate Master"),
    NONE("NONE", "No Title");

    final String key;
    final String name;

    FideTitle(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return name;
    }
}