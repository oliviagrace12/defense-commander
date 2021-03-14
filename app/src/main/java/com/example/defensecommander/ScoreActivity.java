package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScoreActivity extends AppCompatActivity {

    TextView topScoresTextView;
    SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullscreen();
        setContentView(R.layout.activity_score);

        ArrayList<ScoreEntry> scoreEntries =
                (ArrayList<ScoreEntry>) getIntent().getExtras().getSerializable("scoreEntries");

        topScoresTextView = findViewById(R.id.scoreEntries);

        StringBuilder stringBuilder = new StringBuilder(getString(R.string.scorecard_title) + "\n");
        int rank = 1;
        for (ScoreEntry scoreEntry : scoreEntries) {
            if (rank < 10) {
                stringBuilder.append(" ").append(rank);
            } else {
                stringBuilder.append(rank);
            }
            stringBuilder.append("\t\t\t")
                    .append(scoreEntry.getInitials()).append("\t\t\t\t")
                    .append(scoreEntry.getLevel()).append("\t\t\t\t")
                    .append(scoreEntry.getScore()).append("\t\t\t\t")
                    .append(simpleDateFormat.format(new Date(scoreEntry.getDateTimeMillis())))
                    .append("\n");
            rank++;
        }

        topScoresTextView.setText(stringBuilder.toString());
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