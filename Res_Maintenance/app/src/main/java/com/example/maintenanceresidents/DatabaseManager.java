package com.example.maintenanceresidents;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;

    public DatabaseManager(Context context) {
        this.context = context;
    }

    public void open() {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // User operations
    public long addUser(String username, String password, String email) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);

        return database.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    public boolean checkUser(String username, String password) {
        String[] columns = {DatabaseHelper.COLUMN_USER_ID};
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    public boolean checkUsernameExists(String username) {
        String[] columns = {DatabaseHelper.COLUMN_USER_ID};
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    public User getUserByUsername(String username) {
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_EMAIL
        };
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
        }
        cursor.close();

        return user;
    }

    public int getUserIdByUsername(String username) {
        String[] columns = {DatabaseHelper.COLUMN_USER_ID};
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null, null, null);

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
        }
        cursor.close();

        return userId;
    }

    public Cursor getAllUsers() {
        String[] columns = {
                DatabaseHelper.COLUMN_USER_ID,
                DatabaseHelper.COLUMN_USERNAME,
                DatabaseHelper.COLUMN_EMAIL
        };

        return database.query(DatabaseHelper.TABLE_USERS,
                columns,
                null, null, null, null,
                DatabaseHelper.COLUMN_USERNAME + " ASC");
    }

    public boolean deleteUser(int userId) {
        String whereClause = DatabaseHelper.COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        int rowsAffected = database.delete(DatabaseHelper.TABLE_USERS, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    // Inquiry operations
    public long addInquiry(int userId, String description, String link) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID_FK, userId);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_lINK, link);

        return database.insert(DatabaseHelper.TABLE_INQUIRIES, null, values);
    }

    public Cursor getAllInquiriesWithUsers() {
        String query = "SELECT " +
                "i." + DatabaseHelper.COLUMN_INQUIRY_ID + ", " +
                "i." + DatabaseHelper.COLUMN_DESCRIPTION + ", " +
                "i." + DatabaseHelper.COLUMN_lINK+ ", " +
                "i." + DatabaseHelper.COLUMN_TIMESTAMP + ", " +
                "i." + DatabaseHelper.COLUMN_STATUS + ", " +
                "u." + DatabaseHelper.COLUMN_USERNAME + ", " +
                "u." + DatabaseHelper.COLUMN_EMAIL + ", " +
                "u." + DatabaseHelper.COLUMN_USER_ID + " " +
                "FROM " + DatabaseHelper.TABLE_INQUIRIES + " i " +
                "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u " +
                "ON i." + DatabaseHelper.COLUMN_USER_ID_FK + " = u." + DatabaseHelper.COLUMN_USER_ID + " " +
                "WHERE u." + DatabaseHelper.COLUMN_USERNAME + " != 'Admin1' " +
                "ORDER BY i." + DatabaseHelper.COLUMN_TIMESTAMP + " DESC";

        return database.rawQuery(query, null);
    }

    public Cursor getUserInquiries(int userId) {
        String[] columns = {
                DatabaseHelper.COLUMN_INQUIRY_ID,
                DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_lINK,
                DatabaseHelper.COLUMN_TIMESTAMP,
                DatabaseHelper.COLUMN_STATUS
        };

        String selection = DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = DatabaseHelper.COLUMN_TIMESTAMP + " DESC";

        return database.query(DatabaseHelper.TABLE_INQUIRIES,
                columns,
                selection,
                selectionArgs,
                null, null, orderBy);
    }

    public Cursor getInquiryById(int inquiryId) {
        String[] columns = {
                DatabaseHelper.COLUMN_INQUIRY_ID,
                DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_lINK,
                DatabaseHelper.COLUMN_TIMESTAMP,
                DatabaseHelper.COLUMN_STATUS,
                DatabaseHelper.COLUMN_USER_ID_FK
        };

        String selection = DatabaseHelper.COLUMN_INQUIRY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(inquiryId)};

        return database.query(DatabaseHelper.TABLE_INQUIRIES,
                columns,
                selection,
                selectionArgs,
                null, null, null);
    }

    public boolean updateInquiryStatus(int inquiryId, String status) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_STATUS, status);

        String whereClause = DatabaseHelper.COLUMN_INQUIRY_ID + " = ?";
        String[] whereArgs = {String.valueOf(inquiryId)};

        int rowsAffected = database.update(DatabaseHelper.TABLE_INQUIRIES, values, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    public boolean updateInquiry(int inquiryId, String description, String link, String status) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_lINK, link);
        values.put(DatabaseHelper.COLUMN_STATUS, status);

        String whereClause = DatabaseHelper.COLUMN_INQUIRY_ID + " = ?";
        String[] whereArgs = {String.valueOf(inquiryId)};

        int rowsAffected = database.update(DatabaseHelper.TABLE_INQUIRIES, values, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    public boolean deleteInquiry(int inquiryId) {
        String whereClause = DatabaseHelper.COLUMN_INQUIRY_ID + " = ?";
        String[] whereArgs = {String.valueOf(inquiryId)};

        int rowsAffected = database.delete(DatabaseHelper.TABLE_INQUIRIES, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    public int getInquiryCount() {
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_INQUIRIES;
        Cursor cursor = database.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

    public int getUserInquiryCount(int userId) {
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_INQUIRIES +
                " WHERE " + DatabaseHelper.COLUMN_USER_ID_FK + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count;
    }

    public Cursor getInquiriesByStatus(String status) {
        String query = "SELECT " +
                "i.*, u." + DatabaseHelper.COLUMN_USERNAME + ", u." + DatabaseHelper.COLUMN_EMAIL + " " +
                "FROM " + DatabaseHelper.TABLE_INQUIRIES + " i " +
                "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u " +
                "ON i." + DatabaseHelper.COLUMN_USER_ID_FK + " = u." + DatabaseHelper.COLUMN_USER_ID + " " +
                "WHERE i." + DatabaseHelper.COLUMN_STATUS + " = ? " +
                "ORDER BY i." + DatabaseHelper.COLUMN_TIMESTAMP + " DESC";

        return database.rawQuery(query, new String[]{status});
    }

    // Database utility methods
    public void clearAllData() {
        database.delete(DatabaseHelper.TABLE_INQUIRIES, null, null);
        database.delete(DatabaseHelper.TABLE_USERS, null, null);

        // Re-insert default admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(DatabaseHelper.COLUMN_USERNAME, "Admin1");
        adminValues.put(DatabaseHelper.COLUMN_PASSWORD, "Admin11");
        adminValues.put(DatabaseHelper.COLUMN_EMAIL, "admin@system.com");
        database.insert(DatabaseHelper.TABLE_USERS, null, adminValues);
    }

    public void recreateDatabase() {
        dbHelper.onUpgrade(database, 1, 1);
    }
}
