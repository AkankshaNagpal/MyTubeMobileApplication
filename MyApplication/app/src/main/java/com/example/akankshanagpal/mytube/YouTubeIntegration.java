package com.example.akankshanagpal.mytube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.client.util.Joiner;

import com.example.akankshanagpal.mytube.ApplicationParams;

/**
 * Created by akankshanagpal on 10/18/15.
 */
public class YouTubeIntegration {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    public static ArrayList<Video> searchVideoWithKeywords(String keywords) throws Exception {


        String searchVideoURL = ApplicationParams.BASE_URL+ApplicationParams.SEARCH_PATH;

        StringBuilder searchVideoURLBuilder = new StringBuilder();
        searchVideoURLBuilder.append(ApplicationParams.PART).append("="+"id,snippet");
        searchVideoURLBuilder.append("&").append(ApplicationParams.MAX_RESULTS).append("="+NUMBER_OF_VIDEOS_RETURNED);
        searchVideoURLBuilder.append("&").append(ApplicationParams.KEYWORD).append("=").append(keywords);
        searchVideoURLBuilder.append("&").append(ApplicationParams.TYPE).append("=").append("video");

        String searchRequestParams = searchVideoURLBuilder.toString();
        ArrayList <String> searchVideoResponse = ConnectionUtility
                .fetchResponse(searchVideoURL, searchRequestParams, ApplicationConfiguration.getAppConfig().getAccessToken());

        JSONObject searchVideosJSON = new JSONObject(searchVideoResponse.get(0));

        Map<String, Object> searchVideoMap = toMap(searchVideosJSON);

        ArrayList <Object> searchedVideoItems = new ArrayList<Object>();
        searchedVideoItems.addAll((Collection<?>) searchVideoMap.get("items"));

        ArrayList<Video> pItems = new ArrayList<Video>();
        ArrayList <Video> playlistItemList = getVideosInFavorites();

        for (int i = 0; i < searchedVideoItems.size(); i++) {

            Video video = new Video();

            HashMap videoIdMap = ((HashMap)((HashMap)searchedVideoItems.get(i)).get("id"));

            Map<String, Object> videoMap = getVideoAttributes((String) videoIdMap.get("videoId"));

            ArrayList <Object> videoItems = new ArrayList<Object>();
            videoItems.addAll((Collection<?>) videoMap.get("items"));

            HashMap snippetMap = ((HashMap) ((HashMap) videoItems.get(0)).get("snippet"));
            HashMap statisticsMap = ((HashMap) ((HashMap) videoItems.get(0)).get("statistics"));

            video.setId((String) ((HashMap) videoItems.get(0)).get("id"));
            video.setPublishedDate((String) snippetMap.get("publishedAt"));
            video.setTitle((String) snippetMap.get("title"));
            video.setThumbnailURL((String) ((HashMap) (((HashMap) snippetMap.get("thumbnails"))).get("default")).get("url"));
            String playlistId = getPlaylistId(playlistItemList, video.getId());
            video.setPlaylistId(playlistId);
            video.setFavorite((playlistId != "0"));
            video.setNumberOfViews((String) statisticsMap.get("viewCount"));

            pItems.add(video);
        }

        return pItems;
    }

    private static Map<String, Object> getVideoAttributes (String id) throws JSONException {

        /**
         * https://www.googleapis.com/youtube/v3/videos?part=snippet&id=yzTuBuRdAyA
         */
        List<String> videoIds = new ArrayList<String>();
        videoIds.add(id);
        Joiner stringJoiner = Joiner.on(',');
        String videoId = stringJoiner.join(videoIds);

        String videoURL = ApplicationParams.BASE_URL+ApplicationParams.GET_VIDEO;

        StringBuilder videoURLBuilder = new StringBuilder();
        videoURLBuilder.append(ApplicationParams.PART).append("="+ApplicationParams.SNIPPET+","+ApplicationParams.STATISTICS);
        videoURLBuilder.append("&").append(ApplicationParams.ID).append("="+videoId);

        String videoRequestParams = videoURLBuilder.toString();
        ArrayList <String> videoResponse = ConnectionUtility
                .fetchResponse(videoURL, videoRequestParams, ApplicationConfiguration.getAppConfig().getAccessToken());

        JSONObject videoJSON = new JSONObject(videoResponse.get(0));

        Map<String, Object> videoMap = toMap(videoJSON);

        return videoMap;
    }

    public static ArrayList <Video> getFavorites() throws JSONException {

        ArrayList <Video> playlistItemList = getVideosInFavorites();

        for (int i = 0; i < playlistItemList.size(); i++) {

           Video video = playlistItemList.get(i);

            Map<String, Object> videoMap = getVideoAttributes(video.getId());

            ArrayList <Object> videoItems = new ArrayList<Object>();
            videoItems.addAll((Collection<?>) videoMap.get("items"));

            HashMap snippetMap = ((HashMap) ((HashMap) videoItems.get(0)).get(ApplicationParams.SNIPPET));
            HashMap statisticsMap = ((HashMap) ((HashMap) videoItems.get(0)).get(ApplicationParams.STATISTICS));

            video.setPublishedDate((String) snippetMap.get("publishedAt"));
            video.setTitle((String) snippetMap.get("title"));
            video.setThumbnailURL((String) ((HashMap) (((HashMap) snippetMap.get("thumbnails"))).get("default")).get("url"));
            video.setFavorite(true);
            video.setNumberOfViews((String) statisticsMap.get("viewCount"));

            playlistItemList.set(i, video);
        }

        return playlistItemList;
    }

