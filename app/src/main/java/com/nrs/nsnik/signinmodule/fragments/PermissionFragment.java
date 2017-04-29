package com.nrs.nsnik.signinmodule.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nrs.nsnik.signinmodule.R;
import com.nrs.nsnik.signinmodule.interfaces.Pager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PermissionFragment extends Fragment{

    @BindView(R.id.profileImage) CircularImageView mProfileImage;
    @BindView(R.id.profileName) TextView mProfileName;
    @BindView(R.id.profileNextIcon) ImageView mNext;
    private static final int SMS_REQUEST_CODE = 564;
    private static final String NULL_VALUE =  "N/A";


    private Unbinder mUnbinder;

    public PermissionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_permission, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        listeners();
        new setNameAsync().execute();
        new setImageAsync().execute();
        return v;
    }


    private void listeners(){
        Animation shake = AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
        mNext.startAnimation(shake);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                   swipePage();
                }else {
                   checkPermission();
                }
            }
        });
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            requestPermissions( new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==SMS_REQUEST_CODE){
            if(grantResults.length>0){
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    swipePage();
                }
            }
        }
    }

    private void swipePage(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                vp.setCurrentItem(3,true);
            }
        });
    }

    private Bitmap getBitmapAsync(){
       return BitmapFactory.decodeFile(new File(getActivity().getExternalCacheDir(),"profile.jpg").toString());
    }

    private String getNameAsync(){
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getActivity().getResources().getString(R.string.singInName),NULL_VALUE);
    }


    private class setImageAsync extends AsyncTask<Void,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... params) {
            return getBitmapAsync();

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap==null){
                new setImageAsync().execute();
            }else {
                mProfileImage.setImageBitmap(bitmap);
            }
        }

    }

    private class setNameAsync extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            return getNameAsync();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase(NULL_VALUE)||s==null){
                new setNameAsync().execute();
            }else {
                mProfileName.setText(s);
            }
        }

    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}
