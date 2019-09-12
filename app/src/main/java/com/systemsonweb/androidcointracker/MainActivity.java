package com.systemsonweb.androidcointracker;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.systemsonweb.androidcointracker.Adapter.CoinAdapter;
import com.systemsonweb.androidcointracker.Interface.ILoadMore;
import com.systemsonweb.androidcointracker.Model.CoinModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<CoinModel> items = new ArrayList<>();
    CoinAdapter adapter;
    RecyclerView recyclerView;

    //my Key
    private static String apiKey = "ebd8bf0f-75d5-4c49-8e15-e1e07c77e652";
    //String uri = " https://CoinMarketCapzakutynskyV1.p.rapidapi.com/getCryptocurrenciesList";
    String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

    OkHttpClient client;
    Request request;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.rootLayout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadFirst10Coin(0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                loadFirst10Coin(0);
                setupAdapter();

            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.coinList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupAdapter();


    }


    private void setupAdapter(){
        adapter = new CoinAdapter(recyclerView, MainActivity.this, items);
        recyclerView.setAdapter(adapter);
        adapter.setiLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                if(items.size() <= 1000)    //Max size is 1000
                {
                    loadNext10Coin(items.size());
                }
                else
                    {
                        Toast.makeText(MainActivity.this, "Max items is 1000", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    private void loadNext10Coin(int index){
        client = new OkHttpClient();
        //"https://pro-api.coinmarketcap.com/cryptocurrency/listings/latest?limit=1000"
        request = new Request.Builder().url(String.format(uri,index))
            .build();
        swipeRefreshLayout.setRefreshing(true);
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().toString();
                        Gson gson = new Gson();
                        final List<CoinModel> newItems = gson.fromJson(body, new TypeToken<List<CoinModel>>(){}.getType());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                items.addAll(newItems);
                                adapter.setLoaded();
                                adapter.updateData(items);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });

                    }
                });

    }

    private void loadFirst10Coin(int index){
        client = new OkHttpClient();
        //"https://pro-api.coinmarketcap.com/cryptocurrency/listings/latest?limit=1000"
        request = new Request.Builder().url(String.format(uri,index))
                .build();
        swipeRefreshLayout.setRefreshing(true);
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().toString();
                        Gson gson = new Gson();
                        final List<CoinModel> newItems = gson.fromJson(body, new TypeToken<List<CoinModel>>(){}.getType());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adapter.updateData(newItems);

                            }
                        });

                    }
                });

        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }



}
