package com.aviv_pos.olgats.avivinventory;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aviv_pos.olgats.avivinventory.async.InitTask;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "ALogin";
    private EditText etUser;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressDialog progress;
    private static final String ACTION_FOR_INTENT_CALLBACK = "THIS_IS_A_UNIQUE_KEY_WE_USE_TO_COMMUNICATE_WITH_INITTASK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String languageToLoad = "he"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_login);
        etUser = (EditText) findViewById(R.id.etUser);
        etPassword = (EditText) findViewById(R.id.etPwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(v);
                login();
            }
        });
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        };
        etPassword.setOnEditorActionListener(onEditorActionListener);
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            btnLogin.setEnabled(true);
            return;
        }

        btnLogin.setEnabled(false);

        String user = etUser.getText().toString();
        String password = etPassword.getText().toString();
        final InitTask task = new InitTask(this, ACTION_FOR_INTENT_CALLBACK);
        task.execute(user, password);

        progress = new ProgressDialog(LoginActivity.this);
        progress.setIndeterminate(true);
        progress.setMessage(getString(R.string.authenticating));
        progress.setCancelable(false);
        progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                task.cancel(true);
                btnLogin.setEnabled(true);
            }
        });
        progress.show();


        // TODO: Implement your own authentication logic here.

       /* new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progress.dismiss();
                    }
                }, 3000);*/
    }

    public void setProgressMessage(String msg) {
        if (progress != null) {
            progress.setMessage(msg);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }
            int response = intent.getIntExtra(InitTask.RESPONSE, -1);
            switch (response) {
                case WSConstants.SUCCESS:
                    onLoginSuccess();
                    break;
                default:
                    onLoginFailed(intent.getStringExtra(InitTask.RESPONSE_MSG));
                    break;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), msg != null ? msg : "Login failed", Toast.LENGTH_LONG).show();

        btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = etUser.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty()) {
            etUser.setError("enter a valid user value");
            valid = false;
        } else {
            etUser.setError(null);
        }

        if (password.isEmpty()) {
            etPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }
}
