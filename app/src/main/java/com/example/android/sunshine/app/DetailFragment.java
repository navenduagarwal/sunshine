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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    private ShareActionProvider mShareActionProvider;
    private Uri mUri;
    private ImageView dIconView;
    private TextView dFriendlyDayView;
    private TextView dDateView;
    private TextView dDescView;
    private TextView dHighTempView;
    private TextView dLowTempView;
    private TextView dHumidityView;
    private TextView dWindView;
    private TextView dPressureView;
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

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        dIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        dFriendlyDayView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        dDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        dDescView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        dHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        dLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        dHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        dPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        dWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        windSpeedView = (WindSpeedView) rootView.findViewById(R.id.detail_windCompass);
        windDirectionView = (WindDirectionView) rootView.findViewById(R.id.detail_windDirection);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (data != null && data.moveToFirst()) {

            //Read weather condition id from Cursor
            int weatherConditionId = data.getInt(COL_WEATHER_CONDITION_ID);
            //use placeholder image
            dIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherConditionId));

            //read date from Cursor and update view for day of the week and date
            long date = data.getLong(COL_WEATHER_DATE);
            //Day for the date
            String day = Utility.getDayName(getContext(), date);
            //Formatted date
            String dateString = Utility.getFormattedMonthDay(getActivity(), date);
            //Set date for the views
            dFriendlyDayView.setText(day);
            dDateView.setText(dateString);

            // Read weather desc from cursor
            String weatherDescription = data.getString(COL_WEATHER_DESC);
            //set desc to view
            dDescView.setText(weatherDescription);

            //for accessibility add a content description to the icon field
            dIconView.setContentDescription(weatherDescription);

            //Read temperatures from cursor
            boolean isMetric = Utility.isMetric(getActivity());
            String high = Utility.formatTemperature(getContext(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = Utility.formatTemperature(getContext(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
            //set the temperature views
            dHighTempView.setText(high);
            dLowTempView.setText(low);

            //Read humidity from cursor
            String humidity = String.format(getString(R.string.format_humidity), data.getFloat(COL_WEATHER_HUMIDITY));
            //set the view
            dHumidityView.setText(humidity);

            //Read Wind values from cursor
            float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
            float windDirection = data.getFloat(COL_WEATHER_DEGREE);
            String formattedWindSpeed = Utility.getFormattedWind(getContext(), windSpeed, windDirection);
            //Set the wind view
            dWindView.setText(formattedWindSpeed);
            //windSpeedView.setDegrees(windDirection);
            windSpeedView.setSpeed(windSpeed);
            windSpeedView.setContentDescription(getResources().getString(R.string.anim_wind_speed) + windSpeed);
            windDirectionView.setWindDirection(windDirection);
            windDirectionView.setContentDescription(getResources().getString(R.string.anim_wind_direction) + windDirection);

            //Read pressure values from cursor
            String pressure = String.format(getString(R.string.format_pressure), data.getFloat(COL_WEATHER_PRESSURE));
            //set view for pressure
            dPressureView.setText(pressure);


            //If onCreateOptionsMenu has already happened, we need to update the share intent now
            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
