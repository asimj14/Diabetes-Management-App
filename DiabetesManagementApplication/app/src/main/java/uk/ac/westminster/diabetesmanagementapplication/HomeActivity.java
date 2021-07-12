package uk.ac.westminster.diabetesmanagementapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationview;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

    DBHelper db;
    SQLiteDatabase sqLiteDatabase;
    ListView listView;
    String[] glucose;
    String[] recordDate;
    String[] recordTime;
    int[] id;
    int mYear, mMonth, mDay, mHour, mMinute;
    String todayDate = "";
    String todayTime = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        updateNavHeader();
        db = new DBHelper(HomeActivity.this);

        //create methods in order to show ListView with all Records
        findId();
        displayAllData();


        drawerLayout = findViewById(R.id.drawerlayout);
        toolbar = findViewById(R.id.toolbar);

        //Side Navigator
        navigationview = findViewById(R.id.navigationview);
        navigationview.bringToFront();
        navigationview.setNavigationItemSelectedListener((menuItem) -> {
            switch (menuItem.getItemId()) {
                case R.id.share_opt:
                    try {
                        createPdf();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
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

    }

    //Display all BG level records
    private void displayAllData() {

        SharedPreferences preferences = getSharedPreferences("useriddetails", MODE_PRIVATE);
        Integer userid = preferences.getInt("userid", 0);
        String userID = Integer.toString(userid);

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
                recordDate[i] = cursor.getString(2);
                recordTime[i] = cursor.getString(3);
                i--;
            }
            //Setting the adapter and the listView
            Custom adapter = new Custom();
            listView.setAdapter(adapter);

        } else {
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
            ImageView edit, delete;

            convertView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.singledata, parent, false);
            textView = convertView.findViewById(R.id.txt_name);

            edit = convertView.findViewById(R.id.edit_data);
            delete = convertView.findViewById(R.id.delete_data);
            textView.setText("BG: " + glucose[position] + " mg/dL" + "\n Date: " + recordDate[position] + "\n Time: " + recordTime[position]);

            if ((Integer.parseInt(glucose[position]) <= 80)) {
                convertView.setBackgroundColor(Color.rgb(245, 199, 0));
                textView.setTextColor(Color.WHITE);

            } else if ((Integer.parseInt(glucose[position]) > 80) && (Integer.parseInt(glucose[position]) <= 115)) {
                convertView.setBackgroundColor(Color.rgb(106, 150, 31));
                textView.setTextColor(Color.WHITE);

            } else if ((Integer.parseInt(glucose[position]) > 115) && (Integer.parseInt(glucose[position]) < 180)) {
                convertView.setBackgroundColor(Color.rgb(255, 102, 0));
                textView.setTextColor(Color.WHITE);

            } else if ((Integer.parseInt(glucose[position]) >= 180)) {
                convertView.setBackgroundColor(Color.rgb(193, 37, 82));

                textView.setTextColor(Color.WHITE);

            }

            //Edit Icon click event
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", id[position]);
                    bundle.putString("glucoseValue", glucose[position]);
                    bundle.putString("recordDate", recordDate[position]);
                    bundle.putString("recordTime", recordTime[position]);


                    Intent intent = new Intent(HomeActivity.this, AddEventActivity.class);
                    intent.putExtra("userdata", bundle);
                    startActivity(intent);
                }
            });
            //Delete Icon click event
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLiteDatabase = db.getReadableDatabase();
                    long recd = sqLiteDatabase.delete("glucose", "id=" + id[position], null);
                    if (recd != -1) {
                        Toast.makeText(HomeActivity.this, "Reading deleted successfully!", Toast.LENGTH_SHORT).show();
                        displayAllData();
                    } else {
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

    public void clickMenu(View view) {
        //Open Drawer
        openDrawer(drawerLayout);

    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //Open Drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
        //drawerLayout.setScrimColor(Color.BLUE);
    }

    public void clickLogo(View view) {
        //Close Drawer
        closeDrawer(drawerLayout);

    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //Close Drawer Layout
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //when drawer is open then close it
            drawerLayout.closeDrawer(GravityCompat.START);

        }
    }

    public void updateNavHeader() {

        SharedPreferences preferences = getSharedPreferences("useremaildetails", MODE_PRIVATE);
        String userEmail = preferences.getString("useremail", "");

        SharedPreferences preferences1 = getSharedPreferences("usernamedetails", MODE_PRIVATE);
        String userName = preferences1.getString("username", "");


        navigationview = findViewById(R.id.navigationview);
        View headerView = navigationview.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.textViewUsername);
        TextView navUserEmail = headerView.findViewById(R.id.textViewEmail);
        ImageView navUserPhoto = headerView.findViewById(R.id.imageViewUserPhoto);
        //updating textfields
        navUserName.setText(userName);
        navUserEmail.setText(userEmail);
        //Toast.makeText(this, "User Email:"+userEmail+" User Name:"+userName, Toast.LENGTH_SHORT).show();

    }


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

                //so that only our app can read this preference
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();

                //finish activity
                activity.finishAffinity();
                //exit app
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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

    public void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

    //handling onclicks
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    //Create & Save PDF

    public void createPdf() throws FileNotFoundException {

        SharedPreferences preferences = getSharedPreferences("useremaildetails", MODE_PRIVATE);
        String userEmail = preferences.getString("useremail", "");

        SharedPreferences preferences1 = getSharedPreferences("usernamedetails", MODE_PRIVATE);
        String userName = preferences1.getString("username", "");

        //Today Date
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        todayDate = (String.format("%04d-%02d-%02d", mYear, mMonth + 1, mDay));
        todayTime = (String.format(("%02d:%02d"), mHour, mMinute));

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "myBGReadings.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        document.setMargins(0, 0, 0, 0);

        //Image2
        Drawable d2 = getResources().getDrawable(R.drawable.logo_small);
        Bitmap bitmap2 = ((BitmapDrawable) d2).getBitmap();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        byte[] bitmapData2 = stream2.toByteArray();
        ImageData imageData2 = ImageDataFactory.create(bitmapData2);
        Image image2 = new Image(imageData2);
        image2.setWidth(150);
        image2.setHeight(100);
        image2.setMargins(20, 30, 20, 30);
        image2.setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(image2);

        float columnWidth[] = {120, 220, 120, 100};
        Table userTable = new Table(columnWidth);
        userTable.addCell(new Cell().add(new Paragraph("Patient Name").setFontSize(14).
                setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        userTable.addCell(new Cell().add(new Paragraph(userName).setFontSize(14)));
        userTable.addCell(new Cell().add(new Paragraph("Patient Email").setFontSize(14).
                setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        userTable.addCell(new Cell().add(new Paragraph(userEmail).setFontSize(14)));
        userTable.addCell(new Cell().add(new Paragraph("Date").setFontSize(14).
                setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        userTable.addCell(new Cell().add(new Paragraph(todayDate).setFontSize(14)));
        userTable.addCell(new Cell().add(new Paragraph("Time").setFontSize(14).
                setFontColor(ColorConstants.BLACK).setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        userTable.addCell(new Cell().add(new Paragraph(todayTime).setFontSize(14)));
        userTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
        userTable.setMarginTop(10);
        userTable.setMarginBottom(20);
        userTable.setMarginLeft(20);
        userTable.setMarginRight(20);
        document.add(userTable);

        String[] tableHeader = {"BG Level Reading", "Date", "Time"};
        float columnWidth1[] = {200f, 50f, 100f};
        Table table = new Table(columnWidth1);
        table.addCell(new Cell().add(new Paragraph(tableHeader[0]).setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        table.addCell(new Cell().add(new Paragraph(tableHeader[1]).setBackgroundColor(ColorConstants.LIGHT_GRAY)));
        table.addCell(new Cell().add(new Paragraph(tableHeader[2]).setBackgroundColor(ColorConstants.LIGHT_GRAY)));

        for (int i = 0; i < glucose.length; i++) {
            table.addCell(new Cell().add(new Paragraph(glucose[i])));
            table.addCell(new Cell().add(new Paragraph(recordDate[i])));
            table.addCell(new Cell().add(new Paragraph(recordTime[i])));
        }
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(table);

        document.close();
        Toast.makeText(this, "Pdf Created & Saved!!", Toast.LENGTH_SHORT).show();
        System.out.println("Pdf created");
    }
}
