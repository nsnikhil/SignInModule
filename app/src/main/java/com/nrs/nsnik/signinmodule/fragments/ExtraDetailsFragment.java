package com.nrs.nsnik.signinmodule.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nrs.nsnik.signinmodule.R;
import com.nrs.nsnik.signinmodule.fragments.dialogFragments.VerifyDialogFragment;
import com.nrs.nsnik.signinmodule.interfaces.VerificationDone;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ExtraDetailsFragment extends Fragment implements VerificationDone {


    private static final int SMS_REQUEST_CODE = 564;
    @BindView(R.id.extraPhoneNo) TextInputEditText mExtraPhoneNo;
    @BindView(R.id.extraPhoneVerify) Button mVerify;
    SmsManager mSmsManager;
    String mRandomNo;
    Fragment mFragment;
    private Unbinder mUnbinder;


    public ExtraDetailsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_extra_details, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        listeners();
        mFragment = this;
        return v;
    }


    private void listeners() {
        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    if (validPhoneNo()) {
                        mRandomNo = generateRandomCode();

                        mSmsManager = SmsManager.getDefault();
                        mSmsManager.sendTextMessage(mExtraPhoneNo.getText().toString().trim(), null, mRandomNo, null, null);

                        Bundle args = new Bundle();
                        args.putString(getActivity().getResources().getString(R.string.bundleExtraRandom), mRandomNo);
                        VerifyDialogFragment verifyDialogFragment = new VerifyDialogFragment();
                        verifyDialogFragment.setCancelable(false);
                        verifyDialogFragment.setArguments(args);
                        verifyDialogFragment.setTargetFragment(mFragment, 0);
                        verifyDialogFragment.show(getFragmentManager(), "verify");

                    }
                } else {
                    checkPermission();
                }
            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
        }
    }


    private boolean validPhoneNo() {
        if (mExtraPhoneNo.getText().toString().isEmpty() || mExtraPhoneNo.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Enter a phone no", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mExtraPhoneNo.getText().toString().length() < 10 || mExtraPhoneNo.getText().toString().length() > 15) {
            Toast.makeText(getActivity(), "Enter a valid phone no", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String generateRandomCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt((9999 - 1000) + 1) + 1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void swipePage() {
        mVerify.setEnabled(false);
        mVerify.setText(getActivity().getResources().getString(R.string.verified));
        mExtraPhoneNo.setEnabled(false);
    }
}
