package de.DevsWithoutHobbies.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatternMatcher {
    static boolean match(String pattern, String string) {
        String new_pattern = pattern.replaceAll("[*]", "\\\\w*");
        new_pattern = new_pattern.replaceAll("[?]", "\\\\w");
        new_pattern = "\\A" + new_pattern.replaceAll("[#]", "\\\\d") + "\\z";
        Pattern r = Pattern.compile(new_pattern);
        Matcher m = r.matcher(string);

        return m.find();
    }
}
