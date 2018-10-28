package com.fireblaze.foodiee.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.models.ImageItem;

/**
 * Created by chait on 8/27/2016.
 */

public class ImageItemHolder extends RecyclerView.ViewHolder {
    private View item;
    private ImageView imageRes;
    private TextView title;
    public ImageItemHolder(View itemView) {
        super(itemView);
        imageRes = (ImageView) itemView.findViewById(R.id.item_image);
        title = (TextView) itemView.findViewById(R.id.title);
        item = itemView;

    }
    public void bindToPost(Context context, ImageItem imageItem, View.OnClickListener clickListener){
        try {
            Glide.with(context).load(imageItem.getResourceURL())
                    .into(imageRes);
        }catch(IllegalArgumentException e){
            Log.e("ImageItemViewHolder",e.getMessage());
        }
        item.setOnClickListener(clickListener);
        title.setText(imageItem.getName());


    }
}
