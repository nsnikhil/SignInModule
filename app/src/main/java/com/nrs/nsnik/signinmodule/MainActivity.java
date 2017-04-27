package com.nrs.nsnik.signinmodule;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.nrs.nsnik.signinmodule.adapters.adapterViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_REQUEST_CODE = 565;
    @BindView(R.id.mainViewPager)
    UnScrollableViewPager mViewPager;
    adapterViewPager mViePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialize();
        listeners();
    }

    private void initialize() {
        mViePagerAdapter = new adapterViewPager(getSupportFragmentManager());
        mViewPager.setAdapter(mViePagerAdapter);
    }

    private void listeners() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
                    }

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==SMS_REQUEST_CODE){
            if (grantResults.length>0){
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mViewPager.setCurrentItem(3,true);
                }
            }
        }
    }
}
