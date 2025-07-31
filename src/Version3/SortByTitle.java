package Version3;

import java.util.*;

public class SortByTitle implements Comparator<Battle> {
    // Constants
    public static final String NAME = "TITLE";
    public static final int NUM = 0;

    // Description: This method compares two battles by their name
    public int compare(Battle b1, Battle b2) {
        String pfx1 = getPrefix(b1.name);
        String pfx2 = getPrefix(b2.name);

        int cmp = pfx1.compareTo(pfx2);
        if (cmp != 0) {
            return cmp;
        }
        String num1 = b1.name.substring(pfx1.length());
        String num2 = b2.name.substring(pfx2.length());
        return (num1.isEmpty() ? 0 : Integer.parseInt(num1)) - (num2.isEmpty() ? 0 : Integer.parseInt(num2));
    }

    public static String getPrefix(String str) {
        int i = str.length() - 1;
        while (i >= 0 && Character.isDigit(str.charAt(i))) {
            i--;
        }
        return str.substring(0, i + 1);
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
