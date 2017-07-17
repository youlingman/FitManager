package com.cyl.fitmanager.Fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyl.fitmanager.R;

import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseConfigFragment extends Fragment {

    Unbinder unbinder;

    public BaseConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null != unbinder) {
            unbinder.unbind();
        }
    }
}
