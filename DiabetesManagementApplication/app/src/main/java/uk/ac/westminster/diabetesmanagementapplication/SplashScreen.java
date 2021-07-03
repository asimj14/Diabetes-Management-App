package uk.ac.westminster.diabetesmanagementapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER = 5000;
    //Variables
    ImageView splashscreen;
    TextView poweredByLine;

    //Animations
    Animation sideAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Hooks --> hook our design elements with our java code
        splashscreen = findViewById(R.id.splashscreen);
        poweredByLine = findViewById(R.id.powered_by_line);

        //Animations Hooks
        sideAnim = AnimationUtils.loadAnimation(this,R.anim.side_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //Set animations on elements
        splashscreen.setAnimation(sideAnim);
        poweredByLine.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                //Activity finished so destroy it and move to next activity so won't be able to comeback
                finish();
            }
        }, SPLASH_TIMER);


    }
}