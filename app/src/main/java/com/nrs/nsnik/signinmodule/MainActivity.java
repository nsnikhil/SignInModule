package com.nrs.nsnik.signinmodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.nrs.nsnik.signinmodule.adapters.adapterViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainToolbar) Toolbar mMainToolbar;
    @BindView(R.id.mainViewPager) com.nrs.nsnik.signinmodule.UnScrollableViewPager mViewPager;
    adapterViewPager mViePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        setSupportActionBar(mMainToolbar);
        mViePagerAdapter = new adapterViewPager(getSupportFragmentManager());
        mViewPager.setAdapter(mViePagerAdapter);
    }

}
