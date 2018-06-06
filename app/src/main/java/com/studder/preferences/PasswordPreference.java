package com.studder.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.LoginActivity;
import com.studder.R;
import com.studder.database.schema.UserTable;

public class PasswordPreference extends DialogPreference {

    private static final String TAG = "PasswordPreferenceLOG";

    EditText oldPassword;
    EditText newPasswordFirst;
    EditText newPasswordSecond;

    public PasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.password_preference);
        setDialogTitle(R.string.change_password_dialog_tittle);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        oldPassword = view.findViewById(R.id.edit_text_old_password);
        newPasswordFirst = view.findViewById(R.id.edit_text_first_password);
        newPasswordSecond = view.findViewById(R.id.edit_text_second_password);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
           Log.i(TAG, "Positive Result");
            String newFirstPw = newPasswordFirst.getText().toString();
            String newSecondPw = newPasswordSecond.getText().toString();
            String oldPw = oldPassword.getText().toString();
            if(newFirstPw.equals(newSecondPw)){
                SharedPreferences pref = getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                String username = pref.getString(UserTable.Cols.USERNAME, "Unknown");
                JsonObject json = new JsonObject();
                json.addProperty("username", username);
                json.addProperty("password", oldPw);
                json.addProperty("newPw", newFirstPw);

                Ion.with(getContext())
                        .load("http://10.0.2.2:8080/users/update")
                        .setJsonObjectBody(json)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                if(result.getHeaders().code() == 200){
                                    Log.i(TAG, result.getResult());
                                    if(!result.getResult().equals("-1")){
                                        Log.i(TAG, "updated");
                                        Toast.makeText(getContext(), R.string.change_password_success, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "bad old pw");
                                        Toast.makeText(getContext(), R.string.old_password_fail, Toast.LENGTH_SHORT).show();
                                    }
                                } else{
                                    Log.e(TAG, "server response != 200");
                                    Toast.makeText(getContext(), R.string.old_password_fail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Log.e(TAG, "First and second don't match....");
                Toast.makeText(getContext(), R.string.two_new_passwords_fail, Toast.LENGTH_SHORT).show();
            }



        }
    }
}
