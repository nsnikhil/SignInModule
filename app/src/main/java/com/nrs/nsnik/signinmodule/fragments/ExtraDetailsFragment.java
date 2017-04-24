package com.nrs.nsnik.signinmodule.fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nrs.nsnik.signinmodule.R;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ExtraDetailsFragment extends Fragment{


    @BindView(R.id.extraPhoneNo) TextInputEditText mExtraPhoneNo;
    @BindView(R.id.extraPhoneVerify) Button mVerify;
    @BindView(R.id.extraNextIcon) ImageView mNextIcon;
    private Unbinder mUnbinder;
    SmsManager mSmsManager;
    private static final int SMS_REQUEST_CODE = 564;
    BroadcastReceiver mBroadcastReceiver;
    private static boolean mVerified;
    Dialog mDialog;
    String mRandomNo;

    public ExtraDetailsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_extra_details, container, false);
        mUnbinder = ButterKnife.bind(this,v);
        initialize();
        listeners();
        readMessage();
        return v;
    }

    private void initialize() {

    }

    private void listeners() {
        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    if(validPhoneNo()) {
                        AlertDialog.Builder verifyDialog = new AlertDialog.Builder(getActivity());
                        verifyDialog.setMessage("Verifying...");
                        verifyDialog.setCancelable(false);
                        mDialog  = verifyDialog.create();
                        mDialog.show();
                        mSmsManager = SmsManager.getDefault();
                        mSmsManager.sendTextMessage(mExtraPhoneNo.getText().toString().trim(), null, generateRandomCode(), null, null);
                    }
                } else {
                    checkPermission();
                }
            }
        });
        mNextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVerified){
                    ViewPager vp = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                    vp.setCurrentItem(4,true);
                }else {
                    Toast.makeText(getActivity(),"Verify a phone no to continue",Toast.LENGTH_SHORT).show();
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

    private boolean validPhoneNo(){
        if(mExtraPhoneNo.getText().toString().isEmpty()||mExtraPhoneNo.getText().toString().length()==0){
            Toast.makeText(getActivity(),"Enter a phone no",Toast.LENGTH_SHORT).show();
            return false;
        }else if(mExtraPhoneNo.getText().toString().length()<10||mExtraPhoneNo.getText().toString().length()>15){
            Toast.makeText(getActivity(),"Enter a valid phone no",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String generateRandomCode(){
        Random random = new Random();
        mRandomNo =  String.valueOf(random.nextInt((9999 - 1000) + 1) + 1000);
        return mRandomNo;
    }

    private void readMessage(){
         mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle myBundle = intent.getExtras();
                SmsMessage [] messages = null;
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
                        mExtraPhoneNo.setEnabled(false);
                        mVerify.setEnabled(false);
                        //mExtraPhoneNo.setText(messages[i].getMessageBody());
                        verifyCode(messages[i].getMessageBody());
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(mBroadcastReceiver,intentFilter);
    }

    private void verifyCode(String code){
        mDialog.dismiss();
        if(code.equalsIgnoreCase(mRandomNo)){
            Toast.makeText(getActivity(),"Verified",Toast.LENGTH_LONG).show();
            mVerified = true;
            mVerify.setText("Verified");
            ViewPager vp = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
            vp.setCurrentItem(4,true);
        }else {
            Toast.makeText(getActivity(),"Verification failed retry",Toast.LENGTH_LONG).show();
            mExtraPhoneNo.setText("");
            mExtraPhoneNo.setEnabled(true);
            mVerify.setEnabled(true);
        }
    }

    private void swipePager(final int to){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                vp.setCurrentItem(to,true);
            }
        }, 1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
