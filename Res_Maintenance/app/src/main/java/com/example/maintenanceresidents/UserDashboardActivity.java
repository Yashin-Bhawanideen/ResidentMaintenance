package com.example.maintenanceresidents;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class UserDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private EditText etInquiry, link;
    private Button btnSubmit, btnLogout;
    private ListView lvInquiries;
    private DatabaseManager dbManager;
    private int userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        etInquiry = findViewById(R.id.etInquiry);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);
        lvInquiries = findViewById(R.id.lvInquiries);
        link = findViewById(R.id.link);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        userId = getIntent().getIntExtra("USER_ID", -1);
        username = getIntent().getStringExtra("USERNAME");

        tvWelcome.setText("Welcome, " + username + "!");

        loadUserInquiries();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitInquiry();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void submitInquiry() {
        String description = etInquiry.getText().toString().trim();
        String Link = link.getText().toString().trim();

        if (description.isEmpty() || Link.isEmpty()) {
            Toast.makeText(this, "Please enter an inquiry description or image link", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbManager.addInquiry(userId, description, Link);

        if (result != -1) {
            Toast.makeText(this, "Inquiry submitted successfully!", Toast.LENGTH_SHORT).show();
            etInquiry.setText("");
            loadUserInquiries();
        } else {
            Toast.makeText(this, "Failed to submit inquiry", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadUserInquiries() {
        ArrayList<String> inquiriesList = new ArrayList<>();
        Cursor cursor = dbManager.getUserInquiries(userId);

        if (cursor.moveToFirst()) {
            do {
                int inquiryId = cursor.getInt(cursor.getColumnIndex("inquiry_id"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                String link = cursor.getString(cursor.getColumnIndex("linkImage"));
                String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                String status = cursor.getString(cursor.getColumnIndex("status"));

                String inquiryItem = "ID: " + inquiryId + "\n" +
                        description + "\n" +
                        "Link: " + link + "\n" +
                        "Date: " + timestamp + "\n" +
                        "Status: " + status;

                inquiriesList.add(inquiryItem);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, inquiriesList);
        lvInquiries.setAdapter(adapter);
    }

    private void logout() {
        Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

}
