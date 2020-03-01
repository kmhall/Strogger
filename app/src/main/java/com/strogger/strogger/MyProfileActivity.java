package com.strogger.strogger;

        import android.content.Context;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.TextView;

public class MyProfileActivity extends AccountActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.my_profile);
        final TextView mTitle =  findViewById(R.id.toolbar_title);
        mTitle.setText("My Profile");

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.my_profile, null, false);
        super.dl.addView(contentView, 0);
    }
}
