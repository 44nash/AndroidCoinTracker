package com.systemsonweb.androidcointracker.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.systemsonweb.androidcointracker.Interface.ILoadMore;
import com.systemsonweb.androidcointracker.Model.CoinModel;
import com.systemsonweb.androidcointracker.R;

import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ILoadMore iLoadMore;
    boolean isLoading;
    Activity activity;
    List<CoinModel> items;

    int visibleThreshold = 5, lastVisibleItem, totalItemCount;


    public CoinAdapter(RecyclerView recyclerView, Activity activity, List<CoinModel> items) {
        this.activity = activity;
        this.items = items;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalItemCount <= (lastVisibleItem+visibleThreshold))
                {
                    if(iLoadMore != null)
                    {
                        iLoadMore.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });


    }

    public void setiLoadMore(ILoadMore iLoadMore) {
        this.iLoadMore = iLoadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.coin_layout, parent,false);

        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CoinModel item = items.get(position);
        CoinViewHolder holderItem = (CoinViewHolder)holder;

        holderItem.coin_name.setText(item.getName());
        holderItem.coin_symbol.setText(item.getSymbol());
        holderItem.coin_price.setText(item.getPrice_usd());
        holderItem.one_hour_change.setText(item.getPercent_change_1h()+"%");
        holderItem.twenty_hours_change.setText(item.getPercent_change_24h()+"%");
        holderItem.seven_days_change.setText(item.getPercent_change_7d()+"%");

        //Load images
        Picasso.with(activity)
                .load(new StringBuilder("https://res.cloudinary.com/dxi90ksom/image/upload/")
                .append(item.getSymbol().toLowerCase()).append(".png").toString())
                .into(holderItem.coin_icon);

        holderItem.one_hour_change.setTextColor(item.getPercent_change_1h().contains("-")?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));
        holderItem.twenty_hours_change.setTextColor(item.getPercent_change_24h().contains("-")?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));
        holderItem.seven_days_change.setTextColor(item.getPercent_change_7d().contains("-")?
                Color.parseColor("#FF0000"):Color.parseColor("#32CD32"));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoaded(){isLoading = true;}

    public void updateData(List<CoinModel> coinModels)
    {
        this.items = coinModels;
        notifyDataSetChanged();
    }


}
