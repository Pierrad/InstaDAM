package com.example.instadam.auth;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;

import com.example.instadam.R;

/**
 * Class containing a function to change the visibility of the password
 */
public class Utilities {

    private final Context context;

    public Utilities(Context context) {
        this.context = context;
    }

    /**
     * Called when the password icon is pressed.
     *
     * Change visibility of password and change icon
     *
     * @param password  The password
     * @param showHidePassword The icon
     */
    public void changeVisibility(EditText password, Button showHidePassword) {
        Log.d("Utilities: ", "changeVisibility()");

        if (password.getInputType() == InputType.TYPE_CLASS_TEXT) {
            showHidePassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password_not_show, 0, 0, 0);
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            showHidePassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password_show, 0, 0, 0);
            password.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        password.setTypeface(ResourcesCompat.getFont(context, R.font.poppins));
    }

}
