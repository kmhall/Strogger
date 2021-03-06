package com.strogger.strogger;

        import android.content.Context;
        import android.content.ContextWrapper;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.strogger.strogger.firebase.User;
        import static com.strogger.strogger.GlobalVariables.profileImgUri;
        import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AccountActivity implements View.OnClickListener{

    private Button updateProfileButton;
    private DatabaseReference mDatabase;
    private  String myUserId;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.my_profile);
        final TextView mTitle = findViewById(R.id.toolbar_title);
        mTitle.setText("My Profile");

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.my_profile, null, false);
        super.dl.addView(contentView, 0);

        circleImageView = findViewById(R.id.profile);
        if(profileImgUri != Uri.EMPTY){
            circleImageView.setImageURI(profileImgUri);
        }
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


        updateProfileButton = findViewById(R.id.update_profile);
        updateProfileButton.setOnClickListener(this);

        //Firebase retrieve data
        final EditText first = findViewById(R.id.profile_first);
        final EditText last = findViewById(R.id.profile_last);
        final EditText dob = findViewById(R.id.profile_dob);
        final EditText phone = findViewById(R.id.profile_phone);

        myUserId = super.mAuth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(myUserId);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                first.setText(user.first);
                last.setText(user.last);
                phone.setText(user.phone);
                dob.setText(user.dob);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    // Firebase write data
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_profile:
                final EditText first = findViewById(R.id.profile_first);
                final EditText last = findViewById(R.id.profile_last);
                final EditText dob = findViewById(R.id.profile_dob);
                final EditText phone = findViewById(R.id.profile_phone);

                User user = new User(first.getText().toString(), last.getText().toString(), phone.getText().toString(),dob.getText().toString());
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(myUserId).setValue(user);

                startActivity(new Intent(MyProfileActivity.this, MyProfileActivity.class));
        }
    }

    private static final int PICK_IMAGE = 100;
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            profileImgUri = data.getData();
            circleImageView.setImageURI(profileImgUri);
        }
    }

}