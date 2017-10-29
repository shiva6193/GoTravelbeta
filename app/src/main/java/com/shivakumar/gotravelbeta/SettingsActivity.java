package com.shivakumar.gotravelbeta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;


    //Android Layout

      private CircleImageView mDisplayName;
      private TextView mName;
      private TextView mQuote;
      private Button mQuotebtn;
      private Button mSettingImage;

      private ImageView mProfileImage;

      private  static final int Gallery_pick=1;

      //Storage Firebase

    private StorageReference mImageStorage ;

    //progress

    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mDisplayName = (CircleImageView)findViewById(R.id.profile_image);

        mName = (TextView) findViewById(R.id.settings_displayname);

        mQuote = (TextView) findViewById(R.id.quote);

        mProfileImage = (ImageView)findViewById(R.id.profile_image);


        mQuotebtn=(Button) findViewById(R.id.quote_btn);
        mSettingImage=(Button)findViewById(R.id.settings_imagebtn);

        //storage firebase

        mImageStorage = FirebaseStorage.getInstance().getReference();


         mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();

         String current_uid=mCurrentUser.getUid();

         mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

         mUserDatabase.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name" ).getValue().toString();
                String image = dataSnapshot.child("Image").getValue().toString();
                String quote = dataSnapshot.child("Quote").getValue().toString();
                String thumb_image = dataSnapshot.child("Thumb Image").getValue().toString();


                mName.setText(name);
                mQuote.setText(quote);

                if(!image.equals("default")) {


                    Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.dp).into(mProfileImage);
                }

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });

         mQuotebtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 String quote_value = mQuote.getText().toString();

                 Intent quoteIntent = new Intent (SettingsActivity.this, QuoteActivity.class);
                 quoteIntent.putExtra("quote_value",quote_value);
                 startActivity(quoteIntent);

             }
         });

         mSettingImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 Intent galleryIntent=new Intent();
                 galleryIntent.setType("image/*");
                 galleryIntent.setAction(Intent.ACTION_GET_CONTENT);


                 startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),Gallery_pick);








             }
         });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==Gallery_pick && resultCode==RESULT_OK)

        {

             Uri imageuri = data.getData();

            CropImage.activity(imageuri)
                    .setAspectRatio(1 ,1)
                    .setMinCropWindowSize(500,500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)

        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)

            {

                mProgressDialog = new ProgressDialog(SettingsActivity.this);

                mProgressDialog.setTitle("Uploaing Image....");

                mProgressDialog.setMessage("Please Wait while we upload and process the image. ");

                mProgressDialog.setCanceledOnTouchOutside(false);

                mProgressDialog.show();



                Uri resultUri = result.getUri();


                String current_user_id = mCurrentUser.getUid();


                //Storage Filepath

                StorageReference filepath = mImageStorage.child("Profile_Images").child(current_user_id  + ".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())

                        {

                           String download_url = task.getResult().getDownloadUrl().toString();

                               mUserDatabase.child("Image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {



                                    if(task.isSuccessful())

                                    {


                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Success !! Uploading Image",Toast.LENGTH_LONG).show();





                                    }

                                }
                            });


                        }

                        else

                        {

                            Toast.makeText(SettingsActivity.this, "Error !!!! in Uploading Image",Toast.LENGTH_LONG).show();

                            mProgressDialog.dismiss();


                        }

                    }
                });

            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)

            {

                Exception error = result.getError();

            }
        }
    }




}
