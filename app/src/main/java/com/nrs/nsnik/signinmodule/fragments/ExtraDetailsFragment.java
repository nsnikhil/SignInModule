package com.nrs.nsnik.signinmodule.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nrs.nsnik.signinmodule.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ExtraDetailsFragment extends Fragment{


    @BindView(R.id.extraPhoneNo) TextInputEditText mExtraPhoneNo;
    @BindView(R.id.extraPhoneVerify) Button mVerify;
    private Unbinder mUnbinder;
    SmsManager mSmsManager;
    private static final int SMS_REQUEST_CODE = 564;

    public ExtraDetailsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_extra_details, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initialize();
        listeners();
        return v;
    }

    private void initialize() {
        TelephonyManager telephoneManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mExtraPhoneNo.setText(telephoneManager.getLine1Number());
    }

    private void listeners() {

    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},SMS_REQUEST_CODE);
        }else {

        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
