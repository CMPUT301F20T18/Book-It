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

/**
 * This is the adapter that sets the option to add and present the image that will be add to books
 * The adapter is used in the class of MyBooksAddBook
 * @see Book
 * @see MyBooksAddBook
 * @author Johnathon
 * @author Sean
 */

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder> {


    private ArrayList<Bitmap> photos;
    private ArrayList<Bitmap> photoData;
    private MyBooksAddBook.addListener addListener;

    /**
     * Initialize adapter (Constructor)
     * @param addListener This is to add the functionality to the add photo button
     * @param photos The photos of a book
     */

    public ImageRecyclerViewAdapter(ArrayList<Bitmap> photos, MyBooksAddBook.addListener addListener){
        this.photos = photos;
        this.photos.add(null);
        this.addListener = addListener;
        this.photoData = new ArrayList<Bitmap>();

    }

    /**
     * This initializes a ImageView to be presented in the RecyclerView
     * @param parent Is indicative of the view
     * @param viewType This will indicate the type of view used
     * @return the ImageViewHolder created
     */

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_single_view,parent,false);
        ImageViewHolder imageHolder = new ImageViewHolder(view);
        return imageHolder;
    }

    /**
     * This binds the ImageView created previously and binds it to the designated
     * space with the RecyclerView
     * @param holder Is what is being binded to the RecylerView
     * @param position This indicates the position within the RecyclerView
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageButton imageView = holder.bookImage;
        FloatingActionButton button = holder.button;

        if (position < this.photos.size() -1) {
            Bitmap bm = photoAdapter.scaleBitmap(photos.get(position), imageView.getLayoutParams().width, imageView.getLayoutParams().height);
            photoData.add(bm);
            imageView.setImageBitmap(bm);
            button.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FLOAT CLICK", "onClick: postion of " + position);
                    photos.remove(photos.size()-1);
                    photos.remove(position);
                    photos.add(null);
                    photoData.clear();
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

    /**
     * Returns the photos for the RecylerView
     * @return The photos, as an ArrayList<> of Bitmaps
     */

    public ArrayList<Bitmap> getPhotos() {

        return this.photoData;

    }

    /**
     * This just adds the image data from a respective book object and
     * saves it in a ArrayList of Bitmaps
     * @param bm Is the corresponding bitmap to be added in the ArrayList
     */

    public void addData(Bitmap bm){
        this.photos.add(this.photos.size()-1, bm);
        this.photoData = new ArrayList<Bitmap>();
        notifyDataSetChanged();
    }

    /**
     * Returns the number of photos of a certain book object
     * @return The count, as an integer
     */

    @Override
    public int getItemCount() {
        return photos.size(); // This needs to be set to the length of the container
    }


    /**
     * This is just an additional class that creates an object of ImageViewHolder
     * @author Sean
     */

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
