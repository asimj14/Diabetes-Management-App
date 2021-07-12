package uk.ac.westminster.diabetesmanagementapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Update;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateDetails extends AppCompatActivity {

    TextView textViewNewPassword, textViewRePassword;
    Button btnUpdateDetails;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);
        db = new DBHelper(this);
        textViewNewPassword = findViewById(R.id.newPassword);
        textViewRePassword = findViewById(R.id.reNewPassword);
        btnUpdateDetails = findViewById(R.id.btnUpdatePassword);


        SharedPreferences preferences = getSharedPreferences("userreset", MODE_PRIVATE);
        String userEmail = preferences.getString("useremail", "");

        btnUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = textViewNewPassword.getText().toString();
                String rePassword = textViewRePassword.getText().toString();

                //Checking if empty fields
                if ((newPassword.isEmpty()) || (rePassword.isEmpty())) {
                    Toast.makeText(UpdateDetails.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                } else if (!(newPassword.equals(rePassword))) {
                    Toast.makeText(UpdateDetails.this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean result = null;
                    try {
                        result = db.updatePassword(userEmail, newPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == true) {

                        Toast.makeText(UpdateDetails.this, "Update Password Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateDetails.this, LoginActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(UpdateDetails.this, "Update Failed!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
}