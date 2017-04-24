package com.nrs.nsnik.signinmodule.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nrs.nsnik.signinmodule.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PermissionFragment extends Fragment {

    @BindView(R.id.profileImage) CircularImageView mProfileImage;
    @BindView(R.id.profileName) TextView mProfileName;
    @BindView(R.id.profileNextIcon) ImageView mNext;
    @BindView(R.id.profilePermissionCheck) Button mCheckButton;
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
        new setValues().execute();
        return v;
    }

    private void listeners(){
        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    ViewPager vp = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                    vp.setCurrentItem(3,true);
                }else {
                   checkPermission();
                }
            }
        });
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==SMS_REQUEST_CODE){
            if(grantResults.length>0){
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    ViewPager vp = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                    vp.setCurrentItem(3,true);
                }
            }
        }
    }

    private Bitmap getBitmapAsync(){
       return BitmapFactory.decodeFile(new File(getActivity().getExternalCacheDir(),"profile.jpg").toString());
    }

    private String getNameAsync(){
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getActivity().getResources().getString(R.string.singInName),NULL_VALUE);
    }


    private class setValues extends AsyncTask<Void,Void,Void> {

        Bitmap mImage;
        String mName;

        @Override
        protected Void doInBackground(Void... params) {
            mImage = getBitmapAsync();
            mName = getNameAsync();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(mImage==null||mName.equalsIgnoreCase(NULL_VALUE)){
                new setValues().execute();
            }else {
                mProfileImage.setImageBitmap(mImage);
                mProfileName.setText(mName);
            }
        }
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}