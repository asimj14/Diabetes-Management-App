package uk.ac.westminster.diabetesmanagementapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "diabetesMS.db", null, 1);
    }

    String AES = "AES";
    String encrytedPass, decryptedPass;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users(userid Integer primary key autoincrement,username Text, email Text, password Text, dateBirth String, gender Text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS glucose(id Integer primary key autoincrement, glucoseValue Double, recordDate Text, recordTime Text, patientId Integer, foreign key(patientId) REFERENCES users(userid))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users ");
        db.execSQL("DROP TABLE IF EXISTS glucose ");

    }

    //Insert user in DB
    public Boolean insertUser(String username, String email, String password, String dateBirth,
                              String gender) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        encrytedPass = encrypt(password, password);
        contentValues.put("password", encrytedPass);
        contentValues.put("dateBirth", dateBirth);
        contentValues.put("gender", gender);

        //Storing value
        long result = db.insert("users", null, contentValues);
        //If data inserted successfully then it will return another value otherwise return -1
        if (result == -1) { //registration failed
            return false;
        } else { //registration successful
            return true;
        }
    }

    //Verify username
    public Boolean checkUserEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Verify user credentials (username & password)
    public Boolean checkUserCredentials(String email, String password) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        encrytedPass = encrypt(password, password);
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, encrytedPass});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Update Password
    public Boolean updatePassword(String email, String password) throws Exception {
        encrytedPass = encrypt(password, password);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", encrytedPass);
        long result = db.update("users", contentValues, "email=?", new String[]{email});
        if (result == -1) { //update failed
            return false;
        } else { //update successfull
            return true;
        }
    }

    public SecretKeySpec generateKey(String password) throws Exception {
        //Using MessageDigest class to get instance of SHA-256 Algorithm to generate Key
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        //Converting Password into Byte Array with UTF-8 encoding
        byte[] bytes = password.getBytes("UTF-8");
        //Updating digest using specified array of bytes starting with the specified offset
        digest.update(bytes, 0, bytes.length);

        byte[] key = digest.digest();
        //Completing hash computation by performing final operations such as padding
        //building secret key from the given byte array
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        //Returning secret key
        return secretKeySpec;
    }

    public String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        //Creating Cipher instance from Cipher class with Advance Encryption Standard Algorithm
        Cipher c = Cipher.getInstance(AES);
        //Initializing the Cipher with
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());

        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;

    }

    public String decrypt(String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        //decodes encrpyoted array and return as byte array
        byte[] decodedValue = Base64.decode(password, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

}

