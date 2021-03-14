package com.example.defensecommander;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class EnterScoreDatabaseHandler implements Runnable {

    private static final String TAG = "EnterScoreDatabaseHandl";

    private MainActivity mainActivity;
    private Connection connection;
    private ScoreEntry scoreEntry;

    public EnterScoreDatabaseHandler(MainActivity mainActivity, ScoreEntry scoreEntry) {
        this.mainActivity = mainActivity;
        this.scoreEntry = scoreEntry;
    }

    @Override
    public void run() {
        try {
            Class.forName(mainActivity.getString(R.string.jdbc_driver));
            connection = DriverManager.getConnection(
                    mainActivity.getString(R.string.db_url),
                    mainActivity.getString(R.string.db_user),
                    mainActivity.getString(R.string.db_pass));

            String query = "INSERT INTO AppScores VALUES ("
                    + scoreEntry.getDateTimeMillis()
                    + ",'" + scoreEntry.getInitials() + "',"
                    + scoreEntry.getScore() + "," + scoreEntry.getLevel() + ")";

            Log.i(TAG, "Insert statement: " + query);

            Statement statement = connection.createStatement();
            int updated = statement.executeUpdate(query);

            Log.i(TAG, "Insert completed. Rows updated: " + updated);

            mainActivity.runOnUiThread(() ->
                    new Thread(new TopTenScoreDatabaseHandler(mainActivity))
                            .start());

        } catch (ClassNotFoundException | SQLException e) {
            Log.e(TAG, "run: " + e.getLocalizedMessage());
        }

    }
}
