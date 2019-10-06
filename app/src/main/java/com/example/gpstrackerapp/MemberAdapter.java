package com.example.gpstrackerapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder>{

    ArrayList<CreateUser> nameList;
    StorageReference storageReference;
    Context c;

    public MemberAdapter(ArrayList<CreateUser> nameList, Context c) {
        this.nameList = nameList;
        this.c = c;
        storageReference = FirebaseStorage.getInstance().getReference().child("User_images");

    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        MemberViewHolder memberViewHolder = new MemberViewHolder(v,c,nameList);
        return memberViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        CreateUser currentUserObj = nameList.get(position);
        holder.name_txt.setText(currentUserObj.name+" :"+currentUserObj.username);
        Picasso.get().load(currentUserObj.imageUri).placeholder(R.drawable.defaultprofile).into(holder.circleImageView);

        if(currentUserObj.isSharing.equals("false")){
            holder.sIcon.setImageResource(R.drawable.red_offline);
        }
        else if(currentUserObj.isSharing.equals("true")){
            holder.sIcon.setImageResource(R.drawable.green_online);
        }


    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


    public static class MemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name_txt;
        CircleImageView circleImageView;
        ImageView sIcon;
        View v;
        Context c;
        ArrayList<CreateUser> nameArrayList;
        FirebaseAuth auth;
        FirebaseUser user;


        public MemberViewHolder(@NonNull View itemView, Context c, ArrayList<CreateUser> nameArrayList) {
            super(itemView);
            this.c = c;
            this.nameArrayList = nameArrayList;

            itemView.setOnClickListener(this);
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();




            name_txt = itemView.findViewById(R.id.item_title);
            circleImageView = itemView.findViewById(R.id.profilepic);
            sIcon = itemView.findViewById(R.id.sharingIcon);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(c,"You have clicked this user",Toast.LENGTH_LONG).show();
        }
    }
}
