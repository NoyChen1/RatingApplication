package com.example.library;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RateUs implements Strings {

    private static userRating userRatingData;
    public interface CallBack_Rating {
        void userRating(float rating);
    }

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserRating");
    private static float userRating = 0;
    private static boolean continueClicked = false;


    public static void Rate(
            final Activity activity, final int color, final int openStoreFromXStars, final String userName, final String password,
            CallBack_Rating callBack_rating)
    {
        Rate(activity,"","","","","","",
                color, openStoreFromXStars,userName, password, -1, -1, callBack_rating);
    }

    public static void Rate(
            final Activity activity, final String title, final String secondTitle, final String rateUsText,
            final String clickHerText, final String maybeLaterText, final String thanksForFeedbackText,
            final int mainColor, final int openStoreFromXStars, final String userName, final String password, final int hoursBetweenCalls, final int hoursDelayToActivate ,
            CallBack_Rating callBack_userRating)
    {

        //initial the dialog text
        final String mainTitle = (title != null && !title.equals("")) ? title: DEFAULT_TITLE;
        final String secondT = (secondTitle != null && !secondTitle.equals("")) ? secondTitle: DEFAULT_SECOND_TITLE;
        final String rateUs = (rateUsText != null && !rateUsText.equals("")) ? rateUsText: DEFAULT_RATING_BTN;
       // final String clickHere = (clickHerText != null && !clickHerText.equals("")) ? clickHerText: DEFAULT_CLICK_HERE_BTN;
        final String mayBeLater = (maybeLaterText != null && !maybeLaterText.equals("")) ? maybeLaterText: DEFAULT_MAYBE_LATER_BTN;
        final String thankYou = (thanksForFeedbackText != null && !thanksForFeedbackText.equals("")) ? thanksForFeedbackText: DEFAULT_THANKS_FOR_RATING_US;


        final long timeBetweenCalls_Ms =
                (hoursBetweenCalls >= 0 && hoursBetweenCalls < 366 * 24) ? 1000l * 60 * 60 * hoursBetweenCalls : DEFAULT_TIME_BETWEEN_DIALOG_MS;
        final long timeDelayToActivate_Ms =
                (hoursDelayToActivate >= 0 && hoursDelayToActivate < 366 * 24) ? 1000l * 60 * 60 * hoursDelayToActivate : DEFAULT_DELAY_TO_ACTIVATE_MS;


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rate_us_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);


       // final RelativeLayout back_LAY = dialogView.findViewById(R.id.back_LAY);
        final Button rate_now_BTN = dialogView.findViewById(R.id.rate_now_BTN);
        final Button maybe_later_BTN = dialogView.findViewById(R.id.maybe_later_BTN);
        //final Button alert_BTN_stop = dialogView.findViewById(R.id.alert_BTN_stop);
        final TextView title_LBL = dialogView.findViewById(R.id.title_LBL);
        final TextView second_title_LBL = dialogView.findViewById(R.id.second_title_LBL);
        final RatingBar rating_bar_RB = dialogView.findViewById(R.id.rating_bar_RB);
        final View primary_color_VIEW = dialogView.findViewById(R.id.primary_color_VIEW);
        final ImageView emoji_IMG = dialogView.findViewById(R.id.emoji_IMG);
        final View emoji_VIEW = dialogView.findViewById(R.id.emoji_VIEW);
        final EditText rating_EditTxt = dialogView.findViewById(R.id.rating_EditTxt);
        final Button show_avg_rating_BTN = dialogView.findViewById(R.id.show_avg_rating_BTN);


        continueClicked = false;
        boolean hideNeverAskAgain = false;


        if (hoursBetweenCalls != -1 && hoursDelayToActivate != -1) {
            // no force asking mode
            long initTime = getInitTime(activity);
            //probably the first usage on this phone
            if (initTime == 0) {
                initTime = System.currentTimeMillis();
                setInitTime(activity, initTime);
            }
            if (System.currentTimeMillis() < initTime + timeDelayToActivate_Ms) {
                return;
            }

            if (getLastAskTime(activity) == 0) {
                // first time asked
                hideNeverAskAgain = true;
            }

            if (getLastAskTime(activity) == NEVER_ASK_AGAIN) {
                // user already rate or click on never ask button
                return;
            }
            if (System.currentTimeMillis() < getLastAskTime(activity) + timeBetweenCalls_Ms) {
                // There was not enough time between the calls.
                return;
            }
        }

        setLastAskTime(activity, System.currentTimeMillis());


        if(mainColor != 0){
            primary_color_VIEW.setBackgroundColor(mainColor);
        }

        title_LBL.setText(mainTitle);
        second_title_LBL.setText(secondT);

        rate_now_BTN.setText(rateUs);
        rate_now_BTN.setEnabled(false);
        maybe_later_BTN.setText(mayBeLater);

        rating_bar_RB.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

                if(rating <= 1){
                    emoji_IMG.setImageResource(R.drawable.one_stars_emoji);
                } else if (rating <= 2) {
                    emoji_IMG.setImageResource(R.drawable.two_stars_emoji);
                }else if (rating <= 3){
                    emoji_IMG.setImageResource(R.drawable.three_stars_emoji);
                } else if (rating <= 4) {
                    emoji_IMG.setImageResource(R.drawable.four_stars_emoji);
                } else if (rating <=5) {
                    emoji_IMG.setImageResource(R.drawable.five_stars_emoji);
                }

                userRating = rating;
                rate_now_BTN.setEnabled(true);
                rate_now_BTN.setText(userRating + "/5\n" + rateUs);
            }

        });


        maybe_later_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        /*
        show_avg_rating_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                second_title_LBL.setText("our rating on google play is: " + getData(activity) + " stars");
            }
        });
         */


        rate_now_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int openStoreFrom_Stars = openStoreFromXStars;
                if (openStoreFromXStars < 1 || openStoreFromXStars > 5) {
                    openStoreFrom_Stars = 4;
                }

                if (continueClicked) {
                    setLastAskTime(activity, NEVER_ASK_AGAIN);

                    if (userRating >= openStoreFrom_Stars) {
                        String review = rating_EditTxt.getText() + "";
                        addDataToFirebase(userName, password, (int)userRating, review, activity);
                        launchMarket(activity);
                    } else {
                        Toast.makeText(activity, thankYou, Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                } else {
                    if (openStoreFromXStars != -1 && userRating >= openStoreFrom_Stars) {
                        continueClicked = true;
                        title_LBL.setVisibility(View.GONE);
                        emoji_IMG.setImageResource(R.drawable.google_play_icon);
                        rating_bar_RB.setVisibility(View.GONE);
                        rating_EditTxt.setVisibility(View.VISIBLE);
                        second_title_LBL.setText(GOOGLE_SECOND_TITLE);
                        rate_now_BTN.setText(DEFAULT_GO_TO_GOOGLE_PALY);
                        show_avg_rating_BTN.setVisibility(View.VISIBLE);

                    } else {
                        alertDialog.dismiss();
                     //   Toast.makeText(activity,"Rating: " + userRating + " Stars", Toast.LENGTH_SHORT).show();
                        Toast.makeText(activity, thankYou, Toast.LENGTH_SHORT).show();
                    }
                }

                if (callBack_userRating != null) {
                    callBack_userRating.userRating(userRating);
                }

            }
        });

        alertDialog.show();

    }


    private static void launchMarket(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToGooglePlay = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToGooglePlay);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, " unable to find google play app", Toast.LENGTH_LONG).show();
        }
    }



    private static long getLastAskTime(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SP_LIBRARY_NAME, Context.MODE_PRIVATE);
        long val = sharedPreferences.getLong(SP_KEY_LAST_ASK_TIME, 0);
        return val;
    }

    private static void setLastAskTime(Activity activity, long time) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(SP_LIBRARY_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(SP_KEY_LAST_ASK_TIME, time);
        editor.apply();
    }

    private static long getInitTime(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SP_LIBRARY_NAME, Context.MODE_PRIVATE);
        long val = sharedPreferences.getLong(SP_KEY_INIT_TIME, 0);
        return val;
    }

    private static void setInitTime(Activity activity, long time) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(SP_LIBRARY_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(SP_KEY_INIT_TIME, time);
        editor.apply();
    }

    private static void addDataToFirebase(String userName, String password, int rating, String review, Activity activity){
        userRatingData = new userRating(userName, password, review, rating);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(userRatingData);
                Toast.makeText(activity, "data added", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, "fail to add data", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    private static String getData (Activity activity){
        String rating = "";
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}
