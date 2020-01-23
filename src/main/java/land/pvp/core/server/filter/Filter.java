package land.pvp.core.server.filter;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static land.pvp.core.utils.StringUtil.IP_REGEX;
import static land.pvp.core.utils.StringUtil.URL_REGEX;

public class Filter {
    private static final List<String> FILTERED_PHRASES = ImmutableList.of(
            "ddos", "aids", "lag", "cheats", "cheating", "cheat ", " cheat", "hack", "trash", "reach", "sell",
            "swat", "e t b","cancer", "toxic", "smd", "kys", "etb", "fag", "hax", "dox", "blatant", "porn"
    );
    private static final List<NegativeWordPair> NEGATIVE_WORD_PAIRS;
    private static final String[] SINGLE_FILTERED_WORDS = {"L", "ez", "kb", "#"};
    private static final String[] WHITELISTED_LINKS = {
            "pvp.land", "youtube.com", "youtu.be", "imgur.com", "prntscr.com", "prnt.sc", "gfycat.com", "gyazo.com",
            "twitter.com", "spotify.com", "twitch.tv", "tinypic.com"
    };

    static {
        String[] words = new String[]{"fuck", "shit", "ass", "trash", "garbage", "horrible"};
        String[] matches = new String[]{"kb", "knockback", "server", "staff", "pots"};

        List<NegativeWordPair> pairs = new ArrayList<>();

        for (String word : words) {
            pairs.add(new NegativeWordPair(word, matches));
        }

        NEGATIVE_WORD_PAIRS = ImmutableList.copyOf(pairs);
    }

    public boolean isFiltered(String msg) {
        msg = msg.toLowerCase().trim();

        for (String word : msg.split(" ")) {
            Matcher matcher = IP_REGEX.matcher(word);

            if (matcher.matches()) {
                return true;
            }
        }

        for (String word : msg
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("/\\", "a")
                .replace("/-\\", "a")
                .replace("()", "o")
                .replace("2", "z")
                .replace("@", "a")
                .replace("|", "l")
                .replace("7", "t")
                .replace("4", "a")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .trim().split(" ")) {
            Matcher matcher = URL_REGEX.matcher(word);

            boolean filtered = false;

            if (matcher.matches()) {
                int matches = 0;

                for (String link : WHITELISTED_LINKS) {
                    if (word.contains(link)) {
                        matches++;
                    }
                }

                filtered = matches == 0;
            }

            if (filtered) {
                return true;
            }
        }

        for (String word : msg
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("/\\", "a")
                .replace("/-\\", "a")
                .replace("()", "o")
                .replace("2", "z")
                .replace("@", "a")
                .replace("|", "l")
                .replace("7", "t")
                .replace("4", "a")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .replace(" ", "")
                .trim().split(" ")) {
            Matcher matcher = URL_REGEX.matcher(word);

            boolean filtered = false;

            if (matcher.matches()) {
                int matches = 0;

                for (String link : WHITELISTED_LINKS) {
                    if (word.contains(link)) {
                        matches++;
                    }
                }

                filtered = matches == 0;
            }

            if (filtered) {
                return true;
            }
        }

        for (String word : msg
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("/\\", "a")
                .replace("/-\\", "a")
                .replace("()", "o")
                .replace("2", "z")
                .replace("@", "a")
                .replace("|", "l")
                .replace("7", "t")
                .replace("4", "a")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .trim().replaceAll("\\p{Punct}|\\d", ":").replace(":dot:", ".").split(" ")) {
            Matcher matcher = URL_REGEX.matcher(word);

            boolean filtered = false;

            if (matcher.matches()) {
                int matches = 0;

                for (String link : WHITELISTED_LINKS) {
                    if (word.contains(link)) {
                        matches++;
                    }
                }

                filtered = matches == 0;
            }

            if (filtered) {
                return true;
            }
        }


        for (String word : msg
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("/\\", "a")
                .replace("/-\\", "a")
                .replace("()", "o")
                .replace("2", "z")
                .replace("@", "a")
                .replace("|", "l")
                .replace("7", "t")
                .replace("4", "a")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .replace(" ", "")
                .trim().replaceAll("\\p{Punct}|\\d", ":").replace(":dot:", ".").split(" ")) {
            Matcher matcher = URL_REGEX.matcher(word);

            boolean filtered = false;

            if (matcher.matches()) {
                int matches = 0;

                for (String link : WHITELISTED_LINKS) {
                    if (word.contains(link)) {
                        matches++;
                    }
                }

                filtered = matches == 0;
            }

            if (filtered) {
                return true;
            }
        }

        String parsed = msg
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("/\\", "a")
                .replace("/-\\", "a")
                .replace("()", "o")
                .replace("2", "z")
                .replace("@", "a")
                .replace("|", "l")
                .replace("7", "t")
                .replace("4", "a")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .trim();

        String noPuncParsed = parsed.replaceAll("\\p{Punct}|\\d", "").trim();

        for (String word : SINGLE_FILTERED_WORDS) {
            if (noPuncParsed.equalsIgnoreCase(word) || noPuncParsed.startsWith(word + " ")
                    || noPuncParsed.endsWith(" " + word) || noPuncParsed.contains(" " + word + " ")) {
                return true;
            }
        }

        for (NegativeWordPair pair : NEGATIVE_WORD_PAIRS) {
            for (String match : pair.getMatches()) {
                if (noPuncParsed.contains(pair.getWord()) && noPuncParsed.contains(match)) {
                    return true;
                }
            }
        }

        for (String phrase : FILTERED_PHRASES) {
            if (parsed.contains(phrase)) {
                return true;
            }
        }

        Optional<String> filterablePhrase = FILTERED_PHRASES.stream().map(phrase -> phrase.replaceAll(" ", "")).filter(parsed::contains).findFirst();

        if (filterablePhrase.isPresent()) {
            return true;
        }

        String[] split = parsed.trim().split(" ");

        for (String word : split) {
            if (FILTERED_PHRASES.contains(word)) {
                return true;
            }
        }

        return false;
    }
}
