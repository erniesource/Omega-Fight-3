package Version3;

import java.util.*;

public class SortByTitle implements Comparator<Battle> {
    public static final String NAME = "TITLE";
    public static final int NUM = 0;

    public int compare(Battle b1, Battle b2) {
        return b1.name.compareTo(b2.name);
    }
}

class SortByGrade implements Comparator<Battle> {
    public static final String NAME = "GRADE";
    public static final int NUM = 1;

    public int compare(Battle b1, Battle b2) {
        if (Double.compare(b1.grade, b2.grade) != 0) {
            return Double.compare(b1.grade, b2.grade);
        }  
        else return b1.name.compareTo(b2.name);
    }
}
