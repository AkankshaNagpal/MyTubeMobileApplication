package com.example.akankshanagpal.mytube.ApplicationFragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.akankshanagpal.mytube.R;
import com.example.akankshanagpal.mytube.Video;
import com.example.akankshanagpal.mytube.YouTubeIntegration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akankshanagpal on 10/18/15.
 */
public class FavoriteFragment extends Fragment {

    FavoriteFragmentListener favoriteFragmentListener;

    public interface  FavoriteFragmentListener {

        public void favoriteResultSelected(String videoId);
        public void modifyFavorites();
    }


    View rootView;
    private Handler handler;
    private Handler deletionHandler;
    private ArrayList<Video> searchResults;
    int selectedIndex;
    String removeFavoritesResponseCode = "-1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        handler = new Handler();
        deletionHandler = new Handler();

        addClickListener();

        return rootView;
    }

    private void addClickListener(){

        ListView favoriteVideos = (ListView)rootView.findViewById(R.id.favorite_videos);
        favoriteVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {

                System.out.println("onItemClick Adapter View Favorite fragment");
                String videoId = searchResults.get(pos).getId();

                favoriteFragmentListener.favoriteResultSelected(videoId);
            }

        });
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        favoriteFragmentListener = (FavoriteFragmentListener)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {

        super.onStart();

        getFavorites();
    }


    private void getFavorites() {

        new FavoriteTask().execute();
    }

    private void updateVideoInSearchResults(List<Video> videoList) {

        ArrayAdapter<Video> adapter = new ArrayAdapter<Video>(getActivity().getApplicationContext(), R.layout.search_item, videoList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if(convertView == null) {

                    convertView = getActivity().getLayoutInflater().inflate(R.layout.search_item, parent, false);
                }

                Video searchResult = searchResults.get(position);

                ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.video_title);
                TextView publishedDate = (TextView)convertView.findViewById(R.id.publishedDate);
                TextView numberOfViews = (TextView)convertView.findViewById(R.id.numberOfViews);
                Button starButton = (Button)convertView.findViewById(R.id.star);
                starButton.setTag(position);

                starButton.setBackgroundResource(android.R.drawable.star_on);

                starButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        selectedIndex = (int)v.getTag();

                        Video selectedVideo = searchResults.get(selectedIndex);

                        removeFavoritesResponseCode = "-1";
                        new RemoveFavoritesTask().execute(selectedVideo.getPlaylistId());
                    }
                });

                Picasso.with(getActivity().getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                publishedDate.setText(searchResult.getPublishedDate());
                numberOfViews.setText(searchResult.getNumberOfViews());

                return convertView;
            }
        };

        ListView favoriteVideos = (ListView)rootView.findViewById(R.id.favorite_videos);
        favoriteVideos.setAdapter(adapter);
    }

    private void updateVideoInSearchResults(Boolean isFavorite) {

        searchResults.remove(selectedIndex);

        updateVideoInSearchResults(searchResults);
    }




    public class FavoriteTask extends AsyncTask<String, String, ArrayList<Video>> {

        @Override
        protected ArrayList<Video> doInBackground(String... keyword) {

            try {

                searchResults = YouTubeIntegration.getFavorites();
            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> items) {

            updateVideoInSearchResults(searchResults);
        }
    }


    private class RemoveFavoritesTask extends AsyncTask <String , Void, String> {

        @Override
        protected String doInBackground(String... videoId) {

            try {

                removeFavoritesResponseCode = YouTubeIntegration.removeFavorites(videoId[0]);
            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String responseCode) {

            if (Integer.parseInt(removeFavoritesResponseCode) != -1) {

                updateVideoInSearchResults(false);
            }
        }
    }
}
