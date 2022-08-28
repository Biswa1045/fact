package com.biswa1045.fact;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.media.CamcorderProfile.get;

public class ViewPagerAdapter extends PagerAdapter {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    List<SliderItems> sliderItems;
    LayoutInflater mLayoutInflater;
    Context context;
    ArrayList<String> url;
    ArrayList<String> uploader_img ;
    ArrayList<String> uploader_name ;
    VerticalViewPager verticalViewPager;
    public ViewPagerAdapter(Context context, List<SliderItems> sliderItems, ArrayList<String> url, VerticalViewPager verticalViewPager,ArrayList<String> uploader_img,ArrayList<String> uploader_name) {
        this.context = context;
        this.sliderItems = sliderItems;
        this.url = url;
        this.uploader_img=uploader_img;
        this.uploader_name=uploader_name;
        this.verticalViewPager = verticalViewPager;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return sliderItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_container, container, false);
        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView profileimg = itemView.findViewById(R.id.profileImage2);
        TextView likes = itemView.findViewById(R.id.likes);
      //  TextView uploader = itemView.findViewById(R.id.uploader_text);
        ImageView like = itemView.findViewById(R.id.like_img);
        ImageView save = itemView.findViewById(R.id.save_img);
        ImageView share = itemView.findViewById(R.id.share_img);
        String url_us = url.get(position);
        String uploader_img_us = uploader_img.get(position);
        String uploader_name_us = uploader_name.get(position);
    //    uploader.setText(uploader_name.get(position));
        Glide.with(context).load(sliderItems.get(position).getImage())
                .centerCrop()
                .into(imageView);
        Glide.with(context).load(uploader_img.get(position))
                .centerCrop()
                .into(profileimg);
        String po = String.valueOf(position);
        islikes(po,like);
        nrlikes(likes,po);
        issave(po,save);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser.getUid()!=null) {
                    if (like.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference("Fact_likes").child(po).child(firebaseUser.getUid()).setValue(true);
                        userlike(po,url_us,uploader_img_us, uploader_name_us);
                    } else {
                        FirebaseDatabase.getInstance().getReference("Fact_likes").child(po).child(firebaseUser.getUid()).removeValue();
                        userdislike(po);
                    }
                }else{
                    //login to google popup
                    Toast.makeText(context, "log in to use this features", Toast.LENGTH_SHORT).show();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser.getUid()!=null) {
                    if (save.getTag().equals("save")) {
                        FirebaseDatabase.getInstance().getReference("Fact_saved").child(po).child(firebaseUser.getUid()).setValue(true);
                        usersave(po,url_us,uploader_img_us, uploader_name_us);
                    } else {
                        FirebaseDatabase.getInstance().getReference("Fact_saved").child(po).child(firebaseUser.getUid()).removeValue();
                        userunsave(po);
                    }
                }else{
                    //login to google popup
                    Toast.makeText(context, "log in to use this features", Toast.LENGTH_SHORT).show();
                }
            }
        });
       share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                String bitmappath = MediaStore.Images.Media.insertImage(context.getContentResolver() ,bitmap,"",null);
                Uri uri = Uri.parse(bitmappath);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.putExtra(Intent.EXTRA_TEXT,"Platstore Link : https://play.google.com/store/apps/details?id="+ context.getPackageName());
                context.startActivity(Intent.createChooser(intent,"share"));

            }
        });
       profileimg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               context.startActivity(new Intent(context.getApplicationContext(),profileActivity.class));
           }
       });


        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
    private void islikes(String factid,ImageView imageview){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Fact_likes").child(factid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageview.setImageResource(R.drawable.ic_baseline_favorite_24_liked);
                    imageview.setTag("liked");

                }else{
                    imageview.setImageResource(R.drawable.ic_baseline_favorite_24);
                    imageview.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void nrlikes(final TextView likes, String factid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Fact_likes").child(factid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void issave(String factid,ImageView imageview_save){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Fact_saved").child(factid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageview_save.setImageResource(R.drawable.ic_baseline_bookmark_24);
                    imageview_save.setTag("saved");

                }else{
                    imageview_save.setImageResource(R.drawable.ic_baseline_bookmark_24_notclick);
                    imageview_save.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void userlike(String factid_ul,String url_us,String uploader_img_us,String uploader_name_us){

        Map<String, Object> us = new HashMap<>();
        us.put("like_img",url_us);
        us.put("uploader_img",uploader_img_us);
        us.put("uploader_name",uploader_name_us);
        FirebaseFirestore.getInstance().collection("User")
                .document(firebaseUser.getUid())
                .collection("likes")
                .document(factid_ul)
                .set(us)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "check network connection", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void userdislike(String factid_ul){


        FirebaseFirestore.getInstance().collection("User")
                .document(firebaseUser.getUid())
                .collection("likes")
                .document(factid_ul)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "check network connection", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void usersave(String factid_ul,String url_us,String uploader_img_us,String uploader_name_us){
        Map<String, Object> us_save = new HashMap<>();
        us_save.put("like_img",url_us);
        us_save.put("uploader_img",uploader_img_us);
        us_save.put("uploader_name",uploader_name_us);

        FirebaseFirestore.getInstance().collection("User")
                .document(firebaseUser.getUid())
                .collection("save")
                .document(factid_ul)
                .set(us_save)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "check network connection", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void userunsave(String factid_ul){


        FirebaseFirestore.getInstance().collection("User")
                .document(firebaseUser.getUid())
                .collection("save")
                .document(factid_ul)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "check network connection", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void uploader_profile(){

    }
}