package ru.parvenu.sitile;

import android.support.v4.app.Fragment;

public class ListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ListFragment();
    }
}