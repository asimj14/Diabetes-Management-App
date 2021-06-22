package uk.ac.westminster.diabetesmanagementapplication;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
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
                        //User is valid
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        Bundle bundle = new Bundle();
                        bundle.putString("email",userEmail);

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("userdetails",bundle);
                        startActivity(intent);

                    }else{

                        //Error message
                        Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });




    }
}