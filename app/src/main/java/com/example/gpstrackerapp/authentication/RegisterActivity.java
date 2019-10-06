package com.example.gpstrackerapp.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gpstrackerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {

    EditText e3_email;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        e3_email = (EditText)findViewById(R.id.editText3);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

    }

    public void goToPasswordActivity(View v){

        dialog.setMessage("Checking your email address");
        dialog.show();

        auth.fetchSignInMethodsForEmail(e3_email.getText().toString()).
                addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            boolean check = !task.getResult().getSignInMethods().isEmpty();

                            if(!check){
                                //email does not exist, so we can create this email
                                Intent myIntent = new Intent(RegisterActivity.this, PasswordActivity.class);
                                myIntent.putExtra("email",e3_email.getText().toString());
                                startActivity(myIntent);
                                finish();
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"This email is already registered",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }



}
