package com.example.defensecommander;

import android.content.Intent;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TopTenScoreDatabaseHandler implements Runnable {

    private static final String TAG = "ScoreDatabaseHandler";

    private MainActivity mainActivity;
    private Connection connection;

    public TopTenScoreDatabaseHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try {
            Class.forName(mainActivity.getString(R.string.jdbc_driver));
            connection = DriverManager.getConnection(
                    mainActivity.getString(R.string.db_url),
                    mainActivity.getString(R.string.db_user),
                    mainActivity.getString(R.string.db_pass));

            String query = "SELECT * FROM AppScores ORDER BY Score DESC LIMIT 10";
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<ScoreEntry> scoreEntries = new ArrayList<>();
            while (resultSet.next()) {
                long dateTimeMillis = resultSet.getLong("DateTime");
                String initials = resultSet.getString("Initials");
                int score = resultSet.getInt("Score");
                int level = resultSet.getInt("Level");
                scoreEntries.add(new ScoreEntry(dateTimeMillis, initials, score, level));
            }

            mainActivity.runOnUiThread(() -> mainActivity.openScoresActivity(scoreEntries));
        } catch (ClassNotFoundException | SQLException e) {
            Log.e(TAG, "run: " + e.getLocalizedMessage());
        }
    }
}
