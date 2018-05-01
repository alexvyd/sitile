package ru.parvenu.sitile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;


public class ListFragment extends Fragment {
    private RecyclerView mTrackRecyclerView;
    private trackAdapter mAdapter;
    private boolean mSubtitleVisible;
    private static final int REQUEST_TRACK = 1;
    private int mCurPos; //позиция выбранного элемента во view-группе
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private TextView mNotracksTextView;
    private Button mAddtrackButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container,
                false);
        mNotracksTextView = (TextView) view.findViewById(R.id.track_nolist);
        mAddtrackButton = (Button) view.findViewById(R.id.track_add_button);
        mTrackRecyclerView = (RecyclerView) view
                .findViewById(R.id.track_recycler_view);

        mTrackRecyclerView.setLayoutManager(new LinearLayoutManager
                (getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean
                    (SAVED_SUBTITLE_VISIBLE);
        }
        mAddtrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTrack();
            }
        });
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        TrackBase trackBase = TrackBase.get(getActivity());
        List<Track> tracks = trackBase.gettracks();
        if (mAdapter == null) {
            mAdapter = new trackAdapter(tracks);
            mTrackRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setTracks(tracks);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mCurPos);
        }
        if(tracks.size()>0){
            mNotracksTextView.setVisibility(View.INVISIBLE);
            mAddtrackButton.setVisibility(View.INVISIBLE);
        }
        else {
            mNotracksTextView.setVisibility(View.VISIBLE);
            mAddtrackButton.setVisibility(View.VISIBLE);
        }

        updateSubtitle();
    }
    //субкласс
    private class trackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView,mCurPosTextView,mNoTracksTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Track mTrack;

        public trackHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            super(inflater.inflate(R.layout.list_item_track, parent, false));
                itemView.setOnClickListener(this);
                mCurPosTextView = (TextView) itemView.findViewById(R.id.track_pos);
                mTitleTextView = (TextView) itemView.findViewById(R.id.track_title);
                mDateTextView = (TextView) itemView.findViewById(R.id.track_date);
                mSolvedImageView = (ImageView) itemView.findViewById(R.id.track_solved);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(getActivity(),mTrack.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(getActivity(), DetailActivity.class);
            //Intent intent = DetailActivity.newIntent(getActivity(), mTrack.getId(), mCurPos);
            Intent intent = PagerActivity.newIntent(getActivity(), mTrack.getId(), mTrack.bindpos);
            startActivityForResult(intent, REQUEST_TRACK);
        }

        public void bind(Track track, int pos) {
            mTrack = track;
            mTrack.bindpos=pos;
            mCurPosTextView.setText(String.valueOf(pos));
            mTitleTextView.setText(mTrack.getTitle());
            mDateTextView.setText(DateFormat.getDateTimeInstance().format(mTrack.getDate()));
            mSolvedImageView.setVisibility(track.isSolved() ? View.VISIBLE :
                    View.GONE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TRACK) {
            if (data == null) {
                return;
            }
            mCurPos = DetailFragment.getPos(data);
        }
    }

    //субкласс
    private class trackAdapter extends RecyclerView.Adapter<trackHolder> {
        private List<Track> mTracks;

        public trackAdapter(List<Track> tracks) {
            mTracks = tracks;
        }

        @NonNull
        @Override
        public trackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new trackHolder(layoutInflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull trackHolder holder, int position) {
            Track track = mTracks.get(position);
            holder.bind(track, position);
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
            return track.isSolved()?1:0;
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
        /*int trackSize=TrackBase.get(getActivity()).tracksSize();
        String subtitle = getString(R.string.subtitle_format, trackSize);
        if (!mSubtitleVisible) {
            subtitle = null;
        }*/
        String subtitle = String.valueOf(mCurPos);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }
}