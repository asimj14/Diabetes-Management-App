package uk.ac.westminster.diabetesmanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

public class GraphsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationview;
    Toolbar toolbar;
    DBHelper db;
    SQLiteDatabase sqLiteDatabase;
    String[] glucose;
    String[] recordDate;
    String[] recordTime;
    int[] id;

    LineDataSet lineDataSet = new LineDataSet(null,null);
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    LineData lineData;
    LineChart lineChart;
    int lowCount=0,normalCount=0,highCount=0,extraHighCount=0;

    float rainfall[] = {98.8f,123.8f,161.6f,24.2f,52f,58.2f,35.4f,13.8f,78.4f,203.4f,240.2f,159.7f};
    String monthNames[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    //String LevelNames = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        updateNavHeader();

        
        //Utils.init(this);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationview = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        SharedPreferences preferencesUid = getSharedPreferences("useriddetails", MODE_PRIVATE);
        Integer userid = preferencesUid.getInt("userid", 0);
        String userID = Integer.toString(userid);
        db = new DBHelper(this);

        sqLiteDatabase = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM glucose WHERE patientId=?", new String[]{userID});
        //Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM glucose", null);

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

        //PieChart

        setupPieChart();


        //Graph
        String x, y;
        //lineChart  = findViewById(R.id.chart);

//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
//
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6),
//                new DataPoint(5, 4),
//                new DataPoint(6, 5),
//                new DataPoint(7, 6),
//                new DataPoint(8, 4)
//        });
//
//        graph.addSeries(series);

        //displayGraph();
        //lineDataSet.setLineWidth(4);


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

    private void setupPieChart() {

        //Populating a list of Pie entries
        List<PieEntry> pieEntries = new ArrayList<>();

        // string and integer data
        String status[] = {"Low", "Normal", "High", "Extra High"};

        int values[] = {lowCount, normalCount, highCount, extraHighCount};

        //For every slice an entry
        //Will pair together month & value in pie entry
        for (int i = 0; i < values.length; i++) {
            pieEntries.add(new PieEntry(values[i], status[i]));
            //pieEntries.add(new PieEntry(rainfall[i],monthNames[i]));

        }
        //Pie data set
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(Color.rgb(245, 199, 0), Color.rgb(106, 150, 31), Color.rgb(255, 102, 0),
                Color.rgb(193, 37, 82), Color.rgb(179, 100, 53));
        Legend legend;
        Description description = new Description();

        //red rgb(193, 37, 82)
        //orange rgb(255, 102, 0)
        //yellow rgb(245, 199, 0)
        //green rgb(106, 150, 31)

        //Pie data Obj
        PieData data = new PieData(dataSet);
        data.setValueTextSize(15);
        //Get the chart
        PieChart chart = (PieChart) findViewById(R.id.pieChart);
        chart.setData(data);
        legend = chart.getLegend();
        legend.setEnabled(true);
        //chart.setHoleRadius(20);
        //legend.setTextColor(Color.RED);
        legend.setTextSize(15);
        chart.setDescription(description);
        chart.animateY(1000);
        chart.setUsePercentValues(true);
        chart.invalidate();

    }

//     @Override
//     public static final int[] COLORFUL_COLORS = {
//            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
//            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)
//    };

    private void displayGraph(){

        lineDataSet.setValues(getDataValues());
        lineDataSet.setLabel("DataSet 1");
        dataSets.clear();
        dataSets.add(lineDataSet);
        lineData = new LineData(dataSets);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private ArrayList<Entry> getDataValues() {
        ArrayList<Entry> dataVals = new ArrayList<>();
        String[] columns = {"xValues","yValues"};

        //SharedPreferences preferences = getSharedPreferences("useriddetails", MODE_PRIVATE);
       // Integer userid = preferences.getInt("userid", 0);
       // String userID = Integer.toString(userid);

        sqLiteDatabase = db.getWritableDatabase();
        //Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM graph WHERE patientId=?", new String[]{userID});
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM graph",null);

        for(int i=0; i<cursor.getCount();i++){
            cursor.moveToNext();
            dataVals.add(new Entry(cursor.getFloat(0),cursor.getFloat(1)));
        }
        return dataVals;

    }


    private DataPoint[] grabData() {


        SharedPreferences preferencesUid = getSharedPreferences("useriddetails", MODE_PRIVATE);
        Integer userid = preferencesUid.getInt("userid", 0);
        String userID = Integer.toString(userid);

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
