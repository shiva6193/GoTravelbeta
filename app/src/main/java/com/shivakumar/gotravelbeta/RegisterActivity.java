package com.shivakumar.gotravelbeta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mAccName;
    private  EditText mRegEmail;
    private EditText mRegPassword;
    private Toolbar mToolbar;

    private DatabaseReference  mDatabase;


    //progress dialog

    private ProgressDialog mRegProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //toolbar

       mToolbar=(Toolbar)findViewById(R.id.reg_toolbar);
       setSupportActionBar(mToolbar);
       getSupportActionBar().setTitle("Create Account ");
        getSupportActionBar().setDisplayHomeAsUpEnabled( true);

        mRegProgress = new ProgressDialog(this);

        //Firebase auth

        mAuth = FirebaseAuth.getInstance();
        mAccName = (EditText) findViewById(R.id.reg_acc_name);
        mRegEmail = (EditText) findViewById(R.id.reg_email);
        mRegPassword = (EditText) findViewById(R.id.reg_password);
        Button mCreate_Reg_btn = (Button) findViewById(R.id.reg_create_botton);



        mCreate_Reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String acc_name = mAccName.getText().toString();
                 String email_name = mRegEmail.getText().toString();
                 String password = mRegPassword.getText().toString();

                 if(!TextUtils.isEmpty(acc_name)||!TextUtils.isEmpty(email_name)||!TextUtils.isEmpty(password) )

                 {

                     mRegProgress.setTitle("Registering User.....");
                     mRegProgress.setMessage("Please wait while we are creating your account !! ");
                     mRegProgress.setCanceledOnTouchOutside(false);
                     mRegProgress .show();
                     register_user(acc_name,email_name,password);

                 }


            }
        });


    }

    private void register_user(final String acc_name, String email_name, String password) {
        mAuth.createUserWithEmailAndPassword(email_name, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Uid Retrieving from database and creating hashmaps


                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid=current_user.getUid();

                            mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, String>userMap = new HashMap<>();

                             userMap.put("Name",acc_name);
                             userMap.put("Quote","Life is art of Drawings without an Eraser");
                             userMap.put("Image","default");
                             userMap.put("Thumb Image", "default");


                             mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {

                                     if(task.isSuccessful())

                                     {

                                         mRegProgress.dismiss();

                                         // Sign in success, update UI with the signed-in user's information

                                         Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                         mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                         startActivity(mainIntent);
                                         finish();
                                         Toast.makeText(RegisterActivity.this, "Hola...Account Created...!!!",Toast.LENGTH_LONG).show();


                                     }
                                 }
                             });




                        } else {

                            mRegProgress.hide();

                            // If sign in fails, display a message to the user.

                            Toast.makeText(RegisterActivity.this, "Authentication failed...!! Plz Enter Correct Email and Password ",
                                    Toast.LENGTH_LONG).show();

                        }


                    }
                });
    }
}
