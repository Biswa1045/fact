package com.biswa1045.fact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    List<SliderItems> sliderItems = new ArrayList<>();
    ArrayList<String> url = new ArrayList<>();
    ArrayList<String>uploader_img = new ArrayList<>();
    ArrayList<String> uploader_name = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //navigation
        BottomNavigationView bottomNavigationNiew = findViewById(R.id.buttom_navigation);
        bottomNavigationNiew.setItemIconTintList(null);
        bottomNavigationNiew.setSelectedItemId(R.id.home_nav);
        bottomNavigationNiew.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_nav:

                        return  true;
                    case R.id.cat_nav:
                        startActivity(new Intent(getApplicationContext(),catActivity.class));
                        overridePendingTransition(0,0);
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
        final VerticalViewPager verticalViewPager = (VerticalViewPager) findViewById(R.id.verticalViewPager);
        //database
        mRef = FirebaseDatabase.getInstance().getReference("Facts_image");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    url.add(ds.child("imageUri").getValue(String.class));
                    uploader_img.add(ds.child("uploader_img").getValue(String.class));
                    uploader_name.add(ds.child("uploader_name").getValue(String.class));
                    images.add(ds.child("imageUri").getValue(String.class));
                }
                for (int i = 0; i < images.size(); i++) {
                    sliderItems.add(new SliderItems(images.get(i)));
                }
                verticalViewPager.setAdapter(new ViewPagerAdapter(HomeActivity.this, sliderItems, url, verticalViewPager,uploader_img,uploader_name));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });





    }
}





