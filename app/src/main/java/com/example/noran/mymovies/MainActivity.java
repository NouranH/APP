package com.example.noran.mymovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;




public class MainActivity extends AppCompatActivity implements NameListener{
    public boolean mIsTwoPane=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("MyMovies");

        if (getString(R.string.isTablet).equalsIgnoreCase("true"))
            mIsTwoPane = true;


        startFragment();

        if (savedInstanceState == null) {

        }
    }

    private void startFragment(){
      MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("mIsTwoPane",mIsTwoPane);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment, fragment).commit();
        fragment.setmNameListener(this);
    }




    @Override
    public void setSelectedName(Movie item) {

        //case one pane

        if (!mIsTwoPane){
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        }

        else {
            //case two_pane
            DetailsFragment mDetailsFragment=new DetailsFragment();
            Bundle extras=new Bundle();
            extras.putSerializable("item", item);
            mDetailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_details_view,mDetailsFragment,"").commit();

        }

    }
}