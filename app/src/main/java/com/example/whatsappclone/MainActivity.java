package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whatsappclone.Adoptors.FragmentAdoptor;
import com.example.whatsappclone.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings:

                Intent intent1 = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent1);
                break;

            case R.id.logout:
                auth.signOut();
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();

        binding.viewPager.setAdapter(new FragmentAdoptor(getSupportFragmentManager()));
        binding.tablayout.setupWithViewPager(binding.viewPager);


    }
}