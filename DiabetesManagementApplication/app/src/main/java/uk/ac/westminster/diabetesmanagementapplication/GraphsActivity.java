package uk.ac.westminster.diabetesmanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationview;
    Toolbar toolbar;
    String userName, userEmail;
    private static DBHelper db;
    private static SQLiteDatabase sqLiteDatabase;
    String[] glucose;
    String[] recordDate;
    String[] recordTime;
    int[] id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        updateNavHeader();

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationview = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        SharedPreferences preferencesUid = getSharedPreferences("useriddetails", MODE_PRIVATE);
        Integer userid = preferencesUid.getInt("userid", 0);
        String userID = Integer.toString(userid);

//        sqLiteDatabase = db.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM glucose WHERE patientId=?", new String[]{userID});
//
//        if (cursor.getCount() > 0) {
//            id = new int[cursor.getCount()];
//            glucose = new String[cursor.getCount()];
//            recordDate = new String[cursor.getCount()];
//            recordTime = new String[cursor.getCount()];
//            int i = cursor.getCount() - 1;
//            while (cursor.moveToNext()) {
//                id[i] = cursor.getInt(0);
//                glucose[i] = cursor.getString(1);
//                recordDate[i] = cursor.getString(2);
//                recordTime[i] = cursor.getString(3);
//                i--;
//            }


            //Graph
            String x, y;
            GraphView graph = (GraphView) findViewById(R.id.graph);
            //for (int j = 0; j < id.length; j++) {
                //data[i] = new PieChart.Data(status[i], values[i]);
                //x = glucose[j];
               // y = recordDate[j];
                //LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[0]);

                //series.resetData(grabData());

            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{

                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6),
                    new DataPoint(5, 4),
                    new DataPoint(6, 5),
                    new DataPoint(7, 6),
                    new DataPoint(8, 4)
            });

            graph.addSeries(series);


           // addData();
           // displayGraph();


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

            //Bottom Navigator
            bottomNavigationView = findViewById(R.id.bottom_navigator);
            bottomNavigationView.setSelectedItemId(R.id.graphs); //this item is selected
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
                            startActivity(new Intent(getApplicationContext(), AddEventActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.graphs:
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

    private DataPoint[] grabData() {


        SharedPreferences preferencesUid = getSharedPreferences("useriddetails", MODE_PRIVATE);
        Integer userid = preferencesUid.getInt("userid", 0);
        String userID = Integer.toString(userid);

//        sqLiteDatabase = db.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM glucose WHERE patientId=?", new String[]{userID});
//
//        if (cursor.getCount() > 0) {
//            id = new int[cursor.getCount()];
//            glucose = new String[cursor.getCount()];
//            recordDate = new String[cursor.getCount()];
//            recordTime = new String[cursor.getCount()];
//            int i = cursor.getCount() - 1;
//            while (cursor.moveToNext()) {
//                id[i] = cursor.getInt(0);
//                glucose[i] = cursor.getString(1);
//                recordDate[i] = cursor.getString(2);
//                recordTime[i] = cursor.getString(3);
//                i--;
//            }
            String[] column = {"xValue", "yValue"};
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM glucose WHERE patientId=?", new String[]{userID});
            DataPoint[] dataPoints = new DataPoint[cursor.getCount()];
            for(int i=0; i<cursor.getCount();i++){

                cursor.moveToNext();
                dataPoints[i] = new DataPoint(cursor.getDouble(1), Double.parseDouble(cursor.getString(2)));

            }
            return dataPoints;
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
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                //System.exit(0);
                Toast.makeText(GraphsActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();

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
