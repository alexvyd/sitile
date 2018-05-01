package ru.parvenu.sitile.database;

public class TrackDbSchema {
    public static final class trackTable {
        public static final String NAME = "tracks";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
