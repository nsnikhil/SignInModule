package com.nrs.nsnik.signinmodule.fragments;


import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class SignInFragment extends Fragment implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{



    @BindView(R.id.singInGoogleButton) SignInButton mGoogleSignInButton;
    private Unbinder mUnbinder;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    private static final int mSignInIntentCode = 5264;
    private static final int CAMERA_REQUEST_CODE = 55;
    private static final int GALLERY_REQUEST_CODE = 56;
    private String mCurrentPhotoPath;

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
                    swipePager(2);
                }else {
                    noPictureAlert();
                }
            }
        } else {
            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.signInError),Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void noPictureAlert(){
        final AlertDialog.Builder warning = new AlertDialog.Builder(getActivity());
        warning.setMessage("Your account dosen't have a profile picture, you can take a new pic or choose one from gallery");
        warning.setCancelable(false);
        warning.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseImageAction();
            }
        });
        warning.create().show();
    }

    private void chooseImageAction() {
        AlertDialog.Builder choosePath = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add("Take a picture");
        arrayAdapter.add("Choose from gallery");
        choosePath.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ignored) {
                        }if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.nrs.nsnik.signinmodule.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                        }
                    }
                }if (position == 1) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
                }
            }
        });
        choosePath.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mSignInIntentCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                InputStream is;
                if (data != null) {
                    try {
                        is = getActivity().getContentResolver().openInputStream(data.getData());
                        Bitmap b = BitmapFactory.decodeStream(is);
                        saveFile(b);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Bitmap b = BitmapFactory.decodeFile(data.getData().getPath());
                    try {
                        saveFile(b);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                try {
                    saveFile(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveFile(Bitmap bitmap) throws FileNotFoundException {
        File folder = getActivity().getExternalCacheDir();
        File f = new File(folder,"profile.jpg");
        Toast.makeText(getActivity(),f.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        FileOutputStream fos =   new FileOutputStream(f);
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        try {
            fos.flush();
            swipePager(2);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
