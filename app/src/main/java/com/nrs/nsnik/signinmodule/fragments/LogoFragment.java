package com.nrs.nsnik.signinmodule.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nrs.nsnik.signinmodule.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class LogoFragment extends Fragment {

    @BindView(R.id.logoNextIcon) ImageView mNextIcon;
    private Unbinder mUnbinder;

    public LogoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logo, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        listeners();
        return v;
    }

    private void listeners(){
        Animation shake = AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
        mNextIcon.startAnimation(shake);
        mNextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager pager = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                pager.setCurrentItem(1,true);
            }
        });
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}
