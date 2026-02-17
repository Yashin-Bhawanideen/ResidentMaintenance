package com.example.maintenanceresidents;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnSignUp;
    private DatabaseManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter username & password", Toast.LENGTH_SHORT).show();
            return;
        }



            if(dbManager.checkUser(username,password)){
                if(username.equals("Admin1") && password.equals("Admin123")){
                    Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    //regular user
                    User user = dbManager.getUserByUsername(username);
                    Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                    intent.putExtra("USER_ID", user.getId());
                    intent.putExtra("USERNAME", user.getUsername());
                    startActivity(intent);
                    finish();
                }
            }else {
                Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
            }

       }
    

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbManager.close();
    }
}
