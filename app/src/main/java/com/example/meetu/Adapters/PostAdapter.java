package com.example.meetu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetu.Models.PostModel;
import com.example.meetu.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyHolder>{

    Context context;
    List<PostModel> postModelList;

    public PostAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    public void setFilteredList(List<PostModel> filteredList) {
        this.postModelList = filteredList;
        notifyDataSetChanged();;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_post, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);

        PostModel postModel = postModelList.get(position);
        String title = postModel.getPostTitle();
        String description = postModel.getPostDescription();
        String image = postModel.getPostImage();

        holder.postTitle.setText(title);
        holder.postDescription.setText(description);
        Glide.with(context).load(image).into(holder.postImage);

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView ivImage = holder.postImage;
                String shareBody = "Look at this amazing post on Meet U: "
                        + title + ": " + description + " See also picture attached!";
                Uri bmpUri = getLocalBitmapUri(ivImage);
                if (bmpUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    shareIntent.setType("image/*");
                    context.startActivity(Intent.createChooser(shareIntent, "Send to"));
                } else {
                    Toast.makeText(context, "Something wrong happened!", Toast.LENGTH_LONG).show();
                }

            }
        });
        holder.itemView.startAnimation(animation);

    }

    private Uri getLocalBitmapUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(context, "com.codepath.fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView postImage;
        TextView postTitle, postDescription;
        Button share;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postImage);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDescription = itemView.findViewById(R.id.postDescription);
            share = itemView.findViewById(R.id.btnShare);
        }
    }
}
