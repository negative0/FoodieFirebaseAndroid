package com.fireblaze.evento.viewholders;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fireblaze.evento.R;
import com.fireblaze.evento.models.ImageItem;

import org.w3c.dom.Text;

/**
 * Created by chait on 8/27/2016.
 */

public class ImageItemHolder extends RecyclerView.ViewHolder {
    private ImageView imageRes;
    private TextView title;
    public ImageItemHolder(View itemView) {
        super(itemView);
        imageRes = (ImageView) itemView.findViewById(R.id.item_image);
        title = (TextView) itemView.findViewById(R.id.title);

    }
    public void bindToPost(Context context, ImageItem imageItem, View.OnClickListener clickListener){
        Glide.with(context).load(imageItem.getResourceURL()).error(R.drawable.logo_black).into(imageRes);
        imageRes.setOnClickListener(clickListener);
        title.setText(imageItem.getName());
    }
}
