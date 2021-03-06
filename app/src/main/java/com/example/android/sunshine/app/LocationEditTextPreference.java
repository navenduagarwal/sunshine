package com.example.android.sunshine.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by navendu on 7/17/2016.
 */
public class LocationEditTextPreference extends EditTextPreference {
    private static final int DEFAULT_MIN_LOCATION_LENGTH = 2;
    private int mMinLength;

    public LocationEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.LocationEditTextPreference, 0, 0);
        try {
            mMinLength = array.getInteger(R.styleable.LocationEditTextPreference_minLength, DEFAULT_MIN_LOCATION_LENGTH);
        } finally {
            array.recycle();
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        EditText editText = getEditText();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Dialog d = getDialog();
                if (d instanceof AlertDialog) {
                    AlertDialog dialog = (AlertDialog) d;
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    //Check if the EditText is empty
                    if (editable.length() < mMinLength) {
                        //Disable OK Button
                        positiveButton.setEnabled(false);
                    } else {
                        // Re-enable OK Button
                        positiveButton.setEnabled(true);
                    }
                }
            }
        });

    }
}
