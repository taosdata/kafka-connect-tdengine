package com.taosdata.kafka.connect.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author taosdata
 */
public class MatchKeyUtil {
    private final static Pattern PATTERN_BETWEEN_TWO_CHARACTER = Pattern.compile("\\{\\{(.*?)\\}\\}");

    public static List<String> getMatchers(String input) {
        List<String> matchers = new ArrayList<>();
        Matcher matcher = PATTERN_BETWEEN_TWO_CHARACTER.matcher(input);

        int matcher_start = 0;

        while (matcher.find(matcher_start)) {
            matchers.add(matcher.group(1));
            matcher_start = matcher.end();
        }
        return matchers;
    }

    public static String replaceKey(String str, Map<String, String> valMap) {
        List<String> matchers = getMatchers(str);
        for (String matcher: matchers) {
            String val = valMap.get(matcher);
            if (null == val) {
                val = "";
            }
            str = str.replace("{{"+matcher+"}}", val);
        }
        return str;
    }
}
