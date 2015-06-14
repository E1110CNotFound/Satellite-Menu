package com.guoyonghui.arcmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by сю╩т on 2015/6/14.
 */
public class ContentFragment extends Fragment {

    public static final String EXTRA_LAYOUT_RES_ID = "com.guoyonghui.arcmenu.extra_layout_res_id";

    private int mLayoutResID;

    public static ContentFragment newInstance(int layoutResID) {
        ContentFragment fragment = new ContentFragment();

        Bundle args = new Bundle();
        args.putInt(EXTRA_LAYOUT_RES_ID, layoutResID);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutResID = getArguments().getInt(EXTRA_LAYOUT_RES_ID, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(mLayoutResID, container, false);
    }
}
