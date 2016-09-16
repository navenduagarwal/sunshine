package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_DEGREE = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;
    private String mForecast;
    private Uri mUri;
    private ImageView mIconView;
    private TextView mDateView;
    private TextView mDescView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mHumidityLabelView;
    private TextView mWindView;
    private TextView mWindLabelView;
    private TextView mPressureView;
    private TextView mPressureLabelView;
    private WindSpeedView windSpeedView;
    private WindDirectionView windDirectionView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_start, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mDescView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mHumidityLabelView = (TextView) rootView.findViewById(R.id.detail_humidity_label_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mPressureLabelView = (TextView) rootView.findViewById(R.id.detail_pressure_label_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mWindLabelView = (TextView) rootView.findViewById(R.id.detail_wind_label_textview);
//        windSpeedView = (WindSpeedView) rootView.findViewById(R.id.detail_windCompass);
//        windDirectionView = (WindDirectionView) rootView.findViewById(R.id.detail_windDirection);
        return rootView;
    }

    private void finishCreatingMenu(Menu menu) {
        //Retrieve the share menu item
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        if (mForecast != null) {
            item.setIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() instanceof DetailActivity) {
            inflater.inflate(R.menu.detailfragment, menu);
            finishCreatingMenu(menu);
        }
    }

    private Intent createShareForecastIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return intent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged(String newLocation) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In OnCreateLoader");
        if (mUri != null) {
            //Now create and return cursorLoader that will take care of
            //creating cursor for data that being displayed.

            return new CursorLoader(getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }
        ViewParent vp = getView().getParent();
        if(vp instanceof CardView){
            ((View)vp).setVisibility(View.INVISIBLE);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (data != null && data.moveToFirst()) {
            ViewParent vp = getView().getParent();
            if(vp instanceof CardView){
                ((View)vp).setVisibility(View.VISIBLE);
            }
            //read date from Cursor and update view for day of the week and date
            long date = data.getLong(COL_WEATHER_DATE);
            //Formatted date
            String dateString = Utility.getFullFriendlyDayString(getActivity(), date);
            //Set date for the views
            mDateView.setText(dateString);

            //Read weather condition id from Cursor
            int weatherConditionId = data.getInt(COL_WEATHER_CONDITION_ID);

            // Read weather desc from cursor
            String weatherDescription = Utility.getStringForWeatherCondition(getActivity(), weatherConditionId);
            //set desc to view
            mDescView.setText(weatherDescription);
            mDescView.setContentDescription(getString(R.string.a11y_forecast, weatherDescription));

            if (Utility.usingLocalGraphics(getActivity())) {
                mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherConditionId));
            } else {
                Glide.with(this)
                        .load(Utility.getArtUrlForWeatherCondition(getActivity(), weatherConditionId))
                        .error(Utility.getArtResourceForWeatherCondition(weatherConditionId))
                        .crossFade()
                        .into(mIconView);
            }
            //for accessibility add a content description to the icon field
            mIconView.setContentDescription(getString(R.string.a11y_forecast_icon, weatherDescription));

            //Read temperatures from cursor
            boolean isMetric = Utility.isMetric(getActivity());
            String high = Utility.formatTemperature(getContext(), data.getDouble(COL_WEATHER_MAX_TEMP));
            String low = Utility.formatTemperature(getContext(), data.getDouble(COL_WEATHER_MIN_TEMP));
            //set the temperature views
            mHighTempView.setText(high);
            mHighTempView.setContentDescription(getString(R.string.a11y_high_temp, high));
            mLowTempView.setText(low);
            mLowTempView.setContentDescription(getString(R.string.a11y_low_temp, low));

            //Read humidity from cursor
            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
            //set the view
            mHumidityView.setText(getString(R.string.format_humidity,humidity));
            mHumidityView.setContentDescription(getString(R.string.a11y_humidity,mHumidityView.getText()));
            mHumidityLabelView.setContentDescription(mHumidityView.getContentDescription());

            //Read Wind values from cursor
            float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
            float windDirection = data.getFloat(COL_WEATHER_DEGREE);
            String formattedWindSpeed = Utility.getFormattedWind(getContext(), windSpeed, windDirection);
            //Set the wind view
            mWindView.setText(formattedWindSpeed);
            mWindView.setContentDescription(getString(R.string.a11y_wind,formattedWindSpeed));
            mWindLabelView.setContentDescription(mWindView.getContentDescription());

//            windSpeedView.setSpeed(windSpeed);
//            windSpeedView.setContentDescription(getResources().getString(R.string.anim_wind_speed) + windSpeed);
//            windDirectionView.setWindDirection(windDirection);
//            windDirectionView.setContentDescription(getResources().getString(R.string.anim_wind_direction) + windDirection);

            //Read pressure values from cursor
            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            //set view for pressure
            mPressureView.setText(getString(R.string.format_pressure,pressure));
            mPressureView.setContentDescription(getString(R.string.a11y_pressure,mPressureView.getText()));
            mPressureLabelView.setContentDescription(mPressureView.getContentDescription());


            //we need to update the share intent now
            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);

        // We need to start the enter transition after the data has loaded
        if (activity instanceof DetailActivity) {
            activity.supportStartPostponedEnterTransition();

            if ( null != toolbarView ) {
                activity.setSupportActionBar(toolbarView);

                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            if ( null != toolbarView ) {
                Menu menu = toolbarView.getMenu();
                if ( null != menu ) menu.clear();
                toolbarView.inflateMenu(R.menu.detailfragment);
                finishCreatingMenu(toolbarView.getMenu());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
