package com.studder.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.R;

public class PasswordPreference extends DialogPreference {

    private static final String TAG = "PasswordPreferenceLOG";

    EditText oldPassword;
    EditText newPasswordFirst;
    EditText newPasswordSecond;

    public PasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.password_preference);
    }

        /*protected View onCreateDialogView(){
            View v = LayoutInflater.from(getContext()).inflate(R.layout.password_preference, null);
            oldPassword = v.findViewById(R.id.edit_text_old_password);
            newPasswordFirst = v.findViewById(R.id.edit_text_first_password);
            newPasswordSecond = v.findViewById(R.id.edit_text_second_password);
            return v;
        }*/

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
           Log.i(TAG, "SUCCESS");
           //update user, rest call to server
            //username needs to be sended, currently don't have it in sharedpref/mysqllite
            //if oldpw == pravi pw
            String newFirstPw = newPasswordFirst.getText().toString();
            String newSecondPw = newPasswordSecond.getText().toString();
            if(newFirstPw.equals(newSecondPw)){
                JsonObject json = new JsonObject();
                json.addProperty("username", "PROMENI POSLE");
                json.addProperty("password", newFirstPw);

                Ion.with(getContext())
                        .load("http://10.0.2.2:8080/users")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                if(result.getHeaders().code() == 200){
                                    Log.i(TAG, "updated");
                                } else{
                                    Log.e(TAG, "server response != 200");
                                }
                            }
                        });
            } else {
                Log.e(TAG, "First and second don't match....");
            }



        }
    }
}
