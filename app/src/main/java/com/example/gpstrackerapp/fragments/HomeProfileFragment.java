package com.example.gpstrackerapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpstrackerapp.CreateUser;
import com.example.gpstrackerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


public class HomeProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference userReference, reference;
    TextView name;
    TextView username;
    TextView email;
    TextView code;
    ImageView image;
    SwitchCompat switchCompat;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_profile, container, false);
        name = (TextView)rootView.findViewById(R.id.user_name);
        email = (TextView)rootView.findViewById(R.id.user_email);
        code = (TextView)rootView.findViewById(R.id.user_code);
        image = (ImageView)rootView.findViewById(R.id.user_image);
        switchCompat = (SwitchCompat)rootView.findViewById(R.id.sharingSwitch);
        username = (TextView)rootView.findViewById(R.id.username);


        userReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CreateUser createUser = dataSnapshot.getValue(CreateUser.class);
                name.setText(createUser.getName());
                email.setText(createUser.getEmail());
                code.setText(createUser.getCode());
                username.setText(createUser.getUsername());
                Picasso.get().load(createUser.getImageUri()).placeholder(R.drawable.defaultprofile).into(image);

                if(createUser.isSharing.equals("true")){
                    switchCompat.setChecked(true);
                }
//                else if(createUser.isSharing.equals("false")){
//                    switchCompat.isChecked();
//                }




                switchCompat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(switchCompat.isChecked()){
                            userReference.child("isSharing").setValue("true");
                        }
                        else{
                            userReference.child("isSharing").setValue("false");
                        }
                    }
                });





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });





        return rootView;
    }





}
