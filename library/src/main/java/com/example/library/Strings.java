package com.example.library;

import android.graphics.Color;

public interface Strings {

    final static String SP_LIBRARY_NAME = "SP_RATING_LIBRARY";
    final static String SP_KEY_LAST_ASK_TIME = "SP_KEY_LAST_ASK_TIME";
    final static String SP_KEY_INIT_TIME = "SP_KEY_INIT_TIME";


    final static String DEFAULT_TITLE = "Rate Our App";
    final static String DEFAULT_SECOND_TITLE = "if you are enjoy using our app, please tell other people";
    final static String DEFAULT_MAYBE_LATER_BTN = "Maybe later";
    final static String DEFAULT_SHOW_RATING = "Show Rating";

    final static String DEFAULT_GO_TO_GOOGLE_PALY = "To Google Play";
    final static String DEFAULT_RATING_BTN ="Rate Now";
    final static String DEFAULT_THANKS_FOR_RATING_US = "Thank you for rating us";
    final static String GOOGLE_SECOND_TITLE = "Would you mind rating us on Google Play";


    final static int DEFAULT_MAIN_COLOR = Color.parseColor("#FDD737");
    final static int NEVER_ASK_AGAIN = -1;
    final static long DEFAULT_TIME_BETWEEN_DIALOG_MS = 1000l * 60 * 60 * 24 * 6; //6 days
    final static long DEFAULT_DELAY_TO_ACTIVATE_MS = 1000l * 60 * 60 * 24 * 3; //3 days
}
