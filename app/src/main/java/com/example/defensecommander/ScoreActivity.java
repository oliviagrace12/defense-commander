package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScoreActivity extends AppCompatActivity {

    TextView titleTextView;
    TextView topScoresTextView;
    SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullscreen();
        setContentView(R.layout.activity_score);

        ArrayList<ScoreEntry> scoreEntries =
                (ArrayList<ScoreEntry>) getIntent().getExtras().getSerializable("scoreEntries");

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/RobotoMono-VariableFont_wght.ttf");

        topScoresTextView = findViewById(R.id.scoreEntries);
        titleTextView = findViewById(R.id.scoreTitle);
        topScoresTextView.setTypeface(customFont);
        titleTextView.setTypeface(customFont);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#").append("   ");
        stringBuilder.append("Init").append("  ");
        stringBuilder.append("Level").append("  ");
        stringBuilder.append("Score").append("  ");
        stringBuilder.append("Date/Time").append("\n");
        int rank = 1;
        for (ScoreEntry scoreEntry : scoreEntries) {
            if (rank < 10) {
                stringBuilder.append(" ").append(rank);
            } else {
                stringBuilder.append(rank);
            }
            stringBuilder.append("  ")
                    .append(getPaddedInitials(scoreEntry.getInitials())).append("   ")
                    .append(getPaddedNumber(scoreEntry.getLevel())).append("  ")
                    .append(scoreEntry.getScore()).append("  ")
                    .append(simpleDateFormat.format(new Date(scoreEntry.getDateTimeMillis())))
                    .append("\n");
            rank++;
        }

        topScoresTextView.setText(stringBuilder.toString());
    }

    private String getPaddedNumber(int num) {
        if (num < 10) {
            return num + "    ";
        } else if (num < 100) {
            return num + "   ";
        } else if (num < 1000) {
            return num + "  ";
        } else {
            return num + "";
        }
    }

    private String getPaddedInitials(String initials) {
        if (initials == null) {
            return "   ";
        }
        int lenth = initials.length();
        switch (lenth) {
            case 1:
                return "  " + initials;
            case 2:
                return " " + initials;
            case 3:
                return initials;
            default:
                return "   ";
        }
    }

    private void makeFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void closeApp(View view) {
        finish();
    }
}