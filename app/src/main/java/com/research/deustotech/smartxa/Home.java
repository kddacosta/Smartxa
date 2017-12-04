package com.research.deustotech.smartxa;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;


//import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;


public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);





        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.circleMenu), "Click the menu to get started.")
                        // All options below are optional
                        .outerCircleColor(R.color.default_blue_light)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.progress_gray)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        //.icon(Drawable)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        //doSomething();
                    }
                });




        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {

                switch(menuButton.getHintText())
                {
                    case "Hand Control":
                        startActivity(new Intent(Home.this, FullHandControl.class));
                        break;
                    case "Finger Control":
                        startActivity(new Intent(Home.this, HandControl.class));
                        break;
                    case "My Smartxa Profile":
                        startActivity(new Intent(Home.this, UserProfile.class));
                        break;

                    case "Settings":

                        startActivity(new Intent(Home.this, Settings.class));
                        break;
                }
            }
        });

        circleMenu.setStateUpdateListener(new CircleMenu.OnStateUpdateListener() {
            @Override
            public void onMenuExpanded() {

            }

            @Override
            public void onMenuCollapsed() {

            }
        });

    }



    public void OnControlButtonClick(View v)
    {

        try
        {
            //  TODO: go to Handcontrol page
            startActivity(new Intent(Home.this, HandControl.class));
        }
        catch (Exception e)
        {
            // TODO: Do something with the exception
        }
    }



    public void OnOpenHandButtonClick(View v)
    {

        try
        {
            //  TODO: go to Handcontrol page
            startActivity(new Intent(Home.this, FullHandControl.class));
        }
        catch (Exception e)
        {
            // TODO: Do something with the exception
        }
    }
    public void ProfileButtonClick(View v)
    {

        try
        {
            //  TODO: go to Handcontrol page
            startActivity(new Intent(Home.this, UserProfile.class));
        }
        catch (Exception e)
        {
            // TODO: Do something with the exception
        }
    }

    public void SettingsButtonClick(View v)
    {

        try
        {
            //  TODO: go to Handcontrol page
            startActivity(new Intent(Home.this, Settings.class));
        }
        catch (Exception e)
        {
            // TODO: Do something with the exception
        }
    }
}
