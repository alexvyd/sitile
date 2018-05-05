package ru.parvenu.sitile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class PagerActivity extends AppCompatActivity {
    private static final String EXTRA_track_ID =
            "ru.parvenu.sitile.track_id";
    private static final String EXTRA_track_POS =
            "ru.parvenu.sitile.track_listpos";
    private static final String EXTRA_best_SHOW =
            "ru.parvenu.sitile.best_show";

    private ViewPager mViewPager;
    private List<Track> mTracks;

    public static Intent newIntent(Context packageContext, UUID trackId, int listpos, boolean isbest) {
        Intent intent = new Intent(packageContext, PagerActivity.class);
        intent.putExtra(EXTRA_track_ID, trackId);
        intent.putExtra(EXTRA_track_POS, listpos);
        intent.putExtra(EXTRA_best_SHOW, isbest);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_pager);

        UUID trackId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_track_ID);
        final int listpos = (int) getIntent()
                .getIntExtra(EXTRA_track_POS,0);
        final Boolean mBestShow = (boolean) getIntent()
                .getBooleanExtra(EXTRA_best_SHOW,false);

        mViewPager = findViewById(R.id.track_view_pager);
        mTracks = TrackBase.get(this).getTracks(mBestShow);
        FragmentManager fragmentManager = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Track track = mTracks.get(position);
                return DetailFragment.newInstance(track.getId(), listpos, position, mBestShow);
            }

            @Override
            public int getCount() {
                return mTracks.size();
            }
        });
        for (int i = 0; i < mTracks.size(); i++) {
            if (mTracks.get(i).getId().equals(trackId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
        //mViewPager.setCurrentItem(pos);
    }
}
