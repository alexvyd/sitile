package ru.parvenu.sitile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";
    private RecyclerView mTrackRecyclerView;
    private trackAdapter mAdapter;
    private boolean mSubtitleVisible;
    private static final int REQUEST_TRACK = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    //private TextView mNotracksTextView;
    //private Button mAddtrackButton;
    private List<Track> tracks = new ArrayList<>();
    private PicLoader<trackHolder> mPicLoader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();

        mPicLoader = new PicLoader<>(responseHandler);
        mPicLoader.setPicLoadListener(
                new PicLoader.PicLoadListener<trackHolder>() {
                    @Override
                    public void onPicLoaded(trackHolder photoHolder,
                                                      Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindimage(drawable);
                    }
                }
        );
        mPicLoader.start();
        mPicLoader.getLooper();
        Log.i(TAG, "Background thread started");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container,
                false);

        mTrackRecyclerView = (RecyclerView) view.findViewById(R.id.track_recycler_view);

        //mTrackRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrackRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean
                    (SAVED_SUBTITLE_VISIBLE);
        }
        //Надпись нет треков и кнопка добавить
        /*mNotracksTextView = (TextView) view.findViewById(R.id.track_nolist);
        mAddtrackButton = (Button) view.findViewById(R.id.track_add_button);
        mAddtrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTrack();
            }
        });*/
        updateUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPicLoader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPicLoader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        //TrackBase trackBase = TrackBase.get(getActivity());
        //List<Track> tracks = trackBase.gettracks();

        if (mAdapter == null) {
            mAdapter = new trackAdapter(tracks);
            if (isAdded()) {
                mTrackRecyclerView.setAdapter(mAdapter);
            }
        } else {
            mAdapter.setTracks(tracks);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mCurPos);
        }
        /*if(tracks.size()>0){
            mNotracksTextView.setVisibility(View.INVISIBLE);
            mAddtrackButton.setVisibility(View.INVISIBLE);
        }
        else {
            mNotracksTextView.setVisibility(View.VISIBLE);
            mAddtrackButton.setVisibility(View.VISIBLE);
        }*/

        updateSubtitle();
    }


    private class trackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //private TextView mTitleTextView,mCurPosTextView,mNoTracksTextView;
        //private TextView mDateTextView;
        //private ImageView mBestedImageView;
        private ImageView mTrackImageView;
        private Track mTrack;

        //public trackHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        public trackHolder(View itemView) {
            //super(inflater.inflate(R.layout.list_item_track, parent, false));
            super(itemView);
            itemView.setOnClickListener(this);
            mTrackImageView=(ImageView) itemView.findViewById(R.id.item_image_view);
            //mTitleTextView = (TextView) itemView;
            //mTitleTextView = (TextView) itemView.findViewById(R.id.track_title);
                //mDateTextView = (TextView) itemView.findViewById(R.id.track_date);
                //mBestedImageView = (ImageView) itemView.findViewById(R.id.track_bested);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(getActivity(),mTrack.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(getActivity(), DetailActivity.class);
            //Intent intent = DetailActivity.newIntent(getActivity(), mTrack.getId(), mCurPos);
            Intent intent = PagerActivity.newIntent(getActivity(), mTrack.getId(), mTrack.bindpos);
            startActivityForResult(intent, REQUEST_TRACK);
        }

        public void bind(Track track) {
            mTrack = track;
            //mCurPosTextView.setText(String.valueOf(pos));
            //mTitleTextView.setText(mTrack.getTitle());
            //mDateTextView.setText(DateFormat.getDateTimeInstance().format(mTrack.getDate()));
            //mBestedImageView.setVisibility(track.isBest() ? View.VISIBLE : View.GONE);
        }
        public void bindimage(Drawable drawable) {
            mTrackImageView.setImageDrawable(drawable);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TRACK) {
            if (data == null) {
                return;
            }
            //mCurPos = DetailFragment.getPos(data);
        }
    }

    //субкласс
    private class trackAdapter extends RecyclerView.Adapter<trackHolder> {
        private List<Track> mTracks;

        public trackAdapter(List<Track> tracks) {
            mTracks = tracks;
        }


        @Override
        public trackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
           // TextView textView = new TextView(getActivity());
            //return new trackHolder(layoutInflater, parent, viewType);
            //return new trackHolder(textView);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_track, parent, false);
            return new trackHolder(view);
        }

        @Override
        public void onBindViewHolder(trackHolder holder, int position) {
            Track track = mTracks.get(position);
            //holder.bind(track);
            Drawable placeholder = getResources().getDrawable(R.drawable.blank);
            holder.bindimage(placeholder);
            mPicLoader.loadPic(holder, track.getUrl());
        }

        @Override
        public int getItemCount() {
            return mTracks.size();
        }

        public void setTracks(List<Track> tracks) {
            mTracks = tracks;
        }

        @Override
        public int getItemViewType(int position) {
            Track track = mTracks.get(position);
            return track.isBest()?1:0;
        }

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_track_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int trackSize=TrackBase.get(getActivity()).gettracks().size();

        switch (item.getItemId()) {
            case R.id.new_track:
                AddTrack();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void AddTrack() {
        Track track = new Track();
        TrackBase.get(getActivity()).addtrack(track);
        Intent intent = PagerActivity
                .newIntent(getActivity(), track.getId(),mAdapter.getItemCount()+1);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_TRACK);
    }

    private void updateSubtitle() {
        int trackSize=100; //TrackBase.get(getActivity()).tracksSize();
        String subtitle = getString(R.string.subtitle_format, trackSize);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        //String subtitle = String.valueOf(mCurPos);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<Track>> {

        @Override
        protected List<Track> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }
        @Override
        protected void onPostExecute(List<Track> items) {
            tracks = items;
            updateUI();
        }
    }
}