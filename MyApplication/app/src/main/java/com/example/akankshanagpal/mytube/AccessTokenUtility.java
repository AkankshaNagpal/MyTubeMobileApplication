package com.example.akankshanagpal.mytube;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

/**
 * Created by akankshanagpal on 10/18/15.
 */
public class AccessTokenUtility extends AsyncTask{

    GoogleConnectionUtilProtocol delegate;

    public interface  GoogleConnectionUtilProtocol {

        public void isTokenGenerated(String accessToken);
        public void isExceptionCaught(Exception e);
        public Activity getActivity();
    }


    private final static String YOUTUBE_API_SCOPE
            = "https://www.googleapis.com/auth/youtube";
    private final static String mScopes
            = "oauth2:" + YOUTUBE_API_SCOPE;

    String Email;

    public AccessTokenUtility (Activity activity, String name) {

        this.Email = name;
        delegate = (GoogleConnectionUtilProtocol) activity;
    }

    private String getAccessToken() throws IOException {

        try {

            return GoogleAuthUtil.getToken(delegate.getActivity(), Email, mScopes);
        } catch (UserRecoverableAuthException userRecoverableException) {

            delegate.isExceptionCaught(userRecoverableException);
        } catch (GoogleAuthException fatalException) {

            delegate.isExceptionCaught(fatalException);
        }
        return null;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            String accessToken = getAccessToken();
            //String accessToken = "ya29.EQLxT_6q60Kn7F8ZTw4jUqnoJMikGyGJgzB41tRy1bP28_DKqvLSc4uHNobMUQVY18qr";
            if (accessToken != null) {

                System.out.println("Access Token "+accessToken);
                delegate.isTokenGenerated(accessToken);
            }
        } catch (Exception e) {

        }
        return null;
    }



}
