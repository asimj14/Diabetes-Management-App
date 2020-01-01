package uk.ac.westminster.diabetesmanagementapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    EditText username, password, email, repassword, dateBirth;
    Button btnSignUp, btnSignIn;
    DBHelper db;
    String encryptedPass, encryptedRepass;
    RadioGroup radioGroup;
    RadioButton radioButtonM, radioButtonF;
    String myGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        radioButtonM = (RadioButton) findViewById(R.id.radiobuttonMale);
        radioButtonF = (RadioButton) findViewById(R.id.radiobuttonFemale);

        dateBirth = (EditText) findViewById(R.id.dateBirth);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        dateBirth.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        db = new DBHelper(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUser = username.getText().toString();
                String myEmail = email.getText().toString();
                String myPass = password.getText().toString();
                String myRepass = repassword.getText().toString();
                String myDateBirth = dateBirth.toString();
                if (radioButtonM.isChecked()) {
                    myGender = "Male";
                } else if (radioButtonF.isChecked()) {
                    myGender = "Female";
                } else {
                    Toast.makeText(MainActivity.this, "No gender selected!!", Toast.LENGTH_SHORT).show();
                }


                try {
                    encryptedPass = db.encrypt(myPass, myPass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    encryptedRepass = db.encrypt(myRepass, myRepass);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //Checking if empty fields
                if (myUser.equals("") || myEmail.equals("") || myPass.equals("") || myRepass.equals("") || myDateBirth.equals("") || (!radioButtonM.isChecked() && !radioButtonF.isChecked())) {
                    Toast.makeText(MainActivity.this, "Please fill all the required fields!", Toast.LENGTH_SHORT).show();
                } else {
                    if (myPass.equals(myRepass)) {
                        Boolean validUserResult = db.checkUserEmail(myEmail);
                        if (validUserResult == false) {
                            //No record found for that particular user so we can register
                            Boolean result = null;
                            try {
                                result = db.insertUser(myUser, myEmail, myPass, myDateBirth, myGender);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                            }
                            if (result == true) {
                                //Toast.makeText(MainActivity.this, "encrpted pass: "+shaPass.toString(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "Gender:" + myGender, Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "encrpted pass: " + encryptedPass.toString(), Toast.LENGTH_SHORT).show();

                                Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                //move to login page
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(MainActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            //user already exists
                            Toast.makeText(MainActivity.this, "User already exists! \n Please Sign in with a valid email", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }


//    public void onSubmit(View view){
//
//        AlertDialog.Builder exitBuilder = new AlertDialog.Builder(this);
//
//        exitBuilder.setTitle("Exit ?");
//        exitBuilder.setMessage("Are you sure you want to Exit ?");
//        exitBuilder.setCancelable(false);
//        exitBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                MainActivity.this.finish();
//            }
//        });
//        exitBuilder.setNegativeButton("No", null)
//                .show();
//
//    }
}