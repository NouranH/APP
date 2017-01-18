package com.example.noran.mymovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



public class DetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        
       startFragment();


    }
    private void startFragment(){

        DetailsFragment detailsFragment=new DetailsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.activity_details_view,detailsFragment).commit();
    }
}