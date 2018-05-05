package ru.parvenu.sitile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";
    private RecyclerView mTrackRecyclerView;
    private trackAdapter mAdapter;
    private boolean mIsBest;
    private static final int REQUEST_TRACK = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private List<Track> tracks = new ArrayList<>();
    private PicLoader<trackHolder> mPicLoader;
    private TrackBase trackBase;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trackBase = TrackBase.get(getActivity());
        tracks = trackBase.getTracks(false);


        setHasOptionsMenu(true);
        //setRetainInstance(true);

        //Загрузка XML-списка
        if (tracks.size()==0) {
            new FetchItemsTask().execute();
        }

        //Настройка загрузки превьюшки "по требованию"
        Handler responseHandler = new Handler();
        mPicLoader = new PicLoader<>(responseHandler);
        mPicLoader.setPicLoadListener(
                new PicLoader.PicLoadListener<trackHolder>() {
                    @Override
                    public void onPicLoaded(trackHolder photoHolder,
                                                      Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        //Установка картинки после загрузки
                        photoHolder.bindListPic(drawable);
                    }
                }
        );
        mPicLoader.start();
        mPicLoader.getLooper();



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
            mIsBest = savedInstanceState.getBoolean
                    (SAVED_SUBTITLE_VISIBLE);
        }
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
        //Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new trackAdapter(tracks);
            if (isAdded()) {
                mTrackRecyclerView.setAdapter(mAdapter);
            }
        } else {
            mAdapter.setTracks(trackBase.getTracks(mIsBest));
        }
        mAdapter.notifyDataSetChanged();
        //mAdapter.notifyItemChanged(mCurPos);
        updateSubtitle();
    }


    private class trackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mTrackImageView;
        private ImageView mBestView;
        private Track mTrack;

        public trackHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTrackImageView = itemView.findViewById(R.id.item_image_view);
            mBestView = itemView.findViewById(R.id.bestthumb);
        }

        @Override
        public void onClick(View view) {
            Intent intent = PagerActivity.newIntent(getActivity(), mTrack.getId(), mTrack.bindpos, mIsBest);
            startActivityForResult(intent, REQUEST_TRACK);
        }

        public void bindDetail(Track track) {
            mTrack = track;
        }
        public void bindListPic(Drawable drawable) {

            mTrackImageView.setImageDrawable(drawable);
            if(mTrack.isBest()) mBestView.setImageResource(R.drawable.btn_star_big_on);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TRACK) {
            if (data == null) {
                return;
            }
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

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_track, parent, false);
            return new trackHolder(view);
        }

        @Override
        public void onBindViewHolder(trackHolder holder, int position) {
            Track track = mTracks.get(position);
            holder.bindDetail(track);
            //Установка заглушки
            Drawable placeholder = getResources().getDrawable(R.drawable.blank);
            holder.bindListPic(placeholder);
            //Загрузка картинки
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
        MenuItem bestItem = menu.findItem(R.id.best);
        if (mIsBest) {
            bestItem.setTitle(R.string.show_all);
            bestItem.setIcon(R.drawable.btn_star_big_on);
        } else {
            bestItem.setTitle(R.string.show_best);
            bestItem.setIcon(R.drawable.btn_star_big_off);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.best:
                mIsBest = !mIsBest;
                getActivity().invalidateOptionsMenu();
                updateUI();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        int trackSize=trackBase.getSize(mIsBest);
        String subtitle = getString(R.string.subtitle_format, trackSize);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mIsBest);

    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<Track>> {

        @Override
        protected List<Track> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }
        @Override
        protected void onPostExecute(List<Track> items) {
            items.get(0).setBest(true); //демо: первое фото - лучшее
            trackBase.setTracks(items);//создать модель и залить туда картинки
            //tracks = items;
            updateUI();
        }
    }
}