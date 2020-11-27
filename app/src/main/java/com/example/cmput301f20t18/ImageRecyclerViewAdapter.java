package com.example.cmput301f20t18;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder> {


    private ArrayList<Bitmap> photos;



    public ImageRecyclerViewAdapter(ArrayList<Bitmap> photos){
        this.photos = photos;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_single_view,parent,false);
        ImageViewHolder imageHolder = new ImageViewHolder(view);
        return imageHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageView imageView = holder.bookImage;
        FloatingActionButton button = holder.button;
        //Bitmap bm = photoAdapter.scaleBitmap(photos.get(position), imageView.getWidth(), imageView.getHeight());
        imageView.setImageBitmap(photos.get(position));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FLOAT CLICK", "onClick: postion of " + position);
                photos.remove(position);
                notifyDataSetChanged();

            }
        });


    }

    public ArrayList<Bitmap> getPhotos() {
        return photos;
    }

    @Override
    public int getItemCount() {
        return photos.size(); // This needs to be set to the length of the container
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView bookImage;
        FloatingActionButton button;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.book_image_view);
            button =  itemView.findViewById(R.id.clear_image);
        }

    }
}
