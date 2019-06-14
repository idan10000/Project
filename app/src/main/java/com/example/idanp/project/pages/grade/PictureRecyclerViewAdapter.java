package com.example.idanp.project.pages.grade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.idanp.project.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class PictureRecyclerViewAdapter extends RecyclerView.Adapter<PictureRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "PictureRecyclerViewAdapter";

    private DocumentReference dbReference;
    private StorageReference storageReference;
    private ArrayList<String> imageUrls;
    private Context context;

    public PictureRecyclerViewAdapter(Context context, ArrayList<String> imageUrls, DocumentReference dbReference, StorageReference storageReference) {
        this.imageUrls = imageUrls;
        this.context = context;
        this.dbReference = dbReference;
        this.storageReference = storageReference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grade_pictures_recyclerview, viewGroup, false);
        PictureRecyclerViewAdapter.ViewHolder holder = new PictureRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called.");

        final String curImage = imageUrls.get(i);
        Glide.with(context)
                .asBitmap()
                .load(curImage)
                .into(viewHolder.image);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Subject delete dialog clicked on");
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                dbReference.update("images",FieldValue.arrayRemove(curImage)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "deleting from the database");
                                        Toast.makeText(context, "Picture successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                storageReference.child(curImage).delete();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to delete the picture?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        if(imageUrls!=null)
            return imageUrls.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView delete;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivGradePRVImage);
            delete = itemView.findViewById(R.id.tvGradePRVDelete);
        }
    }
}
