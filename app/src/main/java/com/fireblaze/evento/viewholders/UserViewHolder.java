package com.fireblaze.evento.viewholders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireblaze.evento.R;
import com.fireblaze.evento.models.User;

/**
 * Created by fireblaze on 21/1/17.
 */

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView name, email, phone, college;
    private ImageView imageCall, imageEmail;
    private User user;
    private Context mContext;
    public UserViewHolder(View viewItem){
        super(viewItem);
        name = (TextView) viewItem.findViewById(R.id.text_name);
        email = (TextView) viewItem.findViewById(R.id.text_email);
        phone = (TextView) viewItem.findViewById(R.id.text_phone);
        college = (TextView) viewItem.findViewById(R.id.text_college);
        imageCall = (ImageView) itemView.findViewById(R.id.btn_call);
        imageEmail = (ImageView) itemView.findViewById(R.id.btn_send_email);



    }
    public void bindToPost(final Context context, User user, boolean isPresent){
        if(user== null){
            throw new IllegalArgumentException("User invalid");
        }
        mContext = context;
        this.user = user;
        name.setText(user.getName());
        email.setText(user.getEmailID());
        phone.setText(user.getPhone());
        college.setText(user.getCollegeName());
        if(!isPresent)
            imageEmail.setVisibility(View.GONE);
        imageEmail.setOnClickListener(this);

        imageCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_call:
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+user.getPhone()));
                mContext.startActivity(i);
                break;
            case R.id.btn_send_email:

                break;
        }
    }
}

