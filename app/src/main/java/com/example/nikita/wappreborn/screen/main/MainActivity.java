package com.example.nikita.wappreborn.screen.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.nikita.wappreborn.R;
import com.example.nikita.wappreborn.presenter.MainPresenter;
import com.example.nikita.wappreborn.screen.map.MapActivity;

public class MainActivity extends AppCompatActivity implements IMainView{

    private final static int REQUEST_GET_WEATHER_ID = 1;
    private final static int REQUEST_GET_LOCATION_ID = 2;
    private final static int REQUEST_GET_MAP_ID = 3;
    private final static int DIALOG_ERROR = 1;
    private final static String SAVED_TEXT = "saved_text";

    private double mLon;
    private double mLat;

    private int mCurrentDay;
    private MainPresenter mMainPresenter;
    private SharedPreferences mPref;

    private TextView mPlaceTextView;
    private TextView mTempDayTextView;
    private TextView mTempNightTextView;
    private TextView mDateTextView;
    private TextView mConditionTextView;
    private TextView mLessTextView;
    private TextView mMoreTextView;
    private Button mCheckButton;
    private Button mPlusButton;
    private Button mMinusButton;
    private EditText mCityEditText;
    private ImageView mIconImageView;
    private ImageView mLocationMarkerImageView;
    private ImageView mMapImageView;
    private RelativeLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFullScreen();
        defineViews();
        setListeners();
        mMainPresenter = new MainPresenter(this, getApplicationContext());
        loadText();
    }

    @Override
    public int getCurrentDay() {
        return mCurrentDay;
    }

    @Override
    public String getCity() {
        String city = mCityEditText.getText().toString();
        return city;
    }

    @Override
    public void setLocation(String place) {
        mCityEditText.setText(place);
    }

    @Override
    public void setDate(String date) {
        mDateTextView.setText(date);
    }

    @Override
    public void setCity(String city) {
        mPlaceTextView.setText(city);
    }

    @Override
    public void setTemperatureDay(String temp) {
        mTempDayTextView.setText(temp);
    }

    @Override
    public void setTemperatureNight(String temp) {
        mTempNightTextView.setText(temp);
    }

    @Override
    public void setCondition(String condition) {
        mConditionTextView.setText(condition);
    }

    @Override
    public void setImages(int[] id) {
        mIconImageView.setImageResource(id[0]);
        mLayout.setBackgroundResource(id[1]);
    }

    @Override
    public void setFullScreen() {
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    @Override
    public void setLatLon(double[] coord) {
        mLat = coord[0];
        mLon = coord[1];
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("lat", mLat);
        intent.putExtra("lon", mLon);
        startActivity(intent);
    }

    @Override
    public void defineViews() {
        mDateTextView = (TextView) findViewById(R.id.textViewDate);
        mTempDayTextView = (TextView) findViewById(R.id.textViewDay);
        mTempNightTextView = (TextView) findViewById(R.id.textViewNight);
        mPlaceTextView = (TextView) findViewById(R.id.textViewPlace);
        mConditionTextView = (TextView) findViewById(R.id.textViewCondition);
        mLessTextView = (TextView) findViewById(R.id.textViewLess);
        mMoreTextView = (TextView) findViewById(R.id.textViewMore);
        mIconImageView = (ImageView) findViewById(R.id.imageView);
        mLocationMarkerImageView = (ImageView) findViewById(R.id.imageViewMarker);
        mMapImageView = (ImageView) findViewById(R.id.imageViewMap);
        mCityEditText = (EditText) findViewById(R.id.editText);
        mCheckButton = (Button) findViewById(R.id.buttonCheck);
        mPlusButton = (Button) findViewById(R.id.button2);
        mMinusButton = (Button) findViewById(R.id.button3);
        mLayout = (RelativeLayout) findViewById(R.id.activity_main);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Hattori_Hanzo.otf");
        mPlaceTextView.setTypeface(typeFace);
        mTempDayTextView.setTypeface(typeFace);
        mTempNightTextView.setTypeface(typeFace);
        mDateTextView.setTypeface(typeFace);
        mConditionTextView.setTypeface(typeFace);
        mCheckButton.setTypeface(typeFace);
    }

    @Override
    public void setListeners() {
        mMapImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                isOnline(REQUEST_GET_MAP_ID);
            }
        });

        mLocationMarkerImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                isOnline(REQUEST_GET_LOCATION_ID);
            }
        });

        mCheckButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                mCurrentDay = 0;
                mMoreTextView.setAlpha(1);
                mLessTextView.setAlpha(0);
                saveText();
                isOnline(REQUEST_GET_WEATHER_ID);
            }
        });

        mMinusButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mCurrentDay!=6) {
                    mCurrentDay++;
                }
                if(mCurrentDay==5 || mCurrentDay==6) {
                    mMoreTextView.setAlpha(0);
                }
                if(mCurrentDay!=6 && mCurrentDay!=0) {
                    mMoreTextView.setAlpha(1);
                    mLessTextView.setAlpha(1);
                }
                mMainPresenter.updateData();
            }
        });

        mPlusButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mCurrentDay!=0) {
                    mCurrentDay--;
                }
                if(mCurrentDay==1 || mCurrentDay==0) {
                    mLessTextView.setAlpha(0);
                }
                if(mCurrentDay!=6 && mCurrentDay!=0) {
                    mMoreTextView.setAlpha(1);
                    mLessTextView.setAlpha(1);
                }
                mMainPresenter.updateData();
            }
        });
    }

    @Override
    public Context getWApplicationContext(){
        return getApplicationContext();
    }

    @Override
    public void saveText() {
        mPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = mPref.edit();
        ed.putString(SAVED_TEXT, mCityEditText.getText().toString());
        ed.commit();
    }

    @Override
    public void loadText() {
        mPref = getPreferences(MODE_PRIVATE);
        String savedText = mPref.getString(SAVED_TEXT, "");
        mCityEditText.setText(savedText);
    }

    @Override
    public void isOnline(int id) {
        Handler h = new Handler(getApplicationContext().getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                if (checkInternet()) {
                    switch (id)
                    {
                        case REQUEST_GET_WEATHER_ID:
                            mMainPresenter.getWeather();
                            break;
                        case REQUEST_GET_LOCATION_ID:
                            mMainPresenter.getLocation();
                            break;
                        case REQUEST_GET_MAP_ID:
                            mMainPresenter.getLatLon();;
                            break;
                    }
                } else {
                    showDialog(DIALOG_ERROR);
                }
            }
        });
    }
    @Override
    public boolean checkInternet() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ERROR) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(R.string.error);
            adb.setMessage(R.string.internetConnection);
            adb.setPositiveButton(R.string.yes, myClickListener);
            adb.setNegativeButton(R.string.no, myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}
