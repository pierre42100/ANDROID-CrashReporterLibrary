package org.communiquons.android.crashreporter;

import android.content.Context;

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

public class CrashReporter {

    /**
     * Application context
     */
    private Context mContext;

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
     * @param context A valid context (the application context will be stored)
     * @param url The URL where the reports have to be uploaded
     * @param key The application key
     * @param token The application token
     */
    public CrashReporter(Context context, String url, String key, String token){

        //Set application context
        mContext = context.getApplicationContext();

        //Save api information
        mApiURL = url;
        mAppKey = key;
        mAppToken = token;

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


}
