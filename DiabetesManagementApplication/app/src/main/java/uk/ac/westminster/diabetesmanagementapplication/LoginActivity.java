package uk.ac.westminster.diabetesmanagementapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

import uk.ac.westminster.diabetesmanagementapplication.DBHelper;
import uk.ac.westminster.diabetesmanagementapplication.HomeActivity;
import uk.ac.westminster.diabetesmanagementapplication.R;
public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    TextView forgotPassword;
    TextView signUp;
    CheckBox remember;
    SQLiteDatabase sqLiteDatabase;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.usernameLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        forgotPassword = (TextView)findViewById(R.id.forgotPassword);
        signUp = (TextView)findViewById(R.id.signUp);
        remember = (CheckBox)findViewById(R.id.rememberme);


        //forgot password link to
        forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
        //signUp.setMovementMethod(LinkMovementMethod.getInstance());
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
                String userName="";


                //Toast.makeText(LoginActivity.this, "Details are:"+user+pass, Toast.LENGTH_SHORT).show();
                if(userEmail.equals("") || userPass.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter the credentials!", Toast.LENGTH_SHORT).show();

                }else{

                    //All fields filled
                    Boolean result = null;
                    try {
                        result = db.checkUserCredentials(userEmail,userPass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(result==true){

//                        sqLiteDatabase = db.getWritableDatabase();
//                        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE email=?", new String[]{userEmail});
//
//                        if (cursor.getCount() > 0) {
//
//                            userEmail = cursor.getString(1);
//                            userName = cursor.getString(2);
                             getUserDetails(userEmail);

                            SharedPreferences preferences = getSharedPreferences("userdetails",MODE_PRIVATE);//so that only our app can read this preference
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("useremail",userEmail);
                            editor.putString("username", userName);
                            editor.apply();

                       // }
                        //User is valid
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                    }else{

                        //Error message
                        Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        //it will check if value is true then will redirect to HomeActivity
        //else to LoginActivity

        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")){

            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            

        }else if(checkbox.equals("false")){
            Toast.makeText(this, "Please Sign In!", Toast.LENGTH_SHORT).show();

        }

        //Shared Prefernences Object --> creates a file in our storage
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);//so that only our app can read this preference
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Checked", Toast.LENGTH_SHORT).show();

                }else if(!compoundButton.isChecked()){

                    SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);//so that only our app can read this preference
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void getUserDetails(String email) {
        String userEmail="";
        String userName="";
        sqLiteDatabase = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});

        if (cursor.getCount() > 0) {

            if(cursor.moveToNext()) {

                SharedPreferences preferences = getSharedPreferences("useremaildetails",MODE_PRIVATE);//so that only our app can read this preference
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("useremail",cursor.getString(2));
                editor.apply();

                SharedPreferences preferences1 = getSharedPreferences("usernamedetails",MODE_PRIVATE);//so that only our app can read this preference
                SharedPreferences.Editor editor1 = preferences1.edit();
                editor1.putString("username",cursor.getString(1));
                editor1.apply();




            }else{
                return;
            }

        }

    }

    }
