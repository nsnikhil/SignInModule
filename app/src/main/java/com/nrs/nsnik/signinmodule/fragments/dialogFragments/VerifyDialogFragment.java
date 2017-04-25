package com.nrs.nsnik.signinmodule.fragments.dialogFragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nrs.nsnik.signinmodule.R;
import com.nrs.nsnik.signinmodule.interfaces.VerificationDone;

import java.io.BufferedReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class VerifyDialogFragment extends DialogFragment {

    @BindView(R.id.verifyCodeField) TextView mVerifyField;
    @BindView(R.id.verifyCodeButton) Button mVerifyButton;
    @BindView(R.id.verifyCodeRetry) Button mRetry;
    BroadcastReceiver mBroadcastReceiver;
    private static boolean mVerified;
    private Unbinder mUnbinder;
    String mRandomNo;
    VerificationDone mVerificationDone;

    public VerifyDialogFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verify_dialog, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        listeners();
        readMessage();
        mVerificationDone = (VerificationDone) getTargetFragment();
        if(getArguments()!=null){
           mRandomNo =  getArguments().getString(getActivity().getResources().getString(R.string.bundleExtraRandom));
        }
        return v;
    }


    private void listeners(){
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void readMessage(){
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle myBundle = intent.getExtras();
                SmsMessage[] messages = null;
                if (myBundle != null) {
                    Object [] pdus = (Object[]) myBundle.get("pdus");
                    messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = myBundle.getString("format");
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        } else {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }
                        mVerifyField.setText(messages[i].getMessageBody());
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(mBroadcastReceiver,intentFilter);
    }

    private void verifyCode(){
        if(mVerifyField.getText().toString().equalsIgnoreCase(mRandomNo)){
            Toast.makeText(getActivity(),"Verified",Toast.LENGTH_LONG).show();
            mVerified = true;
            mVerificationDone.swipePage();
            dismiss();
        }else {
            Toast.makeText(getActivity(),"Verification failed retry",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
