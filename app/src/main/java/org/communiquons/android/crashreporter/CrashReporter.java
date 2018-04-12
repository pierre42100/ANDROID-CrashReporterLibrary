package org.communiquons.android.crashreporter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * Crash Reporter library
 *
 * This library intends to report fatal error to a remote server
 *
 * Licence : the MIT Licence
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/12/18.
 */

public class CrashReporter implements Thread.UncaughtExceptionHandler {

    /**
     * Debug tab
     */
    private static final String TAG = "CrashReporter";

    /**
     * Application context
     */
    private Context mContext;

    /**
     * Current activity
     */
    private Activity mActivity;

    /**
     * Default UncaughtExceptionHandler
     */
    private Thread.UncaughtExceptionHandler defaultUEH;

    /**
     * API URL
     */
    private String mApiURL;

    /**
     * Application key
     */
    private String mAppKey;

    /**
     * Application token
     */
    private String mAppToken;

    /**
     * Construct the library
     *
     * @param activity A valid context (the application context will be stored)
     * @param url The URL where the reports have to be uploaded
     * @param key The application key
     * @param token The application token
     */
    public CrashReporter(Activity activity, String url, String key, String token){

        //Set application context and activity references
        mActivity = activity;
        mContext = activity.getApplicationContext();

        //Save api information
        mApiURL = url;
        mAppKey = key;
        mAppToken = token;

        //Save default thread uncaught exception handler
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

    }

    /**
     * Update the API URL
     *
     * @param ApiURL The new API URL
     */
    public void setApiURL(String ApiURL) {
        this.mApiURL = ApiURL;
    }

    /**
     * Get the current API URL
     *
     * @return The current API URL
     */
    public String getApiURL() {
        return mApiURL;
    }

    /**
     * Update the application key
     *
     * @param appKey The application key
     */
    public void setAppKey(String appKey) {
        this.mAppKey = appKey;
    }

    /**
     * Get the current application key
     *
     * @return The application key
     */
    public String getAppKey() {
        return mAppKey;
    }

    /**
     * Set the application token
     *
     * @param appToken The application token
     */
    public void setAppToken(String appToken) {
        this.mAppToken = appToken;
    }

    /**
     * Get the application token
     *
     * @return The application token
     */
    public String getAppToken() {
        return mAppToken;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        //Generate the report
        String report = generateReport(t, e);
        Log.e(TAG, "Generated report: " + report);

        //Try to upload the report
        if(!CrashUploader.upload(mApiURL, report, mAppKey, mAppToken))
            Log.e(TAG, "Could not upload the report!");

        //Call default exception handler
        this.defaultUEH.uncaughtException(t, e);
    }

    /**
     * Generate the crash report
     *
     * @param t The thread where the exception occurred
     * @param e The exception
     * @return The report as a string
     */
    private String generateReport(Thread t, Throwable e){

        //Begin report
        String report = "Exception: " + e.toString() + "\n\n";

        //Generic information
        report += "Thread name: " + t.getName() + "\n";
        report += "\n";


        //Process stack trace
        report += "---------- Stack trace ----------\n";
        report += stackTraceToString(e.getStackTrace());
        report += "---------------------------------\n\n\n";


        //Process error cause
        report += "------------ Cause --------------\n";
        Throwable cause = e.getCause();
        if(cause != null){
            report += cause.getMessage() + "\n";
            report += stackTraceToString(cause.getStackTrace());
        }
        else
            report += "No data available.\n";
        report += "---------------------------------\n";

        return report;

    }

    /**
     * Turn a stack trace array into a string
     *
     * @param array The array to convert
     * @return Generated string
     */
    private String stackTraceToString(StackTraceElement[] array){
        String string = "";
        for(StackTraceElement el : array){
            string += el.toString() + "\n";
        }
        return string;
    }


}
