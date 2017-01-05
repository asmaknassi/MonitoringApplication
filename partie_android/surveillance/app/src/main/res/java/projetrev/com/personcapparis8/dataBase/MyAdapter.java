package projetrev.com.personcapparis8.dataBase;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import projetrev.com.personcapparis8.R;


public class MyAdapter extends BaseAdapter {

    private Context context;
    ArrayList<Bitmap> listImage ;

    public void setListImage(ArrayList<Bitmap> listImage) {
        this.listImage = listImage;
    }


    public MyAdapter(Context context,ArrayList<Bitmap> lisIm) {
        this.context = context;
        listImage = lisIm;


    }
    private LayoutInflater layoutInflater;

    public MyAdapter(PhotoActivity activity,ArrayList<Bitmap> imageSet) {
// TODO Auto-generated constructor stub
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listImage = imageSet;
    }

    public int getCount() {
        return listImage.size();
    }

    @Override
    public Object getItem(int position) {
        return listImage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

// Inflate the item layout and set the views
        View listItem = convertView;
        int pos = position;
        if (listItem == null) {
            listItem = layoutInflater.inflate(R.layout.list_item, null);
        }

// Initialize the views in the layout
        ImageView iv = (ImageView) listItem.findViewById(R.id.personneImage);


// Set the views in the layout
      //  Bitmap image = retrieveContactPhoto(this.context,contacts.get(position).getNumber());

            iv.setImageBitmap(listImage.get(position));

        return listItem;
    }




}