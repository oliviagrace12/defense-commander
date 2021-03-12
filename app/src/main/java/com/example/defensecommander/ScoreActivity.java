package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        setContentView(R.layout.activity_score);

        ArrayList<ScoreEntry> scoreEntries =
                (ArrayList<ScoreEntry>) getIntent().getExtras().getSerializable("scoreEntries");

        topScoresTextView = findViewById(R.id.scoreEntries);

        StringBuilder stringBuilder = new StringBuilder(R.string.scorecard_title);
        int rank = 1;
        for (ScoreEntry scoreEntry : scoreEntries) {
            stringBuilder.append(rank++).append("\t")
                    .append(scoreEntry.getInitials()).append("\t")
                    .append(scoreEntry.getLevel()).append("\t")
                    .append(scoreEntry.getScore()).append("\t")
                    .append(simpleDateFormat.format(new Date(scoreEntry.getDateTimeMillis())));
        }

        topScoresTextView.setText(stringBuilder.toString());
    }
}