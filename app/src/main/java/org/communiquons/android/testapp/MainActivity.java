package org.communiquons.android.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.communiquons.android.crashreporter.CrashReporter;
import org.communiquons.android.crashreporter.R;

/**
 * Library test application
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Debug tag
     */
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Make the button lives
        findViewById(R.id.button).setOnClickListener(this);

        //Init the library
        CrashReporter reporter = new CrashReporter(this, "http://192.168.1.9:5695/",
                "content_key", "content_token");

    }

    @SuppressWarnings("NumericOverflow")
    @Override
    public void onClick(View v) {

        //Do a basic operation that will make the operation crash
        int value = R.id.button/0;
        Log.v(TAG, "Value:" + value);

    }
}