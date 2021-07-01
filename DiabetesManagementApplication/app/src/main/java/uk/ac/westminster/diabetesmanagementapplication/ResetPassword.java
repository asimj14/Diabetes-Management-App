package uk.ac.westminster.diabetesmanagementapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPassword extends AppCompatActivity {

    TextView textviewEmail;
    Button btnResetPassword;
    String userEmail;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        db = new DBHelper(this);
        textviewEmail = (TextView) findViewById(R.id.resetEmail);
        btnResetPassword = (Button) findViewById(R.id.btnResetMyPassword);


        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = textviewEmail.getText().toString();


                //Checking if empty fields
                if (userEmail.equals("")) {
                    Toast.makeText(ResetPassword.this, "Please fill the required field!", Toast.LENGTH_SHORT).show();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(ResetPassword.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                } else {

                    Boolean validUserResult = db.checkUserEmail(userEmail);
                    if (validUserResult == true) {
                        //Record found
                        Intent intent = new Intent(ResetPassword.this, UpdateDetails.class);
                        SharedPreferences preferences = getSharedPreferences("userreset", MODE_PRIVATE);//so that only our app can read this preference
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("useremail", userEmail);
                        editor.apply();
                        startActivity(intent);

                    }else{
                        Toast.makeText(ResetPassword.this, "Email not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}