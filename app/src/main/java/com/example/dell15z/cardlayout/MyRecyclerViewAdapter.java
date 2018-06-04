package com.example.dell15z.cardlayout;

/**
 * Created by dell15z on 03-Jun-18.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;



    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        //Bitmap bmp = BitmapFactory.decodeFile(mDataset.get(position).getUri().toString());
       try{
           Bitmap bmp = MediaStore.Images.Media.getBitmap(holder.im1.getContext().getContentResolver(),mDataset.get(position).getUri() );
           holder.im1.setImageBitmap(bmp);
       }
       catch(IOException e){}

    }
    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder
           // implements View
           // .OnClickListener
           {
        private ImageView im1 = null;
        private Button validate;

        public DataObjectHolder(View itemView) {
            super(itemView);

            im1 = (ImageView) itemView.findViewById(R.id.im1view);
            validate=(Button)itemView.findViewById(R.id.validate);
            Log.i(LOG_TAG, "Adding Listener");

        }


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}