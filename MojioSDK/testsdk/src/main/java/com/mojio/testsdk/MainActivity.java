package com.mojio.testsdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mojio.mojiosdk.MojioClient;
import com.mojio.mojiosdk.models.Vehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    MojioClient _mojioClient;

    // User config
    // TODO move to config file?
    private static String MOJIO_APP_ID = "<app ID>";
    private static String USER_SECRET_KEY = "<secret key>";
    private static String REDIRECT_URL = "<appname>://";

    // Request constants
    public static int ACCESS_TOKEN_REQUEST_CODE = 0;

    // Outlets
    private Button _loginButton;
    private ListView _listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _loginButton = (Button)findViewById(R.id.oauthButton);
        _listView = (ListView)findViewById(R.id.vehicleList);
        _mojioClient = new MojioClient(getApplicationContext(), MOJIO_APP_ID, USER_SECRET_KEY, REDIRECT_URL);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mojioClient.launchLoginActivity(MainActivity.this, ACCESS_TOKEN_REQUEST_CODE);
            }
        });

        if (_mojioClient.isUserLoggedIn()) {
            _loginButton.setText("Force another login");
            getVehicles();
            createStore("testAgainFromSDK", "bdbdbdbdbdb");
        }
        else {
            // Allow login
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == ACCESS_TOKEN_REQUEST_CODE) {
            Log.i("MOJIO", "onActivityResult called with ACCESS_TOKEN_REQUEST");
            // We now have a stored access token
            if (resultCode == RESULT_OK) {
                _loginButton.setText("Force another login");
                getVehicles();
            }
        }
    }

    // This gets an array of vehicles, single vehicles also work
    public void getVehicles() {
        // Now we can call the get.
        String vehicleURL = "Vehicles"; ///53cdeca5-b268-4a25-bfde-3938b5cf7d47";
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("limit", "3");
        queryParams.put("sortBy", "Name");
        queryParams.put("desc", "true");

        _mojioClient.get(Vehicle[].class, vehicleURL, queryParams, new MojioClient.ResponseListener<Vehicle[]>() {
            @Override
            public void onSuccess(Vehicle[] result) {
                Log.i("MOJIO", "Vehicle get success");
                loadList(result);
            }

            @Override
            public void onFailure(String error) {
                Log.e("MOJIO", "Vehicle get fail");
            }
        });
    }

    public void createStore(final String key, String body) {
        String storeUrl = "Vehicles/53cdeca5-b268-4a25-bfde-3938b5cf7d47/Store/"+ key;
        HashMap<String, String> queryParams = new HashMap<>();

        _mojioClient.create(String.class, storeUrl, body, new MojioClient.ResponseListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(MainActivity.this, "Create store success", Toast.LENGTH_LONG).show();
                getStore(key);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Create store fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getStore(final String key) {
        String storeUrl = "Vehicles/53cdeca5-b268-4a25-bfde-3938b5cf7d47/Store/"+ key;
        HashMap<String, String> queryParams = new HashMap<>();

        _mojioClient.get(String.class, storeUrl, queryParams, new MojioClient.ResponseListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(MainActivity.this, "Get store success: " + result, Toast.LENGTH_LONG).show();
                deleteStore(key);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Get store fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteStore(String key) {
        String storeUrl = "Vehicles/53cdeca5-b268-4a25-bfde-3938b5cf7d47/Store/"+ key;

        _mojioClient.delete(String.class, storeUrl, new MojioClient.ResponseListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(MainActivity.this, "Delete store success: " + result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Delete store fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadList(Vehicle[] listData) {
        VehicleListAdapter adapter = new VehicleListAdapter(this, new ArrayList<Vehicle>(Arrays.asList(listData)));
        _listView.setAdapter(adapter);
    }
}
