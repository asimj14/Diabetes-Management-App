package uk.ac.westminster.diabetesmanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
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
    SQLiteDatabase sqLiteDatabse;
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
        sqLiteDatabse = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabse.rawQuery("SELECT * FROM glucose", null);
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
            textView.setText("BG: "+glucose[position]+" Date: "+recordDate[position]+" Time: "+recordTime[position]);

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
                    sqLiteDatabse = db.getReadableDatabase();
                    long recd = sqLiteDatabse.delete("glucose","id="+id[position],null);
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
        if(getIntent().getBundleExtra("userdetails")!=null){

            Bundle bundle = getIntent().getBundleExtra("userdetails");
            userEmail = bundle.getString("email");
        }

        navigationview = findViewById(R.id.navigationview);
        View headerView = navigationview.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.textViewUsername);
        TextView navUserEmail = headerView.findViewById(R.id.textViewEmail);
        ImageView navUserPhoto = headerView.findViewById(R.id.imageViewUserPhoto);
        //updating textfields
        //navUserName.setText(userName);
        navUserEmail.setText(userEmail);
        Toast.makeText(this, "User:"+userEmail, Toast.LENGTH_SHORT).show();

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

