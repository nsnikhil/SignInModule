package com.nrs.nsnik.signinmodule.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nrs.nsnik.signinmodule.fragments.ExtraDetailsFragment;
import com.nrs.nsnik.signinmodule.fragments.LogoFragment;
import com.nrs.nsnik.signinmodule.fragments.TermsFragment;
import com.nrs.nsnik.signinmodule.fragments.SignInFragment;


public class adapterViewPager extends FragmentStatePagerAdapter {


    public adapterViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new LogoFragment();
        }
        if (position == 1) {
            return new SignInFragment();
        }
        if (position == 2) {
            return new TermsFragment();
        }
        if (position == 3) {
            return new ExtraDetailsFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 4;
    }
}
