package com.example.szparak.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

public class MainActivity extends AppCompatActivity implements ManageToolbar {

    Toolbar myToolbar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        MainFragment.manageToolbar = this;
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.ic_film_2);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment(this))
                    .commit();
        }

    }

    public void setActionBarTitle(String title){
        myToolbar.setTitle(title);
    }



    @Override
    public void onHide() {
        final ActionBar ab = getSupportActionBar();
        if (ab != null && ab.isShowing()) {
            if(myToolbar != null) {
                myToolbar.animate().translationY(-150).translationX(50).setDuration(100L)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                ab.hide();
                            }
                        }).start();
            } else {
                ab.hide();
            }
        }
    }

    @Override
    public void onShow() {
        ActionBar ab = getSupportActionBar();
        if (ab != null && !ab.isShowing()) {
            ab.show();
            if(myToolbar != null) {
                myToolbar.animate().translationY(0).translationX(0).setDuration(300L).alpha(1).start();
            }
        }
    }
}
