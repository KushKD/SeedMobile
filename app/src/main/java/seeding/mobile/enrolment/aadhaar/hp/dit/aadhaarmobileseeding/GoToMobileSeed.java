package seeding.mobile.enrolment.aadhaar.hp.dit.aadhaarmobileseeding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.support.v4.app.FragmentActivity;

public class GoToMobileSeed extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_mobile_seed);

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new CustomPagerAdapter(this));


        RelativeLayout click = (RelativeLayout) findViewById(R.id.button_main);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GoToMobileSeed.this, MainActivity.class);
                startActivity(i);
            }
        });



    }

}
