package com.example.nikita.wappreborn.screen.main;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.example.nikita.wappreborn.R;
import com.example.nikita.wappreborn.data.model.Coordinates;
import com.example.nikita.wappreborn.screen.map.MapActivity;

public class MainActivity extends AppCompatActivity implements MainContract.IMainView {

    private static final int TEST_GIT = 2;
    private static final int ID_CLEARSKY = 800;
    private static final int ID_SUNNYCLOUD = 8;
    private static final int ID_CLOUDS = 7;
    private static final int ID_SNOW = 6;
    private static final int ID_RAIN = 5;
    private static final int ID_DRIZZLE = 3;
    private static final int ID_THUNDER = 2;
    private static final String CONDITION_FROM_API_LIGHT_SNOW = "light snow";
    private static final String CONDITION_FROM_API_LIGHT_RAIN_AND_SNOW = "light rain and snow";
    private static final String CONDITION_FROM_API_LIGHT_SHOWER_SNOW = "light shower snow";
    private static final String CONDITION_FROM_API_SNOW = "snow";
    private static final String CONDITION_FROM_API_RAIN_AND_SNOW = "rain and snow";
    private static final String CONDITION_FROM_API_SHOWER_SNOW = "shower snow";
    private static final String CONDITION_FROM_API_HEAVY_SNOW = "heavy snow";
    private static final String CONDITION_FROM_API_HEAVY_SHOWER_SNOW = "heavy shower snow";
    private static final String CONDITION_FROM_API_LIGHT_RAIN = "light rain";
    private static final String CONDITION_FROM_API_MODERATE_RAIN = "moderate rain";
    private final static int CONNECTION_ERROR = 1;
    private final static String SAVED_TEXT = "saved_text";
    private int mIconImageId;
    private int mBackgroundImageId;
    private double mLon;
    private double mLat;
    private String mCondition;
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
        mMainPresenter = new MainPresenter(this);
        loadCityToView();
        setNotEnabledButtonsInView();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mMainPresenter.start();
    }

    @Override
    public void showNetworkConnectionError() {
        showDialog(CONNECTION_ERROR);
    }

    @Override
    public void showEmptyLineError() {
        Toast.makeText(getApplicationContext(), "Entered city error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEnteredCityError() {
        Toast.makeText(getApplicationContext(), "Unknown city", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getCurrentDay() {
        return mCurrentDay;
    }

    @Override
    public Coordinates getCoordinates() {
        Coordinates coord;
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            coord = new Coordinates(latitude, longitude);
            return coord;
        }
        catch (Exception c){
            showNetworkConnectionError();
            return null;
        }
    }

    @Override
    public String getCityFromView() {
        String city = mCityEditText.getText().toString();
        return city;
    }

    @Override
    public void showLocationInView(String place) {
        mCityEditText.setText(place);
    }

    @Override
    public void showDateInView(String date) {
        mDateTextView.setText(date);
    }

    @Override
    public void showCityInView(String city) {
        mPlaceTextView.setText(city);
    }

    @Override
    public void showTemperatureDayInView(String temp) {
        mTempDayTextView.setText(temp);
    }

    @Override
    public void showTemperatureNightInView(String temp) {
        mTempNightTextView.setText(temp);
    }

    @Override
    public void showConditionInView(String condition) {
        mCondition = condition;
        mConditionTextView.setText(condition);
    }

    @Override
    public void showIconImageInView(int id) {
        mIconImageView.setImageResource(id);
    }

    @Override
    public void showImagesInView(int id) {
        if (id == ID_CLEARSKY) {
            mIconImageId = R.drawable.sunny;
            mBackgroundImageId = R.drawable.sunnybackground;
        } else {
            switch (id / 100) {
                case ID_SUNNYCLOUD:;
                    mIconImageId = R.drawable.sunnycloud;
                    mBackgroundImageId = R.drawable.sunnycloudbackground;
                    break;
                case ID_CLOUDS:
                    mIconImageId = R.drawable.cloud;
                    mBackgroundImageId = R.drawable.cloudsbackground;
                    break;
                case ID_SNOW:
                    mIconImageId = R.drawable.snow;
                    mBackgroundImageId = R.drawable.snowbackground;
                    break;
                case ID_RAIN:
                    mIconImageId = R.drawable.rain;
                    mBackgroundImageId = R.drawable.rainbackground;
                    break;
                case ID_DRIZZLE:
                    mIconImageId = R.drawable.drizzle;
                    mBackgroundImageId = R.drawable.drizzlebackground;
                    break;
                case ID_THUNDER:
                    mIconImageId = R.drawable.thunder;
                    mBackgroundImageId = R.drawable.thunderbackground;
                    break;
            }
        }
        if (mCondition.equals(CONDITION_FROM_API_LIGHT_SNOW) || mCondition.equals(CONDITION_FROM_API_LIGHT_RAIN_AND_SNOW) || mCondition.equals(CONDITION_FROM_API_LIGHT_SHOWER_SNOW)) {
            mIconImageId = R.drawable.smallsnow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(CONDITION_FROM_API_SNOW) || mCondition.equals(CONDITION_FROM_API_RAIN_AND_SNOW) || mCondition.equals(CONDITION_FROM_API_SHOWER_SNOW)) {
            mIconImageId = R.drawable.snow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(CONDITION_FROM_API_HEAVY_SNOW) || mCondition.equals(CONDITION_FROM_API_HEAVY_SHOWER_SNOW)) {
            mIconImageId = R.drawable.heavysnow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(CONDITION_FROM_API_LIGHT_RAIN)) {
            mIconImageId = R.drawable.rain;
            mBackgroundImageId = R.drawable.drizzlebackground;
        }
        else if (mCondition.equals(CONDITION_FROM_API_MODERATE_RAIN)) {
            mIconImageId = R.drawable.drizzle;
            mBackgroundImageId = R.drawable.rainbackground;
        }
        showBackgroundImageInView(mBackgroundImageId);
        showIconImageInView(mIconImageId);
    }

    @Override
    public void showBackgroundImageInView(int id){
        mLayout.setBackgroundResource(id);
    }

    @Override
    public void setCoordinates(Coordinates coord) {
        mLat = coord.latitude;
        mLon = coord.longitude;
        startMapActivity();
    }

    @Override
    public void defineViews() {
        mDateTextView =  findViewById(R.id.textViewDate);
        mTempDayTextView = findViewById(R.id.textViewDay);
        mTempNightTextView = findViewById(R.id.textViewNight);
        mPlaceTextView = findViewById(R.id.textViewPlace);
        mConditionTextView = findViewById(R.id.textViewCondition);
        mLessTextView = findViewById(R.id.textViewLess);
        mMoreTextView = findViewById(R.id.textViewMore);
        mIconImageView = findViewById(R.id.imageView);
        mLocationMarkerImageView = findViewById(R.id.imageViewMarker);
        mMapImageView = findViewById(R.id.imageViewMap);
        mCityEditText = findViewById(R.id.editText);
        mCheckButton = findViewById(R.id.buttonCheck);
        mPlusButton = findViewById(R.id.button2);
        mMinusButton = findViewById(R.id.button3);
        mLayout = findViewById(R.id.activity_main);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Hattori_Hanzo.otf");
        mPlaceTextView.setTypeface(typeFace);
        mTempDayTextView.setTypeface(typeFace);
        mTempNightTextView.setTypeface(typeFace);
        mDateTextView.setTypeface(typeFace);
        mConditionTextView.setTypeface(typeFace);
        mCheckButton.setTypeface(typeFace);
    }

    @Override
    public void startMapActivity(){
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("lat", mLat);
        intent.putExtra("lon", mLon);
        startActivity(intent);
    }

    @Override
    public void setEnabledButtonsInView() {
        mPlusButton.setEnabled(true);
        mMinusButton.setEnabled(true);
    }

    @Override
    public void setNotEnabledButtonsInView() {
        mPlusButton.setEnabled(false);
        mMinusButton.setEnabled(false);
    }

    @Override
    public void setListeners() {
        mMapImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String city = getCityFromView();
                mMainPresenter.fetchCoordinatesForMapActivity(city);
            }
        });

        mLocationMarkerImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Coordinates coord = getCoordinates();
                mMainPresenter.fetchCityWithCoordinates(coord);
            }
        });

        mCheckButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                mCurrentDay = 0;
                mMoreTextView.setAlpha(1);
                mLessTextView.setAlpha(0);
                saveCityFromView();
                String city = getCityFromView();
                mMainPresenter.fetchWeather(city);
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
    public void saveCityFromView() {
        mPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = mPref.edit();
        ed.putString(SAVED_TEXT, mCityEditText.getText().toString());
        ed.commit();
    }

    @Override
    public void loadCityToView() {
        mPref = getPreferences(MODE_PRIVATE);
        String savedText = mPref.getString(SAVED_TEXT, "");
        mCityEditText.setText(savedText);
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

    protected Dialog onCreateDialog(int id) {
        if (id == CONNECTION_ERROR) {
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

    @Override
    protected void onStop(){
        super.onStop();
        mMainPresenter.stop();
    }
}
