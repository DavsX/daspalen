package com.github.davsx.daspalen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DaspalenConstants {
    public static final Integer MAX_CARD_LEARN_SCORE = 8;

    public static final Integer LEARN_SESSION_MAX_CARDS = 10;
    public static final Integer LEARN_SESSION_MAX_ROUNDS = 20;
    public static final Integer LEARN_SESSION_MAX_NEW_CARDS = 5;
    public static final Integer LEARN_SESSION_CANDIDATE_CARDS =
            Double.valueOf(1.5 * LEARN_SESSION_MAX_CARDS).intValue();
    public static final Integer LEARN_CARD_KEYBOARD_COLUMNS = 5;
    public static final Integer LEARN_SESSION_RANDOM_CARDS_COUNT = 400;

    public static final Integer REVIEW_SESSION_CANDIDATE_CARDS = 200;
    public static final Integer REVIEW_SESSION_MAX_CARDS = 15;

    public static final Integer REVIEW_CARD_MAX_BAD_ANSWERS = 5;
    public static final Double REVIEW_CARD_MIN_EASINESS_FACTOR = 1.3;
    public static final Double REVIEW_CARD_MAX_EASINESS_FACTOR = 2.5;

    public static final char[] SPANISH_LOWERCASE_LETTERS = "abcdefghijklmnñopqrstuvwxyzáéíñóúü".toCharArray();

    public static final String PKG_SPANISHDICT = "com.spanishdict.spanishdict";
    public static final String PKG_GOOGLE_TRANSLATE = "com.google.android.apps.translate";
    public static final String PKG_REVERSO_CONTEXT = "com.softissimo.reverso.context";


    public static final Integer REQUEST_CODE_CREATE_DOCUMENT = 1;
    public static final Integer REQUEST_CODE_OPEN_DOCUMENT = 2;

    public static final Long ONE_DAY_MILLIS = (long) (24 * 3600 * 1000);

    public static final String REVIEW_ANSWER_WRONG = "WRONG";
    public static final String REVIEW_ANSWER_OK = "OK";
    public static final String REVIEW_ANSWER_GOOD = "GOOD";

    public static final Integer CARD_TYPE_INCOMPLETE = 0;
    public static final Integer CARD_TYPE_LEARN = 1;
    public static final Integer CARD_TYPE_REVIEW = 2;

    public static final List<Integer> CARD_TYPES_ALL = new ArrayList<>(
            Arrays.asList(CARD_TYPE_INCOMPLETE, CARD_TYPE_LEARN, CARD_TYPE_REVIEW));
    public static final String CARD_NOTIFICATION_CHANNEL = "DASPALEN_CARD_NOTIFICATION_CHANNEL";

    public static final String OKHTTP_TAG = "DASPALEN_OK_HTTP_TAG";
}