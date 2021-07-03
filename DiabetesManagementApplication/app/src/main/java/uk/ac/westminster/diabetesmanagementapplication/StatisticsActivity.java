package uk.ac.westminster.diabetesmanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class StatisticsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationview;
    Toolbar toolbar;
    TextView labelLow,labelNormal,labelHigh, labelExhigh;
    TextView textViewLow,textViewNormal,textViewHigh,textViewExHigh;
    int lowCount=0,normalCount=0,highCount=0,extraHighCount=0;
    DBHelper db;
    SQLiteDatabase sqLiteDatabase;
    String[] glucose;
    String[] recordDate;
    String[] recordTime;
    int[] id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        updateNavHeader();


        drawerLayout = findViewById(R.id.drawerlayout);
        navigationview = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        textViewLow = findViewById(R.id.txt_low_levels);
        textViewNormal = findViewById(R.id.txt_normal_levels);
        textViewHigh = findViewById(R.id.txt_high_levels);
        textViewExHigh = findViewById(R.id.txt_extra_high_levels);
        labelLow = findViewById(R.id.labellow);
        labelHigh = findViewById(R.id.labelhigh);
        labelNormal = findViewById(R.id.labelnormal);
        labelExhigh = findViewById(R.id.labelexhigh);

        //Statistics

        SharedPreferences preferencesUid = getSharedPreferences("useriddetails", MODE_PRIVATE);
        Integer userid = preferencesUid.getInt("userid", 0);
        String userID = Integer.toString(userid);
        db = new DBHelper(this);

        sqLiteDatabase = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM glucose WHERE patientId=?", new String[]{userID});

        if (cursor.getCount() > 0) {
            id = new int[cursor.getCount()];
            glucose = new String[cursor.getCount()];
            recordDate = new String[cursor.getCount()];
            recordTime = new String[cursor.getCount()];
            int i = cursor.getCount() - 1;
            while (cursor.moveToNext()) {
                id[i] = cursor.getInt(0);
                glucose[i] = cursor.getString(1);
                if(Integer.parseInt(glucose[i]) <= 80){
                    lowCount++;

                }else if((Integer.parseInt(glucose[i]) > 80) && (Integer.parseInt(glucose[i]) <= 115)){
                    normalCount++;

                }else if((Integer.parseInt(glucose[i]) > 115) && (Integer.parseInt(glucose[i]) < 180)){
                    highCount++;

                }else if((Integer.parseInt(glucose[i]) >= 180)) {
                    extraHighCount++;
                }

                recordDate[i] = cursor.getString(2);
                recordTime[i] = cursor.getString(3);
                i--;
            }
        }

        textViewLow.setText(Integer.toString(lowCount));
        labelLow.setBackgroundColor(Color.rgb(245, 199, 0));
        textViewLow.setBackgroundColor(Color.rgb(245, 199, 0));
        textViewNormal.setText(Integer.toString(normalCount));
        labelNormal.setBackgroundColor(Color.rgb(106, 150, 31));
        textViewNormal.setBackgroundColor(Color.rgb(106, 150, 31));
        textViewHigh.setText(Integer.toString(highCount));
        labelHigh.setBackgroundColor(Color.rgb(255, 102, 0));
        textViewHigh.setBackgroundColor(Color.rgb(255, 102, 0));
        textViewExHigh.setText(Integer.toString(extraHighCount));
        labelExhigh.setBackgroundColor(Color.rgb(193, 37, 82));
        textViewExHigh.setBackgroundColor(Color.rgb(193, 37, 82));

        //Side Navigator
        navigationview.bringToFront();
        navigationview.setNavigationItemSelectedListener((menuItem) -> {
            switch(menuItem.getItemId()){
                case R.id.logout_opt:
                    clickLogout();
                    break;
            }
            return false;
        });

        //Bottom Navigator
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.statistics); //this item is selected
        //so perform some operations
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                    case R.id.statistics:
                        return true;
                    case R.id.add:
                        startActivity(new Intent(getApplicationContext(), AddEventActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.graphs:
                        startActivity(new Intent(getApplicationContext(), GraphsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;


                }

                return false;
            }
        });

    }

    public void updateNavHeader(){

        SharedPreferences preferences = getSharedPreferences("useremaildetails",MODE_PRIVATE);
        String userEmail = preferences.getString("useremail","");

        SharedPreferences preferences1 = getSharedPreferences("usernamedetails",MODE_PRIVATE);
        String userName = preferences1.getString("username","");


        navigationview = findViewById(R.id.navigationview);
        View headerView = navigationview.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.textViewUsername);
        TextView navUserEmail = headerView.findViewById(R.id.textViewEmail);
        ImageView navUserPhoto = headerView.findViewById(R.id.imageViewUserPhoto);
        //updating textfields
        navUserName.setText(userName);
        navUserEmail.setText(userEmail);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    public void clickMenu(View view) {
        //Open Drawer
        HomeActivity.openDrawer(drawerLayout);

    }


    //logout
    public void clickLogout() {
        //logout
        logout(this);
    }

    private void logout(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        //positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish activity
                activity.finishAffinity();
                //exit app
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                //System.exit(0);
                Toast.makeText(StatisticsActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();

            }
        });
        //negative No
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss the alert
                dialog.dismiss();
            }
        });
        //show dialog
        builder.show();
    }

}

