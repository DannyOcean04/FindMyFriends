package com.example.gpstrackerapp.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gpstrackerapp.CircleJoin;
import com.example.gpstrackerapp.CreateUser;
import com.example.gpstrackerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class NameActivity extends AppCompatActivity {

    String email,password;
    EditText e5_name;
    EditText user_name;
    CircleImageView circleImageView;
    ProgressDialog dialog;
    DatabaseReference reference;
    Uri resultUri;
    boolean validUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        e5_name = (EditText)findViewById(R.id.editText5);
        user_name = (EditText)findViewById(R.id.editUserName);
        circleImageView = (CircleImageView)findViewById(R.id.circleImageView);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        dialog = new ProgressDialog(this);
        validUsername = false;


        Intent myIntent = getIntent();
        if(myIntent !=null){
            email = myIntent.getStringExtra("email");
            password = myIntent.getStringExtra("password");
        }

    }

    public void generateCode(View v){

        checkUserName();

        Date myDate = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        String date = format1.format(myDate);
        Random r = new Random();

        int n = 100000 + r.nextInt(900000);
        String code = String.valueOf(n);

        if(resultUri !=null){

            if(validUsername == true) {
                Intent myIntent = new Intent(NameActivity.this, InviteCodeActivity.class);
                myIntent.putExtra("name", e5_name.getText().toString());
                myIntent.putExtra("username", user_name.getText().toString());
                myIntent.putExtra("email", email);
                myIntent.putExtra("password", password);
                myIntent.putExtra("date", date);
                myIntent.putExtra("isSharing", "false");
                myIntent.putExtra("code", code);
                myIntent.putExtra("imageUri", resultUri);

                startActivity(myIntent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Please choose a suitable username",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please choose an image",Toast.LENGTH_SHORT).show();
        }


    }

    public void checkUserName(){

        dialog.setMessage("Checking your username");
        dialog.show();

        Query query = reference.orderByChild("username").equalTo(user_name.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dialog.dismiss();
                    validUsername = false;
                    Toast.makeText(getApplicationContext(),"This username is already taken",Toast.LENGTH_SHORT).show();

                }
                else{
                    dialog.dismiss();
                    validUsername = true;
                    Toast.makeText(getApplicationContext(),"This username is available",Toast.LENGTH_SHORT).show();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





    public void selectImage(View v){

        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i,12);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode ==12 && resultCode == RESULT_OK && data!=null){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }


        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                resultUri = result.getUri();
                circleImageView.setImageURI(resultUri);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }

        }



    }
}
