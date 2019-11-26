package com.marklynch.flickrsearch.ui.main;

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
import com.marklynch.flickrsearch.R;

import java.util.ArrayList;
import java.util.List;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    private final LayoutInflater mInflater;
    private final List<ImageToDisplay> imagesToDisplay = new ArrayList<>();
    private final int spaceAtTopInPixels;

    private static final RequestOptions options = new RequestOptions()
            .centerCrop();
    private final ImageZoomer imageZoomer;

    public ImagesAdapter(
            Activity activity, ImageZoomer imageZoomer) {
        this.imageZoomer = imageZoomer;
        mInflater = LayoutInflater.from(activity);

        final float scale = activity.getResources().getDisplayMetrics().density;
        spaceAtTopInPixels = (int) ((56 + 28) * scale + 0.5f);
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageview);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(mInflater.inflate(R.layout.image_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        if (position == 0 || position == 1 || position == getItemCount() - 1 || position == getItemCount() - 2) {
            holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(spaceAtTopInPixels, spaceAtTopInPixels));
            holder.imageView.setVisibility(View.INVISIBLE);
        } else {
            String thumbUrl = imagesToDisplay.get(position - 2).thumb.url;
            holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.imageView).load(thumbUrl).apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(holder.imageView);
            holder.imageView.setTag(R.id.url_tag, thumbUrl);

            holder.itemView.setOnClickListener(
                    v -> imageZoomer.zoomImageFromThumb(holder.imageView, imagesToDisplay.get(position - 2))
            );
        }
    }

    public void setImagesToDisplay(ImagesToDisplay imagesToDisplay) {
        this.imagesToDisplay.clear();
        this.imagesToDisplay.addAll(imagesToDisplay.images);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.imagesToDisplay.size() + 4;
    }

    public interface ImageZoomer {
        void zoomImageFromThumb(final ImageView thumbView, ImageToDisplay imageToDisplay);
    }


}

