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
                //final EditText first = findViewById(R.id.profile_first);
                User user = new User("Lyle", "Hall", "8472549019","05/12/2000");
                String myUserId = super.mAuth.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(myUserId).child("first").setValue("asdf");
                //myRef.child("first").setValue(first.getText());
                //first.setHint(first.getText());

        }
    }
}