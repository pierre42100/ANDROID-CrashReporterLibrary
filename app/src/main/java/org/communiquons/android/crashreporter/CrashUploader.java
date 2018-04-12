package org.communiquons.android.crashreporter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Uploader
 *
 * This class handles the upload of the crash reports
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/12/18.
 */

class CrashUploader {

    /**
     * Debug tag
     */
    private static final String TAG = "CrashUploader";

    /**
     * Intend to upload the report online
     *
     * @param apiURL The target URL for the report
     * @param report The report to upload
     * @param appKey The application key
     * @param appToken The application token
     * @return TRUE in case of success / FALSE else
     */
    static boolean upload(String apiURL, String report, String appKey, String appToken){

        try {

            //Prepare the connexion
            URL url = new URL(apiURL);

            //Open URL connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();



            //Success
            return true;

        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }


    }

}
