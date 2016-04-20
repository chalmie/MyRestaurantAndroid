package com.epicodus.myrestaurant.ui;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.myrestaurant.R;
import com.epicodus.myrestaurant.models.Restaurant;
import com.epicodus.myrestaurant.services.YelpService;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RestaurantActivity extends AppCompatActivity {
    public static final String TAG = RestaurantActivity.class.getSimpleName();
    public ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    @Bind(R.id.locationTextView) TextView mLocationTextView;
    @Bind(R.id.listView) ListView mListView;
    private Toast mToastToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int toastDurationInMilliseconds = 500;
                String restaurant = ((TextView)view).getText().toString();
                mToastToShow = Toast.makeText(RestaurantActivity.this, restaurant, Toast.LENGTH_LONG);

                CountDownTimer toastCountDown;
                toastCountDown = new CountDownTimer(toastDurationInMilliseconds, 1000) {
                    @Override
                    public void onTick(long l) {
                        mToastToShow.show();
                    }

                    @Override
                    public void onFinish() {
                        mToastToShow.cancel();
                    }
                };

                mToastToShow.show();
                toastCountDown.start();
            }
        });


        Intent intent = getIntent();
        String location = intent.getStringExtra("location");
        getRestaurants(location);
        mLocationTextView.setText("Here are all the restaurants near: " + location);
    }

    private void getRestaurants(String location) {
        final YelpService yelpService = new YelpService();

        YelpService.findRestaurants(location, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                mRestaurants = yelpService.processResults(response);

                RestaurantActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] restaurantNames = new String[mRestaurants.size()];
                        for (int i = 0; i < restaurantNames.length; i++) {
                            restaurantNames[i] = mRestaurants.get(i).getName();
                        }
                        ArrayAdapter adapter = new ArrayAdapter(RestaurantActivity.this, android.R.layout.simple_list_item_1, restaurantNames);
                        mListView.setAdapter(adapter);

                        for (Restaurant restaurant : mRestaurants) {
                            Log.d(TAG, "Name: " + restaurant.getName());
                            Log.d(TAG, "Phone: " + restaurant.getPhone());
                            Log.d(TAG, "Website: " + restaurant.getWebsite());
                            Log.d(TAG, "Image url: " + restaurant.getImageUrl());
                            Log.d(TAG, "Rating: " + Double.toString(restaurant.getRating()));
                            Log.d(TAG, "Address: " + android.text.TextUtils.join(", ", restaurant.getAddress()));
                            Log.d(TAG, "Categories: " + restaurant.getCategories().toString());
                        }
                    }
                });
            }
        });
    }
}


