package com.example.akankshanagpal.mytube;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.akankshanagpal.mytube.ApplicationFragments.SearchFragment;
import com.example.akankshanagpal.mytube.ApplicationFragments.FavoriteFragment;

public class MyTubeHomeActivity extends AppCompatActivity
        implements SearchFragment.SearchFragmentListener, FavoriteFragment.FavoriteFragmentListener {

    private FragmentTabHost tabHost;

    private String playlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tube_home);

        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);

        tabHost.addTab(
                tabHost.newTabSpec("Search")
                        .setIndicator(getTabIndicator(tabHost.getContext(), R.string.search, android.R.drawable.ic_menu_search)),
                SearchFragment.class, null);
        tabHost.addTab(
                tabHost.newTabSpec("Favorite")
                        .setIndicator(getTabIndicator(tabHost.getContext(), R.string.favorites, android.R.drawable.star_on)),
                FavoriteFragment.class, null);

        new GetFavoritePlaylistTask().execute(ApplicationParams.PLAYLIST_NAME);
    }

    private View getTabIndicator(Context context, int title, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        iv.setImageResource(icon);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logout_button:

                super.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

    }


    @Override
    public void searchResultSelection(String videoId) {

        loadVideo(videoId);
    }

    @Override
    public void addVideoToFavorites() {

//        FavoriteFragment favoriteFragment = (FavoriteFragment) tabsPagerAdapter.getFragmentAtPosition(1);
//        favoriteFragment.favoritesModified();
    }

    @Override
    public void favoriteResultSelected(String videoId) {

        loadVideo(videoId);
    }

    @Override
    public void modifyFavorites () {

    }


    private void loadVideo (String videoId) {

        try {
            System.out.println("Selected video Id "+videoId);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            intent.putExtra("force_fullscreen",true);
            startActivity(intent);
        } catch (ActivityNotFoundException ex){

            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+videoId));
            startActivity(intent);
        }
    }




    private class GetFavoritePlaylistTask extends AsyncTask<String , Void, String> {

        @Override
        protected String doInBackground(String... playlistName) {

            try {

                playlistId = YouTubeIntegration.getFavoritePlaylist(playlistName[0]);
            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String responseCode) {

            ApplicationConfiguration.getAppConfig().setFavoritePlaylistId(playlistId);
        }
    }
}
