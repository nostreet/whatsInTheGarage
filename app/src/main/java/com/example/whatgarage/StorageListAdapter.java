package com.example.whatgarage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class StorageListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<StockItem> storageList;

    public StorageListAdapter(Context context, int layout, ArrayList<StockItem> storageList) {
        this.context = context;
        this.layout = layout;
        this.storageList = storageList;
    }

    @Override
    public int getCount() {
        return storageList.size();
    }

    @Override
    public Object getItem(int position) {
        return storageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtQuantity, txtRecStock, txtType;
    }

    // COULD BE USED FOR SORTING AFTER A TYPE BY ADDING IT TO THE HOLDER
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = row.findViewById(R.id.txtName);
            holder.txtQuantity = row.findViewById(R.id.txtQuantity);
            holder.txtRecStock = row.findViewById(R.id.txtRecStock);
            holder.imageView = row.findViewById(R.id.imgFood);
            holder.txtType = row.findViewById(R.id.txtType);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        StockItem stockItem = storageList.get(position);

        String recommendedStock = "(" + stockItem.getRecommendedStock() + ")";

        holder.txtName.setText(stockItem.getName());
        holder.txtQuantity.setText(Integer.toString(stockItem.getExistingQuantity()));
        holder.txtRecStock.setText(recommendedStock);
        holder.txtType.setText(stockItem.getStoredType());

        byte[] foodImage = stockItem.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
