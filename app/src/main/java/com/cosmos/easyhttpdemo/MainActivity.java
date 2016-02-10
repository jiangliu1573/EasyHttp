package com.cosmos.easyhttpdemo;

import android.app.Activity;
import android.os.Bundle;


import com.cosmos.easyhttp.base.Request;
import com.cosmos.easyhttp.core.RequestQueue;
import com.cosmos.easyhttp.core.SimpleHttp;
import com.cosmos.easyhttp.requests.JsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark.
 *
 * Des:
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Map<String, String> map = new HashMap<>();
        map.put("user_name", "123");

        RequestQueue queue = SimpleHttp.newRequestQueue();

        String url = "http://192.168.1.102:8080/ServletTry/login?user_name=123";

//        StringRequest request = new StringRequest(url, map, new Request.RequestListener<String>() {
//            @Override
//            public void onComplete(String response) {
//                Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });

        JsonRequest request = new JsonRequest(url, (new JSONObject(map)).toString(), new Request.RequestListener<JSONObject>() {
            @Override
            public void onComplete(JSONObject response) {

            }
        });
        queue.addRequest(request);
    }
}
