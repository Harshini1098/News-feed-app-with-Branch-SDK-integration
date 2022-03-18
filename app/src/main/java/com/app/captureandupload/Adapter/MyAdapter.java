package com.app.captureandupload.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.app.captureandupload.R;
import com.app.captureandupload.model.List_Data;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<List_Data> list_data;
    private Context context;

    public MyAdapter(List<List_Data> list_data, Context context) {
        this.list_data = list_data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewadapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        List_Data listData = list_data.get(position);

        String imgurl = listData.getThumbnail();

        Glide.with(context).load(imgurl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageview);
        if(listData.getThumbnail().equals("default")){
            holder.imageview.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
        }
        holder.tv_title.setText(listData.getTitle());
        holder.tv_des.setText(listData.getSelftext());
        if(listData.getSelftext().isEmpty()){
            holder.tv_des.setText("No Description Available");
        }
        Long dateValueInLong = System.currentTimeMillis();
        holder.tv_created.setText(dateValueInLong.toString());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd,yyyy ss:HH:mm");
        String dateTime = simpleDateFormat.format(listData.getCreated()).toString();
        holder.tv_created.setText(dateTime);

        holder.tv_ups.setText("UPs:"+String.valueOf(listData.getUps()));
    }
    @Override
    public int getItemCount() {
        return list_data.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title,tv_des,tv_created,tv_no_image,tv_ups;
        private ImageView imageview;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_des = (TextView) itemView.findViewById(R.id.tv_description);
            tv_created = (TextView) itemView.findViewById(R.id.tv_created);
            imageview = (ImageView) itemView.findViewById(R.id.thumbnail);
            tv_no_image=(TextView) itemView.findViewById(R.id.tv_no_image);
            tv_ups=(TextView) itemView.findViewById(R.id.tv_ups);
        }
    }
}

