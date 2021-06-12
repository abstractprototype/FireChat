package com.firstapp.firebasechat.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstapp.firebasechat.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

//Populates imageview with the image uri
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder>{

    ArrayList<String> mediaList;
    Context context;

    public MediaAdapter(Context context, ArrayList<String> mediaList) {//Arraylist is passed from mediaUriList from MessageActivity
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, null, false);
        MediaViewHolder mediaViewHolder = new MediaViewHolder(layoutView);

        return mediaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaAdapter.MediaViewHolder holder, int position) {
        Glide.with(context).load(Uri.parse(mediaList.get(position))).into(holder.mMedia);//Parse string from our list into uri
    }

    @Override
    public int getItemCount() {
        return mediaList.size();//Gets all the media and shows it in the recyclerview
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder{

        ImageView mMedia;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mMedia = itemView.findViewById(R.id.media); //Locates the image view in item_media.xml
        }
    }

}
