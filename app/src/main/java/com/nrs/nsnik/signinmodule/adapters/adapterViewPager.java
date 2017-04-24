package com.nrs.nsnik.signinmodule.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nrs.nsnik.signinmodule.fragments.ExtraDetailsFragment;
import com.nrs.nsnik.signinmodule.fragments.LogoFragment;
import com.nrs.nsnik.signinmodule.fragments.PermissionFragment;
import com.nrs.nsnik.signinmodule.fragments.SignInFragment;
import com.nrs.nsnik.signinmodule.fragments.TermsFragment;


public class adapterViewPager extends FragmentStatePagerAdapter{


    public adapterViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new LogoFragment();
        }if(position==1){
            return new SignInFragment();
        }if(position==2){
            return new PermissionFragment();
        }if(position==3){
            return new ExtraDetailsFragment();
        }if(position==4){
            return new TermsFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 5;
    }
}
