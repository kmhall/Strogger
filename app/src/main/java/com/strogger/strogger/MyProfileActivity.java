package com.strogger.strogger;

        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.strogger.strogger.firebase.User;

public class MyProfileActivity extends AccountActivity implements View.OnClickListener{

    private Button updateProfileButton;
    private DatabaseReference mDatabase;

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

        updateProfileButton = findViewById(R.id.update_profile);
        updateProfileButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_profile:
                //TODO: Clean data before placing
                final EditText first = findViewById(R.id.profile_first);
                final EditText last = findViewById(R.id.profile_last);
                final EditText dob = findViewById(R.id.profile_dob);
                final EditText phone = findViewById(R.id.profile_phone);

                String myUserId = super.mAuth.getUid();
                User user = new User(first.getText().toString(), last.getText().toString(), phone.getText().toString(),dob.getText().toString());

                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(myUserId).setValue(user);

                startActivity(new Intent(MyProfileActivity.this, MyProfileActivity.class));
        }
    }
}