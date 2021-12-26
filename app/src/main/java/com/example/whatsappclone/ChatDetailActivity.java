package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.whatsappclone.Adoptors.ChatAdaptor;
import com.example.whatsappclone.Modals.MessagesModal;
import com.example.whatsappclone.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderID = auth.getUid();
        String recieverID = getIntent().getStringExtra("userId");
        String profilePicture = getIntent().getStringExtra("profilePicture");
        String username = getIntent().getStringExtra("username");

        binding.tvusername.setText(username);
        Picasso.get().load(profilePicture).placeholder(R.drawable.avatar).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessagesModal> list = new ArrayList<>();
        final ChatAdaptor chatAdaptor = new ChatAdaptor(list,this,recieverID);

        binding.chatRecyclerView.setAdapter(chatAdaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        final String senderRoom = senderID+recieverID;
        final String recieverRoom = recieverID+senderID;

        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            MessagesModal model = snapshot1.getValue(MessagesModal.class);
                            model.setMessageID(snapshot1.getKey());
                            list.add(model);
                        }
                        chatAdaptor.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = binding.etMessage.getText().toString();
                if(msg.isEmpty()){
                    binding.etMessage.setError("Please Type");
                    return;
                }
                final MessagesModal messagesModal = new MessagesModal(senderID,msg);
                messagesModal.setTimestamp(new Date().getTime());
                binding.etMessage.setText("");


                database.getReference().child("chats").child(senderRoom).
                        push().setValue(messagesModal).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats")
                                .child(recieverRoom)
                                .push()
                                .setValue(messagesModal).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                    }
                });

            }
        });


    }
}