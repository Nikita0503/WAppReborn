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
import android.util.Log;
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
import com.example.nikita.wappreborn.data.model.OpenWeatherMap;
import com.example.nikita.wappreborn.screen.map.MapActivity;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private static final int ID_CLEARSKY = 800;
    private static final int ID_SUNNYCLOUD = 8;
    private static final int ID_CLOUDS = 7;
    private static final int ID_SNOW = 6;
    private static final int ID_RAIN = 5;
    private static final int ID_DRIZZLE = 3;
    private static final int ID_THUNDER = 2;
    private static final double CONST_FOR_TRANSLATION_TEMPERATURE_1 = 1.8;
    private static final double CONST_FOR_TRANSLATION_TEMPERATURE_2 = 459.67;
    private static final double CONST_FOR_TRANSLATION_TEMPERATURE_3 = 32;
    private static final double CONST_FOR_TRANSLATION_TEMPERATURE_4 = 0.55555555556;
    private static final String MONDAY = "Mon";
    private static final String TUESDAY = "Tue";
    private static final String WEDNESDAY = "Wed";
    private static final String THURSDAY = "Thu";
    private static final String FRIDAY = "Fri";
    private static final String SATURDAY = "Sat";
    private static final String SUNDAY = "Sun";
    private OpenWeatherMap mWeather;

    private int mIconImageId;
    private int mBackgroundImageId;
    private double mLon;
    private double mLat;
    private String mCondition;
    private int mCurrentDay;
    private Presenter mMainPresenter;
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
        mMainPresenter = new Presenter(this);
        loadCityToView();
        setNotEnabledButtonsInView();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mMainPresenter.start();
    }

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
    public void defineViews() {
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Hattori_Hanzo.otf");
        mDateTextView = findViewById(R.id.textViewDate);
        mDateTextView.setTypeface(typeFace);
        mTempDayTextView = findViewById(R.id.textViewDay);
        mTempDayTextView.setTypeface(typeFace);
        mTempNightTextView = findViewById(R.id.textViewNight);
        mTempNightTextView.setTypeface(typeFace);
        mPlaceTextView = findViewById(R.id.textViewPlace);
        mPlaceTextView.setTypeface(typeFace);
        mConditionTextView = findViewById(R.id.textViewCondition);
        mConditionTextView.setTypeface(typeFace);
        mLessTextView = findViewById(R.id.textViewLess);
        mMoreTextView = findViewById(R.id.textViewMore);
        mIconImageView = findViewById(R.id.imageView);
        mLocationMarkerImageView = findViewById(R.id.imageViewMarker);
        mMapImageView = findViewById(R.id.imageViewMap);
        mCityEditText = findViewById(R.id.editText);
        mCheckButton = findViewById(R.id.buttonCheck);
        mCheckButton.setTypeface(typeFace);
        mPlusButton = findViewById(R.id.button2);
        mMinusButton = findViewById(R.id.button3);
        mLayout = findViewById(R.id.activity_main);
    }

    @Override
    public void showNetworkConnectionError() {
        showDialog(getResources().getInteger(R.integer.CONNECTION_ERROR));
    }

    @Override
    public void showEmptyLineError() {
        Toast.makeText(MainActivity.this, getString(R.string.entered_city_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEnteredCityError() {
        Toast.makeText(MainActivity.this, getString(R.string.unknown_city_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setEnabledButtonsInView() {
        mPlusButton.setEnabled(true);
        mMinusButton.setEnabled(true);
    }
    public void setNotEnabledButtonsInView() {
        mPlusButton.setEnabled(false);
        mMinusButton.setEnabled(false);
    }

    @Override
    public void showLocationInView(String place) {
        mCityEditText.setText(place);
    }

    @Override
    public void showCoordinatesInView(Coordinates coord) {
        mLat = coord.latitude;
        mLon = coord.longitude;
        startMapActivity();
    }

    public void startMapActivity(){
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra(getResources().getResourceName(R.string.lat), mLat);
        intent.putExtra(getResources().getResourceName(R.string.lon), mLon);
        startActivity(intent);
    }

    public void setListeners() {
        mMapImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String city = getCityFromView();
                mMainPresenter.fetchCoordinatesByCity(city);
            }
        });

        mLocationMarkerImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                try {
                    Coordinates coord = getCoordinates();
                    mMainPresenter.fetchCityByCoordinates(coord);
                }catch (Exception c){
                    Log.e(getString(R.string.error), c.getLocalizedMessage());
                    showEnteredCityError();
                }
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
                setUpDateTextView();
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
                setUpDateTextView();
            }
        });
    }

    public void loadCityToView() {
        mPref = getPreferences(MODE_PRIVATE);
        String savedText = mPref.getString(getString(R.string.SAVED_TEXT), "");
        mCityEditText.setText(savedText);
    }

    @Override
    public void showWeatherInView(OpenWeatherMap weather){
        mWeather = weather;
        setUpDateTextView();
    }

    public void setUpDateTextView(){
        showDateInView(getDate());
        showCityInView(getCity());
        showTemperatureDayInView(getTemperatureDay());
        showTemperatureNightInView(getTemperatureNight());
        showConditionInView(getCondition());
        showImagesInView(getImagesId());
    }

    public int getCurrentDay() {
        return mCurrentDay;
    }

    public String getDate() {
        long dateLong = mWeather.getList().get(getCurrentDay()).getDt();
        Date date = convertUnixTimestampToDate(dateLong);
        String dateStr = date.toString();
        String dayOfWeek = "";
        switch (dateStr.substring(getResources().getInteger(R.integer.BEGINNING_OF_DAY_NAME_FROM_API),
                getResources().getInteger(R.integer.END_OF_DAY_NAME_FROM_API))) {
            case MONDAY:
                dayOfWeek = getString(R.string.monday);
                break;
            case TUESDAY:
                dayOfWeek = getString(R.string.tuesday);
                break;
            case WEDNESDAY:
                dayOfWeek = getString(R.string.wednesday);
                break;
            case THURSDAY:
                dayOfWeek = getString(R.string.thursday);
                break;
            case FRIDAY:
                dayOfWeek = getString(R.string.friday);
                break;
            case SATURDAY:
                dayOfWeek = getString(R.string.saturday);
                break;
            case SUNDAY:
                dayOfWeek = getString(R.string.sunday);
                break;
        }
        return dateStr.substring(getResources().getInteger(R.integer.BEGINNING_OF_DAY_NAME),
                getResources().getInteger(R.integer.END_OF_DAY_NAME)) + " " + dayOfWeek;
    }

    public String getCity() {
        return mWeather.getCity().getName() + " " + mWeather.getCity().getCountry();
    }

    public String getTemperatureDay() {
        double tempDouble = mWeather.getList().get(getCurrentDay()).getTemp().getDay();
        int temp = (int) ((((tempDouble * CONST_FOR_TRANSLATION_TEMPERATURE_1 - CONST_FOR_TRANSLATION_TEMPERATURE_2))
                - CONST_FOR_TRANSLATION_TEMPERATURE_3) * CONST_FOR_TRANSLATION_TEMPERATURE_4);
        if (temp > 0) {
            return String.valueOf(getString(R.string.day_positive) + temp + getString(R.string.cesium));
        }
        else{
            return String.valueOf(String.valueOf(getString(R.string.day_negative) + " " + temp + getString(R.string.cesium)));
        }
    }

    public String getTemperatureNight() {
        double tempDouble = mWeather.getList().get(getCurrentDay()).getTemp().getNight();
        int temp = (int) ((((tempDouble * CONST_FOR_TRANSLATION_TEMPERATURE_1 - CONST_FOR_TRANSLATION_TEMPERATURE_2))
                - CONST_FOR_TRANSLATION_TEMPERATURE_3) * CONST_FOR_TRANSLATION_TEMPERATURE_4);
        if (temp > 0) {
            return String.valueOf(getString(R.string.night_positive)  + temp + getString(R.string.cesium));
        }
        else {
            return String.valueOf(getString(R.string.night_negetive) + " " + temp + getString(R.string.cesium));
        }
    }

    public String getCondition() {
        String condition = mWeather.getList().get(getCurrentDay()).getWeather().get(0).getDescription();
        return condition;
    }


    public int getImagesId() {
        return mWeather.getList().get(getCurrentDay()).getWeather().get(0).getId();
    }

    public void showDateInView(String date) {
        mDateTextView.setText(date);
    }

    public void showCityInView(String city) {
        mPlaceTextView.setText(city);
    }

    public void showTemperatureDayInView(String temp) {
        mTempDayTextView.setText(temp);
    }

    public void showTemperatureNightInView(String temp) {
        mTempNightTextView.setText(temp);
    }

    public void showConditionInView(String condition) {
        mCondition = condition;
        mConditionTextView.setText(condition);
    }

    public void showIconImageInView(int id) {
        mIconImageView.setImageResource(id);
    }

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
        if (mCondition.equals(getString(R.string.CONDITION_FROM_API_LIGHT_SNOW))
                || mCondition.equals(getString(R.string.CONDITION_FROM_API_LIGHT_RAIN_AND_SNOW))
                || mCondition.equals(getString(R.string.CONDITION_FROM_API_LIGHT_SHOWER_SNOW))) {
            mIconImageId = R.drawable.smallsnow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(getString(R.string.CONDITION_FROM_API_SNOW))
                || mCondition.equals(getString(R.string.CONDITION_FROM_API_RAIN_AND_SNOW))
                || mCondition.equals(getString(R.string.CONDITION_FROM_API_SHOWER_SNOW))) {
            mIconImageId = R.drawable.snow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(getString(R.string.CONDITION_FROM_API_HEAVY_SNOW))
                || mCondition.equals(getString(R.string.CONDITION_FROM_API_HEAVY_SHOWER_SNOW))) {
            mIconImageId = R.drawable.heavysnow;
            mBackgroundImageId = R.drawable.snowbackground;
        }
        else if (mCondition.equals(getString(R.string.CONDITION_FROM_API_LIGHT_RAIN))) {
            mIconImageId = R.drawable.rain;
            mBackgroundImageId = R.drawable.drizzlebackground;
        }
        else if (mCondition.equals(getString(R.string.CONDITION_FROM_API_MODERATE_RAIN))) {
            mIconImageId = R.drawable.drizzle;
            mBackgroundImageId = R.drawable.rainbackground;
        }
        showBackgroundImageInView(mBackgroundImageId);
        showIconImageInView(mIconImageId);
    }

    public void showBackgroundImageInView(int id){
        mLayout.setBackgroundResource(id);
    }

    public Coordinates getCoordinates() {
        Coordinates coord;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location!=null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            coord = new Coordinates(latitude, longitude);
            return coord;
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.location_error), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public String getCityFromView() {
        return mCityEditText.getText().toString();
    }

    public void saveCityFromView() {
        mPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = mPref.edit();
        ed.putString(getString(R.string.SAVED_TEXT), mCityEditText.getText().toString());
        ed.commit();
    }

    protected Dialog onCreateDialog(int id) {
        if (id == getResources().getInteger(R.integer.CONNECTION_ERROR)) {
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

    public static Date convertUnixTimestampToDate(long timestamp) {
        return new Date(timestamp * 1000L);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mMainPresenter.stop();
    }
}
