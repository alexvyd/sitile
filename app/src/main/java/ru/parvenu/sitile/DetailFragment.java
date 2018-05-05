package ru.parvenu.sitile;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;

public class DetailFragment extends Fragment {
    private static final String ARG_track_ID = "track_id";
    private static final String ARG_best_SHOW = "best_show";
    private static final String ARG_track_LISTPOS = "track_listpos";
    private static final String ARG_track_PAGEPOS = "track_pagepos";
    private static final String EXTRA_track_LISTPOS =
            "ru.parvenu.sitile.track_listpos";
    private static final String EXTRA_track_PAGEPOS =
            "ru.parvenu.android.sitile.track_pagepos";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private Track mTrack;
    private int listpos,pagepos;
    private boolean mBestShow;
    private ImageView mTrackImageBigView;

    public static DetailFragment newInstance(UUID trackId, int listpos, int pagepos, boolean isbest) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_track_ID, trackId);
        args.putInt(ARG_track_LISTPOS, listpos);
        args.putInt(ARG_track_PAGEPOS, pagepos);
        args.putBoolean(ARG_best_SHOW, isbest);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID trackId = (UUID) getArguments().getSerializable(ARG_track_ID);
        listpos = (int) getArguments().getInt(ARG_track_LISTPOS);
        pagepos = (int) getArguments().getInt(ARG_track_PAGEPOS);
        mBestShow = (boolean) getArguments().getBoolean(ARG_best_SHOW);
        mTrack = TrackBase.get(getActivity()).getTrack(trackId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track, container, false);
        mTrackImageBigView = v.findViewById(R.id.item_image_bigview);
        Drawable placeholder = getResources().getDrawable(R.drawable.blank);
        mTrackImageBigView.setImageDrawable(placeholder);
        //загрузка картинки в фоне
        new FetchItemTask().execute();

        String subtitle = mBestShow?getString(R.string.subtitle_track1):getString(R.string.subtitle_track);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

        return v;
    }
    @Override
    public void onPause() {
        super.onPause();
        Intent data = new Intent();
        data.putExtra(EXTRA_track_PAGEPOS, pagepos);
        getActivity().setResult(Activity.RESULT_OK, data);
    }
    @Override //Передача данных во фрагмент
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTrack.setDate(date);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_track, menu);
        MenuItem bestItem = menu.findItem(R.id.best_turn);
        if (mTrack.isBest()) {
            bestItem.setTitle(R.string.best_off);
            bestItem.setIcon(R.drawable.btn_star_big_on);
        } else {
            bestItem.setTitle(R.string.best_on);
            bestItem.setIcon(R.drawable.btn_star_big_off);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close_button:
                getActivity().finish();
                return true;
            case R.id.best_turn:
                mTrack.setBest(!mTrack.isBest());
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class FetchItemTask extends AsyncTask<Void,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            return new FlickrFetchr().fetchItem(mTrack.getPhotoUri());
        }
        @Override
        protected void onPostExecute(Bitmap item) {
            mTrackImageBigView.setImageBitmap(item);
        }
    }
}
