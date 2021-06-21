package uk.ac.westminster.diabetesmanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationview;
    Toolbar toolbar;
    String userName, userEmail;
    EditText editTextGlucoseValue, editTextDate, editTextTime;

    private static DBHelper db;
    private static Button submitDetailsButton, editDetailsButton, displayDetailsButton;
    private static SQLiteDatabase sqLiteDatabase;
    private static int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //hooks
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationview = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        db = new DBHelper(AddEventActivity.this);

        editTextGlucoseValue = findViewById(R.id.editTextGlucoseValue);
        editTextTime = findViewById(R.id.editTextTime);
        //editTextTime.setOnClickListener( this );


//        if(getIntent().getBundleExtra("userdata")!=null) {
//
//            Bundle bundle = getIntent().getBundleExtra("userdata");
//            editTextGlucoseValue.setText(bundle.getString("glucoseValue"));
//
//        }

        //creating methods
        findId();
        getData();
        clear();
        editData();


        //Side Navigator
        navigationview.bringToFront();
        navigationview.setNavigationItemSelectedListener((menuItem) -> {
            switch (menuItem.getItemId()) {
                case R.id.logout_opt:
                    clickLogout();
                    break;
            }
            return false;
        });

        //Hooks for the main fields

        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextTime = (EditText) findViewById(R.id.editTextTime);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int mHour = calendar.get(Calendar.HOUR);
        final int mMinutes = calendar.get(Calendar.MINUTE);

        //Date
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        editTextDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        //Time

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        editTextTime.setText(hourOfDay + ":" + minute);

                    }
                }, mHour, mMinutes, false);
                timePickerDialog.show();
            }
        });


        //Bottom Navigator
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.add); //this item is selected
        //so perform some operations
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.statistics:
                        startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.add:
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

    private void editData() {

        //editText hooks
        editTextGlucoseValue = findViewById(R.id.editTextGlucoseValue);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);

        if (getIntent().getBundleExtra("userdata") != null) {

            Bundle bundle = getIntent().getBundleExtra("userdata");
            id = bundle.getInt("id");

            editTextGlucoseValue.setText(bundle.getString("glucoseValue"));
            Toast.makeText(this, "Value:" + bundle.getString("glucoseValue") + " selected to be edited", Toast.LENGTH_SHORT).show();
            editTextDate.setText(bundle.getString("recordDate"));
            editTextTime.setText(bundle.getString("recordTime"));
            editDetailsButton.setVisibility(View.VISIBLE);
            submitDetailsButton.setVisibility(View.GONE);

        }
    }

    private void clear() {
        editTextGlucoseValue.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
    }


    private void findId() {
        //editText hooks
        editTextGlucoseValue = findViewById(R.id.editTextGlucoseValue);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        //buttons hooks
        submitDetailsButton = findViewById(R.id.btnSubmit);
        editDetailsButton = findViewById(R.id.btnEdit);
        displayDetailsButton = findViewById(R.id.btnDisplay);

    }

    private void getData() {
        //Submit Data
        submitDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("glucoseValue", editTextGlucoseValue.getText().toString());
                contentValues.put("recordDate", editTextDate.getText().toString());
                contentValues.put("recordTime", editTextTime.getText().toString());

                sqLiteDatabase = db.getWritableDatabase();
                Long recid = sqLiteDatabase.insert("glucose", null, contentValues);
                if (recid != null) {
                    Toast.makeText(AddEventActivity.this, "New Glucose Reading added successfully!!", Toast.LENGTH_SHORT).show();
                    clear();
                } else {
                    Toast.makeText(AddEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();


                }

            }
        });

        //Display Data
        displayDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddEventActivity.this, HomeActivity.class));
            }
        });

        //Edit Data
        editDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("glucoseValue", editTextGlucoseValue.getText().toString());
                contentValues.put("recordDate", editTextDate.getText().toString());
                contentValues.put("recordTime", editTextTime.getText().toString());

                sqLiteDatabase = db.getWritableDatabase();
                Long recid = Long.valueOf(sqLiteDatabase.update("glucose", contentValues, "id=" + id, null));
                if (recid != -1) {
                    Toast.makeText(AddEventActivity.this, "Reading updated successfully!!", Toast.LENGTH_SHORT).show();
                    //After success eidt button will disappear and submit will appear
                    submitDetailsButton.setVisibility(View.VISIBLE);
                    editDetailsButton.setVisibility(View.GONE);
                    clear();
                } else {
                    Toast.makeText(AddEventActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }


    public void updateNavHeader() {
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        navigationview = findViewById(R.id.navigationview);
        View headerView = navigationview.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.textViewUsername);
        TextView navUserEmail = headerView.findViewById(R.id.textViewEmail);
        ImageView navUserPhoto = headerView.findViewById(R.id.imageViewUserPhoto);
        //updating textfields
        navUserName.setText(userName);
        navUserEmail.setText(userEmail);

    }

    public void clickMenu(View view) {
        //Open Drawer
        HomeActivity.openDrawer(drawerLayout);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
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
                System.exit(0);
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