package com.biswa1045.fact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class userActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.like_recycle)
    RecyclerView rcv;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter,adapter2;
    LinearLayoutManager linearLayoutManager;
    TextView item1, item2, item3, select;
    TextView name,email;
FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(firebaseUser.getDisplayName().toString());
        email.setText(firebaseUser.getEmail().toString());


        findViewById(R.id.upload_fact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), uploadedActivity.class));
            }
        });
        findViewById(R.id.sett_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), settActivity.class));
            }
        });
        //navigation
        BottomNavigationView bottomNavigationNiew = findViewById(R.id.buttom_navigation);
        bottomNavigationNiew.setItemIconTintList(null);
        bottomNavigationNiew.setSelectedItemId(R.id.user_nav);
        bottomNavigationNiew.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_nav:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.cat_nav:
                        startActivity(new Intent(getApplicationContext(), catActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.user_nav:

                        return true;
                }
                return false;
            }
        });
        //end
        //user_tabbed
        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);
        select = findViewById(R.id.select);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        //recycle view
        ButterKnife.bind(this);
        init();
        String name2= "likes";
        getLikeList(name2);
    }

    private void init() {
      //  linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
       // rcv.setLayoutManager(linearLayoutManager);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        rcv.setLayoutManager(gridLayoutManager);
      //  uploader_lll.setVisibility(View.INVISIBLE);
        db = FirebaseFirestore.getInstance();
    }

    private void getLikeList(String name) {
        Query query = db.collection("User")
                .document(firebaseUser.getUid())
                .collection(name);

        FirestoreRecyclerOptions<retrieveModel> response = new FirestoreRecyclerOptions.Builder<retrieveModel>()
                .setQuery(query, retrieveModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<retrieveModel, FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(FriendsHolder holder, int position, retrieveModel model) {
                progressBar.setVisibility(View.GONE);
           //     holder.uploader_name.setText(model.getUploader_name());
              //   Glide.with(getApplicationContext())
              //           .load(model.getUploader_img())
              //           .into(holder.uploader_img);
                Glide.with(getApplicationContext())
                        .load(model.getLike_img())
                        .into(holder.like_img_user);

            }

            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_user, group, false);

                return new FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        rcv.setAdapter(adapter);


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.item1) {
            String name = "likes";
            select.animate().x(0).setDuration(100);
            item1.setTextColor(R.color.front2);
            item2.setTextColor(Color.WHITE);
            item3.setTextColor(Color.WHITE);
            getLikeList(name);
        } else if (view.getId() == R.id.item2) {
            String name  =  "save";
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(R.color.front2);
            item3.setTextColor(Color.WHITE);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
            getLikeList(name);
        } else if (view.getId() == R.id.item3) {
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(Color.WHITE);
            item3.setTextColor(R.color.front2);
            int size = item2.getWidth() * 2;
            select.animate().x(size).setDuration(100);

        }
    }

    public class FriendsHolder extends RecyclerView.ViewHolder {
    //    @BindView(R.id.uploader_name)
      //  TextView uploader_name;
    //    @BindView(R.id.uploader_img)
      //  ImageView uploader_img;
        @BindView(R.id.like_img_user)
        ImageView like_img_user;

        public FriendsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }




}