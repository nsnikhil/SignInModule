package com.nrs.nsnik.signinmodule.fragments;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nrs.nsnik.signinmodule.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SignInFragment extends Fragment implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{



    @BindView(R.id.singInGoogleButton) SignInButton mGoogleSignInButton;
    private Unbinder mUnbinder;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    private static final int mSignInIntentCode = 5264;

    public SignInFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_sign_in, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initialize();
        listeners();
        initializeGoogleClients();
        return v;
    }

    private void listeners() {
        mGoogleSignInButton.setOnClickListener(this);
    }

    private void initialize() {
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
    }

    private void initializeGoogleClients(){
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestProfile().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mSignInIntentCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct!=null){
                SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
                spf.edit().putString(getActivity().getResources().getString(R.string.singInName), acct.getDisplayName()).apply();
                spf.edit().putString(getActivity().getResources().getString(R.string.singInEmail), acct.getEmail()).apply();
                spf.edit().putBoolean(getActivity().getResources().getString(R.string.signInStatus),true).apply();
                if(acct.getPhotoUrl()!=null){
                    downloadProfilePic(acct.getPhotoUrl().toString());
                }
                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                vp.setCurrentItem(1,true);
            }
        } else {
            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.signInError),Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadProfilePic(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setDestinationUri(Uri.fromFile(new File(getActivity().getExternalCacheDir() + "/profile.jpg")));
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.singInGoogleButton:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, mSignInIntentCode);
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
