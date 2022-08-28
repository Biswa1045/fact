package com.biswa1045.fact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class catActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat);
        //navigation
        BottomNavigationView bottomNavigationNiew = findViewById(R.id.buttom_navigation);
        bottomNavigationNiew.setItemIconTintList(null);
        bottomNavigationNiew.setSelectedItemId(R.id.cat_nav);
        bottomNavigationNiew.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_nav:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        return  true;
                    case R.id.cat_nav:

                        return true;
                    case R.id.user_nav:
                        startActivity(new Intent(getApplicationContext(),userActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        //end
        findViewById(R.id.geography_cat).setOnClickListener(new Clik());
        findViewById(R.id.history_cat).setOnClickListener(new Clik());
        findViewById(R.id.math_cat).setOnClickListener(new Clik());
        findViewById(R.id.economics_cat).setOnClickListener(new Clik());
        findViewById(R.id.entain_cat).setOnClickListener(new Clik());
        findViewById(R.id.india_cat).setOnClickListener(new Clik());
        findViewById(R.id.space_cat).setOnClickListener(new Clik());
        findViewById(R.id.animal_cat).setOnClickListener(new Clik());
        findViewById(R.id.science_cat).setOnClickListener(new Clik());
        findViewById(R.id.tech_cat).setOnClickListener(new Clik());
        findViewById(R.id.politics_cat).setOnClickListener(new Clik());
        findViewById(R.id.general_cat).setOnClickListener(new Clik());
        findViewById(R.id.cricket_cat).setOnClickListener(new Clik());
        findViewById(R.id.sports_cat).setOnClickListener(new Clik());
        findViewById(R.id.buss_cat).setOnClickListener(new Clik());
        findViewById(R.id.places_cat).setOnClickListener(new Clik());

    }
    public class Clik implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.general_cat:


            }
        }
    }


}