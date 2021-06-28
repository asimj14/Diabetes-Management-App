package uk.ac.westminster.diabetesmanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationview;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    String userName, userEmail;

    DBHelper db;
    SQLiteDatabase sqLiteDatabase;
    ListView listView;
    String[] glucose;
    String[] recordDate;
    String[] recordTime;
    int[] id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Toolbar
        //setSupportActionBar(toolbar);

        //ListView
        db = new DBHelper(HomeActivity.this);

        //create methods
        findId();
        displayAllData();


        drawerLayout = findViewById(R.id.drawerlayout);
        toolbar = findViewById(R.id.toolbar);

        //Side Navigator
        navigationview = findViewById(R.id.navigationview);
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
        bottomNavigationView.setSelectedItemId(R.id.dashboard); //this item is selected
        //so perform some operations
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.dashboard:
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

        updateNavHeader();
    }

    private void displayAllData() {

        SharedPreferences preferences = getSharedPreferences("useriddetails",MODE_PRIVATE);
        Integer userid = preferences.getInt("userid",0);
        String userID = Integer.toString(userid);

        sqLiteDatabase = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM glucose WHERE patientId=?", new String[]{userID});
        //Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM glucose", null);

        if (cursor.getCount() > 0) {
            id = new int[cursor.getCount()];
            glucose = new String[cursor.getCount()];
            recordDate = new String[cursor.getCount()];
            recordTime = new String[cursor.getCount()];
            int i = cursor.getCount()-1;
            while (cursor.moveToNext()) {
                id[i] = cursor.getInt(0);
                glucose[i]= cursor.getString(1);
                recordDate[i] = cursor.getString(2);
                recordTime[i] = cursor.getString(3);
                i--;
            }
            Custom adapter = new Custom();
            listView.setAdapter(adapter);

        }else{
            Toast.makeText(this, "No data found!!", Toast.LENGTH_SHORT).show();

        }
    }




    private class Custom extends BaseAdapter {


        @Override
        public int getCount() {
            return glucose.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            ImageView edit,delete;

            convertView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.singledata, parent, false);
            textView = convertView.findViewById(R.id.txt_name);

            edit = convertView.findViewById(R.id.edit_data);
            delete = convertView.findViewById(R.id.delete_data);
            textView.setText("BG: "+glucose[position]+"\n Date: "+recordDate[position]+"\n Time: "+recordTime[position]);
//            if(position % 2 == 0){
//                convertView.setBackgroundColor(Color.parseColor("#f2f2f2"));
//
//            }


            if((Integer.parseInt(glucose[position]) <= 80)){
                convertView.setBackgroundColor(Color.parseColor("#AEF5D3"));
                textView.setTextColor(Color.BLACK);

            }else if((Integer.parseInt(glucose[position]) > 80) && (Integer.parseInt(glucose[position]) <= 115)){
                convertView.setBackgroundColor(Color.parseColor("#21B14D"));
                textView.setTextColor(Color.WHITE);

            }else if((Integer.parseInt(glucose[position]) > 115) && (Integer.parseInt(glucose[position]) < 180)){
                convertView.setBackgroundColor(Color.parseColor("#F7AD44"));
                textView.setTextColor(Color.BLACK);

            }else if((Integer.parseInt(glucose[position]) >= 180)){

                convertView.setBackgroundColor(Color.parseColor("#AE1C1C"));
                textView.setTextColor(Color.WHITE);



            }


            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id",id[position]);
                    bundle.putString("glucoseValue",glucose[position]);
                    bundle.putString("recordDate",recordDate[position]);
                    bundle.putString("recordTime",recordTime[position]);


                    Intent intent = new Intent(HomeActivity.this,AddEventActivity.class);
                    intent.putExtra("userdata",bundle);
                    startActivity(intent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLiteDatabase = db.getReadableDatabase();
                    long recd = sqLiteDatabase.delete("glucose","id="+id[position],null);
                    if(recd!=-1){
                        Toast.makeText(HomeActivity.this, "Reading deleted successfully!", Toast.LENGTH_SHORT).show();
                        displayAllData();
                    }
                    else{
                        displayAllData();
                    }
                }
            });
            return convertView;
        }
    }


    private void findId() {

        listView = findViewById(R.id.listView);

    }

    public void clickMenu(View view){
        //Open Drawer
        openDrawer(drawerLayout);

    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //Open Drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
        //drawerLayout.setScrimColor(Color.BLUE);
    }

    public void clickLogo(View view){
        //Close Drawer
        closeDrawer(drawerLayout);

    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //Close Drawer Layout
       if(drawerLayout.isDrawerOpen(GravityCompat.START)){
           //when drawer is open then close it
           drawerLayout.closeDrawer(GravityCompat.START);

       }
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
        Toast.makeText(this, "User Email:"+userEmail+" User Name:"+userName, Toast.LENGTH_SHORT).show();

    }


    public void clickLogout(){
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

                SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);//so that only our app can read this preference
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();

                //finish activity
                activity.finishAffinity();
                //exit app
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                //System.exit(0);
                Toast.makeText(HomeActivity.this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
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

    public void onPause(){
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

    //handling onclicks
    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
        return true;
    }
}

