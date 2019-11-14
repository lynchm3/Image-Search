package com.marklynch.currencyfair.ui.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.marklynch.currencyfair.R;

import java.util.ArrayList;
import java.util.List;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    private Activity activity;
    private LayoutInflater mInflater;
    private List<String> photoUrls = new ArrayList<String>();

    private static RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round);

    public ImagesAdapter(
            Activity activity) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);

    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        //        ImageView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
//            ImageView imageView = itemView.findViewById(R.id.iv);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(mInflater.inflate(R.layout.list_item_image_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Glide.with(activity).load(photoUrls.get(position)).apply(options).into(((ImageView) holder.itemView));
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls.clear();
        this.photoUrls.addAll(photoUrls);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.photoUrls.size();
    }
}


