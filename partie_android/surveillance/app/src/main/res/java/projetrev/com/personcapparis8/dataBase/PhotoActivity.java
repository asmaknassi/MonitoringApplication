package projetrev.com.personcapparis8.dataBase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import projetrev.com.personcapparis8.DetectActivity;
import projetrev.com.personcapparis8.GraphActivity;
import projetrev.com.personcapparis8.R;


public class PhotoActivity extends ActionBarActivity {
    private PersonDataSource datasource;
    private ListView lv;
    private ArrayList<Bitmap> listPhoto;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_photo_main);

        datasource = new PersonDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        chargerImagePersonne();



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detect, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent;
        switch (item.getItemId()) {

            case R.id.gragh:
                intent = new Intent(this, GraphActivity.class);

                startActivity(intent);
                break;
            case R.id.camera:
                intent = new Intent(this, DetectActivity.class);

                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);

}


    @Override
    protected void onResume() {
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
//chargerImagePersonne();


    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    public void chargerImagePersonne() {
        lv = (ListView) findViewById(R.id.listPhoto);
        listPhoto = new ArrayList<Bitmap>();

        File sdCard = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File directory = new File(sdCard.getAbsolutePath() + "/MyCameraApp");

        File file[] = directory.listFiles();
        for (File f : file) {
            FileInputStream streamIn = null;
            try {
                streamIn = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                Log.e("ffffff1","fffffffffff");
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
            listPhoto.add(bitmap);
            Log.e("ffffff"+bitmap.toString(), "fffffffffff");

            try {
                streamIn.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ffffff2","fffffffffff");
            }

        }
        Toast toast = Toast.makeText(this, "chargement effectué", Toast.LENGTH_LONG);
        toast.show();
        Log.e("ffffff leng "+listPhoto.size(), "fffffffffff");
        lv.setAdapter(new MyAdapter(this,listPhoto));
    }

}







