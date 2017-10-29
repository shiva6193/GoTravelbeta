 package com.shivakumar.gotravelbeta;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


 public class QuoteActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mQuoteChange;

    private Button mSavebtn;


    //Firebase

     private DatabaseReference mQuoteDatabase;
     private FirebaseUser mCurrentUser;

     //progress dialogue

      private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        // firebase

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String curr_user = mCurrentUser.getUid();


        mQuoteDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(curr_user);


        mToolbar=(Toolbar)findViewById(R.id.quote_appbar);
         setSupportActionBar(mToolbar);
         getSupportActionBar().setTitle("Profile Quote");
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);


         String quote_value=getIntent().getStringExtra("quote_value");



         mQuoteChange =(EditText) findViewById(R.id.quote_change);
         mSavebtn=(Button)findViewById(R.id.quote_save );


         mQuoteChange.setText(quote_value);


         mSavebtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 //progress

                 mProgress=new ProgressDialog(QuoteActivity.this);
                  mProgress.setTitle("Saving Changes...");
                  mProgress.setMessage("Please wait while we save the changes..");
                  mProgress.show();



                 String quote=mQuoteChange.getText().toString();
                 mQuoteDatabase .child("Quote").setValue(quote).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {

                         if(task.isSuccessful())

                         {

                              mProgress.dismiss();

                              Toast.makeText(QuoteActivity.this, "Successfully Your Quote Has Been Changed..!!",Toast.LENGTH_LONG).show();

                         }
                         else

                         {


                             Toast.makeText(getApplicationContext(),"There was some error in creating saving changes",Toast.LENGTH_LONG).show();


                         }

                     }
                 });
             }
         });


    }
}
