package com.example.akankshanagpal.mytube;

/**
 * Created by akankshanagpal on 10/18/15.
 */
public class ApplicationConfiguration {

    private static ApplicationConfiguration appConfig = null;

    private String accessToken;
    private String favoritePlaylistId;

    private ApplicationConfiguration() {

        accessToken = "";
        favoritePlaylistId = "";
    }

    public static ApplicationConfiguration getAppConfig() {
        if (appConfig == null) {

            appConfig = new ApplicationConfiguration();
        }
        return appConfig;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getFavoritePlaylistId() {
        return favoritePlaylistId;
    }

    public void setFavoritePlaylistId(String favoritePlaylistId) {
        this.favoritePlaylistId = favoritePlaylistId;
    }



}
