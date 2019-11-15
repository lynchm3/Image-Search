package com.marklynch.currencyfair.ui.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.marklynch.currencyfair.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    private Activity activity;
    private LayoutInflater mInflater;
    private List<String> photoUrls = new ArrayList<String>();
    private int spaceAtTopInPixels;

    private static RequestOptions options = new RequestOptions()
            .centerCrop();

    public ImagesAdapter(
            Activity activity) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);

        final float scale = activity.getResources().getDisplayMetrics().density;
        spaceAtTopInPixels = (int) ((56 + 28) * scale + 0.5f);
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageview);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(mInflater.inflate(R.layout.list_item_image_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        if (position == 0 || position == 1 || position == getItemCount() - 1 || position == getItemCount() - 2) {
            holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(spaceAtTopInPixels, spaceAtTopInPixels));
            holder.imageView.setVisibility(View.INVISIBLE);
        } else {
            String url = photoUrls.get(position - 2);
            holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(activity).load(url).apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.imageView);
            holder.imageView.setTag(R.id.url_tag, url);
        }
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls.clear();
        this.photoUrls.addAll(photoUrls);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.photoUrls.size() + 4;
    }
}


