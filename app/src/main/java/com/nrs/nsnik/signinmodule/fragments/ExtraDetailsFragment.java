package com.nrs.nsnik.signinmodule.fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nrs.nsnik.signinmodule.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ExtraDetailsFragment extends Fragment {


    @BindView(R.id.extraPhoneNo) TextInputEditText mExtraPhoneNo;
    private Unbinder mUnbinder;

    public ExtraDetailsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_extra_details, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        return v;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
