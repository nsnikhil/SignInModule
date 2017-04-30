package com.nrs.nsnik.signinmodule.fragments;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.nrs.nsnik.signinmodule.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TermsFragment extends Fragment {

    private static final String NULL_VALUE = "N/A";
    @BindView(R.id.profileImage)
    CircularImageView mProfileImage;
    @BindView(R.id.profileName)
    TextView mProfileName;
    @BindView(R.id.profileNextIcon)
    ImageView mNext;
    private Unbinder mUnbinder;

    public TermsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_terms, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        listeners();
        new setNameAsync().execute();
        new setImageAsync().execute();
        return v;
    }


    private void listeners() {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        mNext.startAnimation(shake);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder terms = new AlertDialog.Builder(getActivity());
                terms.setMessage(getActivity().getResources().getString(R.string.termsagree));
                terms.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        swipePage();
                    }
                });
                terms.create().show();
            }
        });
    }


    private void swipePage() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                vp.setCurrentItem(3, true);
            }
        });
    }

    private Bitmap getBitmapAsync() {
        return BitmapFactory.decodeFile(new File(getActivity().getExternalCacheDir(), "profile.jpg").toString());
    }

    private String getNameAsync() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getActivity().getResources().getString(R.string.singInName), NULL_VALUE);
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    private class setImageAsync extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            return getBitmapAsync();

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) {
                new setImageAsync().execute();
            } else {
                mProfileImage.setImageBitmap(bitmap);
            }
        }

    }

    private class setNameAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return getNameAsync();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase(NULL_VALUE)) {
                new setNameAsync().execute();
            } else {
                mProfileName.setText(s);
            }
        }

    }
}
