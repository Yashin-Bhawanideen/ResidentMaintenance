package com.example.maintenanceresidents;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Maintenance.db";
    private static final int DATABASE_VERSION = 3;

    // User table constants
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";

    // Inquiry table constants
    public static final String TABLE_INQUIRIES = "inquiries";
    public static final String COLUMN_INQUIRY_ID = "inquiry_id";
    public static final String COLUMN_USER_ID_FK = "user_id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_lINK = "linkImage";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_STATUS = "status";

    // Create users table SQL
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT NOT NULL)";

    // Create inquiries table SQL
    private static final String CREATE_INQUIRIES_TABLE =
            "CREATE TABLE " + TABLE_INQUIRIES + "(" +
                    COLUMN_INQUIRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID_FK + " INTEGER NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                    COLUMN_lINK + " TEXT NOT NULL, " +
                    COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_STATUS + " TEXT DEFAULT 'Pending', " +
                    "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE)";

    // Drop tables SQL
    private static final String DROP_USERS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_USERS;
    private static final String DROP_INQUIRIES_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_INQUIRIES;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_INQUIRIES_TABLE);

        // Insert default admin user
        insertDefaultAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL(DROP_INQUIRIES_TABLE);
        db.execSQL(DROP_USERS_TABLE);

        // Create new tables
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key constraints
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }

    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "Admin1");
        adminValues.put(COLUMN_PASSWORD, "Admin123");
        adminValues.put(COLUMN_EMAIL, "admin@system.com");

        db.insert(TABLE_USERS, null, adminValues);
    }

    // Helper method to get all table names
    public String[] getAllTableNames() {
        return new String[]{TABLE_USERS, TABLE_INQUIRIES};
    }

    // Helper method to get table schema
    public String getTableSchema(String tableName) {
        switch (tableName) {
            case TABLE_USERS:
                return CREATE_USERS_TABLE;
            case TABLE_INQUIRIES:
                return CREATE_INQUIRIES_TABLE;
            default:
                return "Table not found";
        }
    }
}
