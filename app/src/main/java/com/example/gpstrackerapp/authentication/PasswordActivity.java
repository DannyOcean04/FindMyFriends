package com.example.gpstrackerapp.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gpstrackerapp.R;

public class PasswordActivity extends AppCompatActivity {

    String email;
    EditText e4_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        e4_password = (EditText)findViewById(R.id.editText4);
        Intent myIntent = getIntent();
        if(myIntent !=null){
            email = myIntent.getStringExtra("email");
        }


    }

    public void goToNamePicActivity(View v){

        if(e4_password.getText().length()>6){

            Intent myIntent = new Intent(PasswordActivity.this, NameActivity.class);
            myIntent.putExtra("email",email);
            myIntent.putExtra("password",e4_password.getText().toString());
            startActivity(myIntent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Password should be more than 6 characters", Toast.LENGTH_SHORT).show();
        }
    }
}
