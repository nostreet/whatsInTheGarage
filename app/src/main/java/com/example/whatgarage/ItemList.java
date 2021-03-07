package com.example.whatgarage;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ItemList extends AppCompatActivity {

    GridView gridView;
    ArrayList<StockItem> list;
    StorageListAdapter adapter = null;

    RadioButton radioAll, radioFreezer, radioShelve, radioCleaning, radioOther;

    RadioGroup radioGroupTopRow;
    String globalSortingQuery = "all";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_list_activity);

        gridView = findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new StorageListAdapter(this, R.layout.storage_items, list);
        gridView.setAdapter(adapter);

        radioAll = findViewById(R.id.radioAll);
        radioFreezer = findViewById(R.id.radioFreezer);
        radioShelve = findViewById(R.id.radioShelve);
        radioCleaning = findViewById(R.id.radioCleaning);
        radioOther = findViewById(R.id.radioOther);

        radioGroupTopRow = findViewById(R.id.radioGroup1);

        radioAll.setChecked(true);

        refreshViewNewSortAll();

        radioGroupTopRow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId != -1) {

                    boolean radioAllIsChecked = radioAll.isChecked();
                    boolean radioFreezerIsChecked = radioFreezer.isChecked();
                    boolean radioShelveIsChecked = radioShelve.isChecked();
                    boolean radioCleaningIsChecked = radioCleaning.isChecked();
                    boolean radioOtherIsChecked = radioOther.isChecked();


                    if (radioAllIsChecked) {
                        globalSortingQuery = "all";
                        refreshViewNewSortAll();
                    } else if (radioFreezerIsChecked) {
                        refreshViewNewSort("freezer");
                    } else if (radioShelveIsChecked) {
                        refreshViewNewSort("shelve food");
                    } else if (radioCleaningIsChecked) {
                        refreshViewNewSort("cleaning");
                    } else if (radioOtherIsChecked) {
                        refreshViewNewSort("other");
                    }
                }
            }
        });



        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ItemList.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT " +
                                    SQLiteHelper.COLUMN_ID + " FROM " +
                                    SQLiteHelper.TABLE_NAME);

                            // show new dialog update at here
                            showDialogUpdate(ItemList.this, list.get(position).getId(), position);

                        } else {
                            // delete
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT " +
                                    SQLiteHelper.COLUMN_ID + " FROM " +
                                    SQLiteHelper.TABLE_NAME);

                            showDialogDelete(list.get(position).getId());
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void refreshViewNewSort(String sortQuery) {
        //save string sortQuery to a global variable for updates/deleting refreshes
        globalSortingQuery = sortQuery;

        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM " + SQLiteHelper.TABLE_NAME  + " WHERE " +
                SQLiteHelper.COLUMN_STORED_TYPES + " = '" + sortQuery + "' ORDER BY " + SQLiteHelper.COLUMN_NAME + " COLLATE NOCASE");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int existingQuantity = cursor.getInt(2);
            int recommendedStock = cursor.getInt(3);
            String type = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            list.add(new StockItem(id, name, existingQuantity, recommendedStock, type, image));
        }
        adapter.notifyDataSetChanged();
    }

    private void refreshViewNewSortAll() {
        // get all data from sqlite
        try {
            Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " ORDER BY " + SQLiteHelper.COLUMN_NAME + " COLLATE NOCASE");
            list.clear();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int existingQuantity = cursor.getInt(2);
                int recommendedStock = cursor.getInt(3);
                String type = cursor.getString(4);
                byte[] image = cursor.getBlob(5);

                list.add(new StockItem(id, name, existingQuantity, recommendedStock, type, image));
            }
            adapter.notifyDataSetChanged();
        } catch (IllegalStateException e) {
            e.getMessage();
        }
    }

    ImageView imageViewStockItem;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showDialogUpdate(Activity activity, final int position, final int itemPosition){

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_stock_list_activity);
        dialog.setTitle("Update");

        //connects the layout

        //loads existing picture & converting byte array to the image
        imageViewStockItem = dialog.findViewById(R.id.imageViewStockItem);
        byte[] byteArray = list.get(itemPosition).getImage();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageViewStockItem.setImageBitmap(bmp);

        //load the editText views
        EditText edtNameSet = dialog.findViewById(R.id.edtName);
        EditText editExiQuantitySet = dialog.findViewById(R.id.edtExiQuantity);
        EditText edtRecStockSet = dialog.findViewById(R.id.edtRecStock);
        Spinner spinnerTypeUpdate = dialog.findViewById(R.id.spinner_type_update);
        spinnerTypeUpdate.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.storeTypes, R.layout.spinner_item));
        final Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        //grabs the existing data
        edtNameSet.setText(list.get(itemPosition).getName());
        editExiQuantitySet.setText(Integer.toString(list.get(itemPosition).getExistingQuantity()));
        edtRecStockSet.setText(Integer.toString(list.get(itemPosition).getRecommendedStock()));

        //spinner
        int positionSpinner = getOldSpinnerPosition(list.get(itemPosition).getStoredType());
        spinnerTypeUpdate.setSelection(positionSpinner);

        final EditText edtName = edtNameSet;
        final EditText editExiQuantity = editExiQuantitySet;
        final EditText edtRecStock = edtRecStockSet;
        final Spinner spinnerStoredType = spinnerTypeUpdate;

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.9);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewStockItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        ItemList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.sqLiteHelper.updateData(
                            edtName.getText().toString().trim(),
                            Integer.parseInt(editExiQuantity.getText().toString().trim()),
                            Integer.parseInt(edtRecStock.getText().toString().trim()),
                            spinnerStoredType.getSelectedItem().toString().trim(),
                            MainActivity.imageViewToByte(imageViewStockItem),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successfully!!!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception error) {
                    Log.e("Update error", error.getMessage());
                }
                updateStockItemList();
            }
        });
    }

    private int getOldSpinnerPosition (String type) {
        if (type.equals("freezer")) {
            return 0;
        } else if (type.equals("shelve food")) {
            return 1;
        } else if (type.equals("cleaning")) {
            return 2;
        } else {
            return 3;
        }
    }
    private void showDialogDelete(final int idItem){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(ItemList.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Arrrrrrr! Sure to delete?");
        dialogDelete.setPositiveButton("Roger Roger!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelper.deleteData(idItem);
                    Toast.makeText(getApplicationContext(), "Delete successfully!!!", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateStockItemList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    //This reloads the grid list view to see updates in the gridview as example
    private void updateStockItemList(){

        if (    globalSortingQuery.equals("freezer") ||
                globalSortingQuery.equals("shelve food") ||
                globalSortingQuery.equals("cleaning") ||
                globalSortingQuery.equals("other")) {
                    refreshViewNewSort(globalSortingQuery);
        } else {
            refreshViewNewSortAll();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewStockItem.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}