package com.example.maintenanceresidents;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    private ListView lvAllInquiries;
    private Button btnLogout, btnRefresh, btnFilter;
    private Spinner spStatusFilter;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        lvAllInquiries = findViewById(R.id.lvAllInquiries);
        btnLogout = findViewById(R.id.btnLogout);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnFilter = findViewById(R.id.btnFilter);
        spStatusFilter = findViewById(R.id.spStatusFilter);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Setup status filter spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_array,
                android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatusFilter.setAdapter(statusAdapter);

        loadAllInquiries();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllInquiries();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterInquiriesByStatus();
            }
        });
    }

    private void loadAllInquiries() {
        ArrayList<String> inquiriesList = new ArrayList<>();
        Cursor cursor = dbManager.getAllInquiriesWithUsers();

        if (cursor.moveToFirst()) {
            do {
                String inquiryItem = formatInquiryFromCursor(cursor);
                inquiriesList.add(inquiryItem);
            } while (cursor.moveToNext());
        } else {
            inquiriesList.add("No inquiries found");
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, inquiriesList);
        lvAllInquiries.setAdapter(adapter);
    }

    private void filterInquiriesByStatus() {
        String selectedStatus = spStatusFilter.getSelectedItem().toString();
        ArrayList<String> inquiriesList = new ArrayList<>();

        Cursor cursor;
        if (selectedStatus.equals("All")) {
            cursor = dbManager.getAllInquiriesWithUsers();
        } else {
            cursor = dbManager.getInquiriesByStatus(selectedStatus);
        }

        if (cursor.moveToFirst()) {
            do {
                String inquiryItem = formatInquiryFromCursor(cursor);
                inquiriesList.add(inquiryItem);
            } while (cursor.moveToNext());
        } else {
            inquiriesList.add("No inquiries found with status: " + selectedStatus);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, inquiriesList);
        lvAllInquiries.setAdapter(adapter);
    }

    private String formatInquiryFromCursor(Cursor cursor) {
        int inquiryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INQUIRY_ID));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
        String link = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_lINK));
        String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));

        //the inquiry display on admin dashboard
        return "Inquiry ID: " + inquiryId + "\n" +
                "User: " + username + " (" + email + ")\n" +
                "Description: " + description + "\n" +
                "Link: " + link + "\n"+
                "Date: " + timestamp + "\n" +
                "Status: " + status;
    }

    private void logout() {
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbManager != null) {
            loadAllInquiries();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbManager != null) {
            dbManager.close();
        }
    }
}
