package com.example.cmput301f20t18;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder> {


    private ArrayList<Bitmap> photos;
    private MyBooksAddBook.addListener addListener;




    public ImageRecyclerViewAdapter(ArrayList<Bitmap> photos, MyBooksAddBook.addListener addListener){
        this.photos = photos;
        this.photos.add(null);
        this.addListener = addListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_single_view,parent,false);
        ImageViewHolder imageHolder = new ImageViewHolder(view);
        return imageHolder;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageButton imageView = holder.bookImage;
        FloatingActionButton button = holder.button;

        if (position < this.photos.size() -1) {
            Bitmap bm = photoAdapter.scaleBitmap(photos.get(position), imageView.getLayoutParams().width, imageView.getLayoutParams().height);
            imageView.setImageBitmap(bm);
            button.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FLOAT CLICK", "onClick: postion of " + position);
                    photos.remove(photos.size()-1);
                    photos.remove(position);
                    photos.add(null);
                    notifyDataSetChanged();

                }
            });

        }
        else{

            imageView.setImageDrawable(imageView.getContext().getDrawable(R.drawable.ic_activeaddimage));
            button.hide();

            imageView.setOnClickListener(addListener);

        }


    }



    public ArrayList<Bitmap> getPhotos() {

        return (ArrayList<Bitmap>) photos.subList(0, photos.size()-1);

    }

    public void addData(Bitmap bm){
        this.photos.add(this.photos.size()-1, bm);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photos.size(); // This needs to be set to the length of the container
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageButton bookImage;
        FloatingActionButton button;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.book_image_view);
            button =  itemView.findViewById(R.id.clear_image);
        }

    }
}
