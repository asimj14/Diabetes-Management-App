package uk.ac.westminster.diabetesmanagementapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    Button forgotPassword, signUp;
    CheckBox remember;
    SQLiteDatabase sqLiteDatabase;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.usernameLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        forgotPassword = (Button) findViewById(R.id.forgotPassword);
        signUp = (Button) findViewById(R.id.signUp);
        remember = (CheckBox) findViewById(R.id.rememberme);


        //forgot password link to
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
                startActivity(intent);

            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }

        });

        db = new DBHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPass = password.getText().toString();
                //Validations
                if (userEmail.equals("") || userPass.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter the credentials!", Toast.LENGTH_SHORT).show();

                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                } else {
                    //All fields filled and validated
                    Boolean result = null;
                    try {
                        result = db.checkUserCredentials(userEmail, userPass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == true) {
                        //User is valid
                        getUserDetails(userEmail);
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        //Invalid user
                        Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        //it will check if value is true then will redirect to HomeActivity
        //else to LoginActivity
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")) {

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        } else if (checkbox.equals("false")) {
            Toast.makeText(this, "Please Sign In!", Toast.LENGTH_SHORT).show();
        }

        //Shared Prefernences Object --> creates a file in our storage
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);//so that only our app can read this preference
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Checked", Toast.LENGTH_SHORT).show();

                } else if (!compoundButton.isChecked()) {

                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);//so that only our app can read this preference
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getUserDetails(String email) {
        String userName = "";
        sqLiteDatabase = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});

        if (cursor.getCount() > 0) {

            if (cursor.moveToNext()) {

                SharedPreferences preferences1 = getSharedPreferences("useriddetails", MODE_PRIVATE);//so that only our app can read this preference
                SharedPreferences.Editor editor1 = preferences1.edit();
                editor1.putInt("userid", cursor.getInt(0));
                editor1.apply();


                SharedPreferences preferences2 = getSharedPreferences("usernamedetails", MODE_PRIVATE);//so that only our app can read this preference
                SharedPreferences.Editor editor2 = preferences2.edit();
                editor2.putString("username", cursor.getString(1));
                editor2.apply();

                SharedPreferences preferences3 = getSharedPreferences("useremaildetails", MODE_PRIVATE);//so that only our app can read this preference
                SharedPreferences.Editor editor = preferences3.edit();
                editor.putString("useremail", cursor.getString(2));
                editor.apply();


            } else {
                return;
            }

        }

    }

}
