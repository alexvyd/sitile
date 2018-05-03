package ru.parvenu.sitile;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;

public class DetailFragment extends Fragment {
    private static final String ARG_track_ID = "track_id";
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
    /*private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mBestedCheckBox;*/
    private ImageView mTrackImageBigView;

    public static DetailFragment newInstance(UUID trackId, int listpos, int pagepos) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_track_ID, trackId);
        args.putInt(ARG_track_LISTPOS, listpos);
        args.putInt(ARG_track_PAGEPOS, pagepos);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //mTrack = new Track();
       // UUID trackId = (UUID) getActivity().getIntent().getSerializableExtra(DetailActivity.EXTRA_track_ID);
        UUID trackId = (UUID) getArguments().getSerializable(ARG_track_ID);
        listpos = (int) getArguments().getInt(ARG_track_LISTPOS);
        pagepos = (int) getArguments().getInt(ARG_track_PAGEPOS);
        mTrack = TrackBase.get(getActivity()).getTrack(trackId);
        //mTrack = TrackBase.get(getActivity()).gettrackByPos(trackPos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track, container, false);
        mTrackImageBigView = v.findViewById(R.id.item_image_bigview);
        Drawable placeholder = getResources().getDrawable(R.drawable.blank);
        mTrackImageBigView.setImageDrawable(placeholder);
        new FetchItemTask().execute();
        /*
        mDateButton = (Button) v.findViewById(R.id.track_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mTrack.getDate());
                dialog.setTargetFragment(DetailFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mBestedCheckBox = (CheckBox)v.findViewById(R.id.track_bested);
        mBestedCheckBox.setChecked(mTrack.isBest());
        mBestedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTrack.setBest(isChecked);
            }
        });
        */

        String subtitle = getString(R.string.subtitle_track, listpos, pagepos);
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
        //TrackBase.get(getActivity()).updatetrack(mTrack);
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
            updateDate();
        }
    }

    private void updateDate() {
        //mDateButton.setText(mTrack.getDate().toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_track,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int trackSize=TrackBase.get(getActivity()).tracksSize();
        switch (item.getItemId()) {
            case R.id.save_track:
                getActivity().onBackPressed();
                //getActivity().finish();
                return true;
            case R.id.delete_track:
                //TrackBase.get(getActivity()).deltrack(mTrack.getId(),listpos);
                getActivity().onBackPressed();
                //getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static int getPos(Intent result) {
        return result.getIntExtra(EXTRA_track_PAGEPOS, -1);
    }
    private class FetchItemTask extends AsyncTask<Void,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            return new FlickrFetchr().fetchItem(mTrack.getPhotoUri());
        }
        @Override
        protected void onPostExecute(Bitmap item) {
            //tracks = items;
            mTrackImageBigView.setImageBitmap(item);
        }
    }
}
