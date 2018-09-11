package com.github.davsx.llearn.service.LearnQuiz;

import android.net.Uri;
import android.util.Pair;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.google.common.collect.Lists;
import info.debatty.java.stringsimilarity.Levenshtein;

import java.util.*;

import static com.github.davsx.llearn.LLearnConstants.LEARN_CARD_KEYBOARD_COLUMNS;
import static com.github.davsx.llearn.LLearnConstants.SPANISH_LOWERCASE_LETTERS;

public class LearnQuizData {
    private String frontText;
    private String backText;
    private Uri imageUri;
    private List<String> choices;
    private boolean isReversed = false;
    private List<List<Character>> keyboardKeys;

    public static LearnQuizData Build(LearnQuizType quizType, CardEntity card, List<CardEntity> randomCards) {
        if (quizType.equals(LearnQuizType.NONE)) {
            return null;
        }
        LearnQuizData data = new LearnQuizData();
        data.setFrontText(card.getFront());
        data.setBackText(card.getBack());
        if (quizType.equals(LearnQuizType.SHOW_CARD_WITH_IMAGE)) {
            data.setImageUri(null);
        } else if (quizType.equals(LearnQuizType.CHOICE_1of4)) {
            data.setChoices(findChoicesFor(data, randomCards));
        } else if (quizType.equals(LearnQuizType.CHOICE_1of4_REVERSE)) {
            data.setFrontText(card.getBack());
            data.setBackText(card.getFront());
            data.setReversed(true);
            data.setChoices(findChoicesFor(data, randomCards));
        } else if (quizType.equals(LearnQuizType.KEYBOARD_INPUT)) {
            data.setKeyboardKeys(findKeyboardKeysFor(card.getBack()));
        }
        return data;
    }

    private static List<String> findChoicesFor(LearnQuizData data, List<CardEntity> randomCards) {
        String original = data.getBackText();
        ArrayList<Pair<Integer, String>> candidates = new ArrayList<>();
        for (CardEntity card : randomCards) {
            String candidate = data.isReversed() ? card.getFront() : card.getBack();

            Integer distance = Levenshtein.Distance(original, candidate);
            if (distance > 0) {
                candidates.add(new Pair<>(distance, candidate));
            }
        }

        // Sort by distance only. The actual candidate String can be in random order
        Collections.sort(candidates, new Comparator<Pair<Integer, String>>() {
            @Override
            public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
                return o1.first.compareTo(o2.first);
            }
        });

        // We don't have enough candidates; fill it with empty String
        if (candidates.size() < 3) {
            List<String> result = new ArrayList<>();
            for (Pair<Integer, String> candidate : candidates) {
                result.add(candidate.second);
            }
            while (result.size() != 3) {
                result.add("");
            }
            return result;
        }

        Random rng = new Random(System.currentTimeMillis());
        Set<String> choices = new HashSet<>();
        while (choices.size() != 3) {
            int index = rng.nextInt(Math.max(candidates.size(), 10));
            choices.add(candidates.get(index).second);
        }

        List<String> result = new ArrayList<>(choices);
        Collections.shuffle(result);

        return result;
    }

    private static List<List<Character>> findKeyboardKeysFor(String word) {
        Set<Character> uniqueChars = new HashSet<>();

        FOR_CHARS:
        for (char c : word.toCharArray()) {
            for (char testChar : SPANISH_LOWERCASE_LETTERS) {
                if (c == testChar) {
                    uniqueChars.add(c);
                    continue FOR_CHARS;
                }
            }
            for (char testChar : LLearnConstants.SPANISH_UPPERCASE_LETTERS) {
                if (c == testChar) {
                    uniqueChars.add(c);
                    continue FOR_CHARS;
                }
            }
        }

        int targetKeyNumber = LEARN_CARD_KEYBOARD_COLUMNS;
        if (uniqueChars.size() >= LEARN_CARD_KEYBOARD_COLUMNS - 1) {
            // If we have more than ~4 unique keys in word, then do 2 rows at least
            targetKeyNumber += LEARN_CARD_KEYBOARD_COLUMNS;
        }
        while (uniqueChars.size() > targetKeyNumber) {
            targetKeyNumber += LEARN_CARD_KEYBOARD_COLUMNS;
        }

        int maxIndex = SPANISH_LOWERCASE_LETTERS.length;
        Random rng = new Random(System.currentTimeMillis());

        while (uniqueChars.size() != targetKeyNumber) {
            int index = rng.nextInt(maxIndex);
            uniqueChars.add(SPANISH_LOWERCASE_LETTERS[index]);
        }

        ArrayList<Character> keys = new ArrayList<>(uniqueChars);
        Collections.shuffle(keys);

        return Lists.partition(keys, LEARN_CARD_KEYBOARD_COLUMNS);
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public List<List<Character>> getKeyboardKeys() {
        return keyboardKeys;
    }

    public void setKeyboardKeys(List<List<Character>> keyboardKeys) {
        this.keyboardKeys = keyboardKeys;
    }

    public boolean isReversed() {
        return isReversed;
    }

    public void setReversed(boolean reversed) {
        isReversed = reversed;
    }
}
