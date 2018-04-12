package org.communiquons.android.crashreporter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
     * Connexion timeout
     */
    private static final int API_CONNEXION_TIMEOUT = 3000;

    /**
     * Method used to connect to the api
     */
    private static final String API_CONNEXION_METHOD = "POST";

    /**
     * Report file name
     */
    private static final String REPORT_FILENAME = "crash_report.txt";

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
        if(!save_report(report))
            Log.e(TAG, "Could not save the report!");
        else
            Log.v(TAG, "Report successfully saved for later upload.");

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
     * Push online any awaiting report
     */
    public void uploadAwaitingReport(){

        //Get the report file
        File file = get_report_file(false);

        //Check if the file exists or not
        if(file == null){
            Log.v(TAG, "Report file seems not to exists.");
            return;
        }

        //Delete the awaiting report
        if(!file.delete()){

        }

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

    /**
     * Intend to upload the report online
     *
     * @param report The report to upload
     * @return TRUE in case of success / FALSE else
     */
    public boolean upload(String report){

        try {

            //Prepare the request body
            String requestBody = "key=" + URLEncoder.encode(mAppKey, "UTF-8") +
                    "&token="+ URLEncoder.encode(mAppToken, "UTF-8")
                    + "&report=" + URLEncoder.encode(report, "UTF-8");

            //Prepare the connexion
            URL url = new URL(mApiURL);

            //Open URL connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Setup a few settings
            conn.setRequestMethod(API_CONNEXION_METHOD);
            conn.setDoOutput(true);
            conn.setDoInput(false);
            conn.setConnectTimeout(API_CONNEXION_TIMEOUT);
            conn.setChunkedStreamingMode(0);

            //Connect to the server
            conn.connect();

            //Write report
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(requestBody);
            writer.flush();
            writer.close();
            os.close();

            conn.disconnect();

            //Success
            return true;

        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save the report locally for ulterior upload
     *
     * @param report The report to save
     * @return TRUE for a success / FALSE else
     */
    private boolean save_report(String report){

        //Get the file
        File file = get_report_file(true);

        //Check for error
        if(file == null){
            Log.e(TAG, "Could not create report file!");
            return false;
        }

        try {

            //Open the file for writing
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file, false));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(report);
            writer.flush();
            writer.close();
            os.close();


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //Success
        return true;
    }

    /**
     * Get the saved report file
     *
     * @param create Create the file if does not exists
     * @return The report file (null in case of failure)
     */
    @Nullable
    private File get_report_file(boolean create){
        File file = new File(mContext.getCacheDir(), REPORT_FILENAME);

        //Check file existence
        if(!file.exists()){

            //Check if the file can be created
            if(create) {
                try {
                    //Intend to create the file
                    if (!file.createNewFile())
                        return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else
                return null;
        }

        return file;
    }
}
