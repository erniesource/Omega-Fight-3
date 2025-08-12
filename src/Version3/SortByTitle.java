package Version3;

import java.util.*;

public class SortByTitle implements Comparator<Battle> {
    // Constants
    public static final String NAME = "TITLE";
    public static final int NUM = 0;

    // Description: This method compares two battles by their name (They are not sorted by some other order if their names are the same)
    public int compare(Battle b1, Battle b2) {
        return b1.name.compareTo(b2.name);
    }
}

class SortByGrade implements Comparator<Battle> {
    // Constants
    public static final String NAME = "GRADE";
    public static final int NUM = 1;

    // Description: This method compares two battles by their grade. If they have the same grade, the are compared by name
    public int compare(Battle b1, Battle b2) {
        if (Double.compare(b1.grade, b2.grade) != 0) {
            return Double.compare(b1.grade, b2.grade);
        }  
        else return b1.name.compareTo(b2.name);
    }
}
