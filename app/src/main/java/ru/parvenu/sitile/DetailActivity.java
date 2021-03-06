package ru.parvenu.sitile;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class DetailActivity extends SingleFragmentActivity {
    private static final String EXTRA_track_ID =
            "ru.parvenu.android.sitile.track_id";
    private static final String EXTRA_track_LISTPOS =
            "ru.parvenu.android.sitile.track_listpos";
    private static final String EXTRA_track_PAGEPOS =
            "ru.parvenu.android.sitile.track_pagepos";
    private static final String EXTRA_best_SHOW =
            "ru.parvenu.android.sitile.best_show";

    //Чтобы сообщить DetailFragment, какой объект Track следует отображать,
    // можно передать идентификатор в дополнении (extra) объекта Intent при запуске DetailActivity.
    public static Intent newIntent(Context packageContext, UUID trackId, int listpos, boolean isbest) {
        Intent intent = new Intent(packageContext, DetailActivity.class);
        intent.putExtra(EXTRA_track_ID, trackId);
        intent.putExtra(EXTRA_track_LISTPOS, listpos);
        intent.putExtra(EXTRA_track_PAGEPOS, listpos);
        intent.putExtra(EXTRA_best_SHOW, isbest);
        return intent;
    }



    @Override
    protected Fragment createFragment() {
        UUID trackId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_track_ID);
        int listpos = (int) getIntent()
                .getIntExtra(EXTRA_track_LISTPOS,0);
        int pagepos = (int) getIntent()
                .getIntExtra(EXTRA_track_PAGEPOS,0);
        boolean isbest = (boolean) getIntent()
                .getBooleanExtra(EXTRA_best_SHOW,false);
        return DetailFragment.newInstance(trackId, listpos, pagepos, isbest);
    }

}
