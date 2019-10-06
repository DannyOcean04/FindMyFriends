package com.example.gpstrackerapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.gpstrackerapp.CircleJoin;
import com.example.gpstrackerapp.CreateUser;
import com.example.gpstrackerapp.R;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class JoinCircleFragment extends Fragment {
    Pinview pinview;
    DatabaseReference reference,currentReference,circleReference, userCircleReference;
    FirebaseUser user;
    FirebaseAuth auth;
    String current_user_id,join_user_id;
    Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        current_user_id = user.getUid();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_circle, container, false);

        pinview = (Pinview)rootView.findViewById(R.id.pinview);
        btn = (Button) rootView.findViewById(R.id.button8);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Query query = reference.orderByChild("code").equalTo(pinview.getValue());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            CreateUser createUser = null;
                            for(DataSnapshot childDss : dataSnapshot.getChildren()){
                                createUser = childDss.getValue(CreateUser.class);
                                join_user_id = createUser.userId;

                                circleReference = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(join_user_id).child("CircleMembers");
                                userCircleReference = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(current_user_id).child("CircleMembers");



                                CircleJoin circleJoin = new CircleJoin(current_user_id);
                                CircleJoin circleJoin1 = new CircleJoin(join_user_id);

                                circleReference.child(current_user_id).setValue(circleJoin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getActivity().getApplicationContext(), "User Joined Circle Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                userCircleReference.child(join_user_id).setValue(circleJoin1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getActivity().getApplicationContext(), "User Joined Circle Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }
                        }
                        else{
                            Toast.makeText(getActivity().getApplicationContext(),"Circle Code is Invalid",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        return rootView;
    }

//    public void submitButton(View v){
//        Query query = reference.orderByChild("code").equalTo(pinview.getValue());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    CreateUser createUser = null;
//                    for(DataSnapshot childDss : dataSnapshot.getChildren()){
//                        createUser = childDss.getValue(CreateUser.class);
//                        join_user_id = createUser.userId;
//
//
//                        circleReference = FirebaseDatabase.getInstance().getReference().child("Users")
//                                .child(join_user_id).child("CircleMembers");
//
//                        CircleJoin circleJoin = new CircleJoin(current_user_id);
//                        CircleJoin circleJoin1 = new CircleJoin(join_user_id);
//
//
//                        circleReference.child(user.getUid()).setValue(circleJoin)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            Toast.makeText(getContext(),"User joined circle successfully",Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                    }
//                }
//                else{
//                    Toast.makeText(getContext(),"Circle Code is Invalid",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