    private static ArrayList <Video> getVideosInFavorites () throws JSONException {

        /**
         * https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=PLvHlrhuuRjgWjcspwO0ZapC42l-QKSHmU
         */
        String getPlayListItemsURL = ApplicationParams.BASE_URL+ApplicationParams.PLAYLISTITEMS;

        StringBuilder getPlayListItemsURLBuilder = new StringBuilder();
        getPlayListItemsURLBuilder.append(ApplicationParams.PART).append("="+"snippet");
        getPlayListItemsURLBuilder.append("&").append("playlistId").append("=" + ApplicationConfiguration.getAppConfig().getFavoritePlaylistId());

        String playlistItemsParams = getPlayListItemsURLBuilder.toString();
        ArrayList <String> playlistItemsResponse = ConnectionUtility
                .fetchResponse(getPlayListItemsURL, playlistItemsParams, ApplicationConfiguration.getAppConfig().getAccessToken());

        JSONObject playlistItemsJSON = new JSONObject(playlistItemsResponse.get(0));

        Map<String, Object> playlistItemsMap = toMap(playlistItemsJSON);

        ArrayList <Object> playlistVideoItems = new ArrayList<Object>();
        playlistVideoItems.addAll((Collection<?>) playlistItemsMap.get("items"));

        ArrayList <Video> favoritePlayList = new ArrayList<Video>();

        for (int i = 0; i < playlistVideoItems.size(); i++) {

            Video video = new Video();

            HashMap snippetMap = ((HashMap)((HashMap) playlistVideoItems.get(i)).get("snippet"));
            video.setId((String) ((HashMap) snippetMap.get("resourceId")).get("videoId"));
            video.setPlaylistId(((String) ((HashMap) playlistVideoItems.get(i)).get("id")));
            favoritePlayList.add(video);
        }

        return favoritePlayList;
    }

    public static String getFavoritePlaylist(String playlistName) throws JSONException {

        String getPlayListItemsURL = ApplicationParams.BASE_URL+ApplicationParams.PLAYLISTS;

        StringBuilder getPlayListURLBuilder = new StringBuilder();
        getPlayListURLBuilder.append(ApplicationParams.PART).append("="+ApplicationParams.ID);
        getPlayListURLBuilder.append(",").append("snippet");
        getPlayListURLBuilder.append("&").append(ApplicationParams.MINE).append("="+"true");

        String playlistParams = getPlayListURLBuilder.toString();
        ArrayList <String> playlistResponse = ConnectionUtility
                .fetchResponse(getPlayListItemsURL, playlistParams, ApplicationConfiguration.getAppConfig().getAccessToken());

        JSONObject playlistJSON = new JSONObject(playlistResponse.get(0));

        Map<String, Object> playlistMap = toMap(playlistJSON);

        ArrayList <Object> playlistList = new ArrayList<Object>();
        playlistList.addAll((Collection<?>) playlistMap.get("items"));

        String playlistId = (String)((HashMap) playlistList.get(0)).get("id");
        return playlistId;
    }



    public static String inserttoFavorites(Video video) {

        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(video.getId());

        PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
        playlistItemSnippet.setTitle(video.getTitle());
        playlistItemSnippet.setPlaylistId(ApplicationConfiguration.getAppConfig().getFavoritePlaylistId());
        playlistItemSnippet.setResourceId(resourceId);

        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.setSnippet(playlistItemSnippet);

        JSONObject insertPlaylistItemRequestBody = new JSONObject(playlistItem);

        String insertPlayListItemsURL = ApplicationParams.BASE_URL+ApplicationParams.PLAYLISTITEMS;

        StringBuilder insertPlayListItemsURLBuilder = new StringBuilder();
        insertPlayListItemsURLBuilder.append(ApplicationParams.PART).append("="+"snippet");
        insertPlayListItemsURLBuilder.append(",").append("contentDetails");
        String insertPlaylistItemsParams = insertPlayListItemsURLBuilder.toString();

        String responseCode = ConnectionUtility.postRequest(insertPlayListItemsURL, insertPlaylistItemsParams,
                insertPlaylistItemRequestBody.toString(), ApplicationConfiguration.getAppConfig().getAccessToken(), true);

        return responseCode;
    }

    public static String removeFavorites(String videoId) {

        /**
         * https://www.googleapis.com/youtube/v3/playlistItems?id=PLi_rXDdOef2RhLGgX4nvVjnvJpHPAdX0sp1MDqaKUFDo
         */
        String deletePlayListItemsURL = ApplicationParams.BASE_URL+ApplicationParams.PLAYLISTITEMS;

        StringBuilder deletePlayListItemsURLBuilder = new StringBuilder();
        deletePlayListItemsURLBuilder.append(ApplicationParams.ID).append("="+videoId);
        String insertPlaylistItemsParams = deletePlayListItemsURLBuilder.toString();

        String responseCode = ConnectionUtility.postRequest(deletePlayListItemsURL, insertPlaylistItemsParams,
                "", ApplicationConfiguration.getAppConfig().getAccessToken(), false);

        return responseCode;
    }

    private static String getPlaylistId (ArrayList<Video> playlistItemList, String videoId) {

        String playlistId = "0";
        for(Video video:playlistItemList) {

            if (video.getId().equals(videoId)) {

                playlistId = video.getPlaylistId();
                break;
            }
        }

        return playlistId;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


}
