package com.example.gpstrackerapp.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpstrackerapp.CreateUser;
import com.example.gpstrackerapp.MainActivity;
import com.example.gpstrackerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InviteCodeActivity extends AppCompatActivity {

    String name,email,password,date,isSharing,code,username;
    Uri imageUri;
    TextView t1;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    StorageReference storageReference;
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        t1 = (TextView)findViewById(R.id.textView);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("User_images");

        Intent myIntent = getIntent();
        if(myIntent !=null){
            name = myIntent.getStringExtra("name");
            username = myIntent.getStringExtra("username");
            email = myIntent.getStringExtra("email");
            password = myIntent.getStringExtra("password");
            isSharing = myIntent.getStringExtra("isSharing");
            code = myIntent.getStringExtra("code");
            imageUri = myIntent.getParcelableExtra("imageUri");
        }

        t1.setText(code);

    }


    public void regUser(View v){

        progressDialog.setMessage("Please wait while we are creating an account for you");
        progressDialog.show();


      // System.out.println("This is the email "+email);
     //  System.out.println("This is the password "+password);

        auth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //insert values into database
                        user = auth.getCurrentUser();
                            CreateUser createUser = new CreateUser(name,username,email,password,code,"false","na","na","na",user.getUid());

                            user = auth.getCurrentUser();
                            userId = user.getUid();

                            reference.child(userId).setValue(createUser).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){


                                                StorageReference sr = storageReference.child(user.getUid()+" .jpg");
                                                sr.putFile(imageUri)
                                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                                if(task.isSuccessful()){

                                                                    String download_image_path = imageUri.toString();
                                                                    reference.child(user.getUid()).child("imageUri").setValue(download_image_path)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        progressDialog.dismiss();
                                                                                        sendVerificationEmail();
                                                                                        Intent myIntent = new Intent(InviteCodeActivity.this, MainActivity.class);
                                                                                        startActivity(myIntent);
                                                                                    }
                                                                                    else{
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getApplicationContext(),"An error occured while creating account",Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });





                                                                }
                                                            }
                                                        });

                                            }
                                            else{
                                                progressDialog.dismiss();

                                                Toast.makeText(getApplicationContext(),"Could not register user.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }
                });



    }

    private void sendVerificationEmail() {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Email sent for verification", Toast.LENGTH_SHORT).show();
                            finish();
                            auth.signOut();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Could not send Email",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
