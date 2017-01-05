package projetrev.com.personcapparis8;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import projetrev.com.personcapparis8.dataBase.PersonDataSource;
import projetrev.com.personcapparis8.dataBase.PhotoActivity;


public class DetectActivity extends ActionBarActivity implements View.OnClickListener {
    private Camera mCamera;
    private DetectSurfaceView mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture;
    private DetectActivity myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private boolean flag = false;
    private boolean takePhoto = false;
    private MediaPlayer mediaPlayer;
    private Boolean songPlay = false;
    Button bConnect, bDisconnect;

    private PersonDataSource datasource;
    Bluetooth bluetoothGraph;

    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (Bluetooth.connectedThread != null) {
            Bluetooth.connectedThread.write("Q");
        }//Stop streaming
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detect);


        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        myContext = this;
        mPicture = getPictureCallback();

        datasource = new PersonDataSource(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.alert);
        if (mediaPlayer == null) {

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    flag = true;
                }
            });
        }

        mPreview = new DetectSurfaceView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Bluetooth.gethandler(mHandler);
        ButtonInit();



    }

    void ButtonInit() {
        bConnect = (Button) findViewById(R.id.bConnectCamera);
        bConnect.setOnClickListener(this);
        bDisconnect = (Button) findViewById(R.id.bDisconnectCamera);
        bDisconnect.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {

            case R.id.gragh:
                intent = new Intent(this, GraphActivity.class);

                startActivity(intent);
                break;
            case R.id.listPhoto:
                intent = new Intent(this, PhotoActivity.class);

                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }
        return c;
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;

        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;

        int numberOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }
    protected void sendSMSMessage() {
        Log.i("Send SMS", "");
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String formattedDate = format.format(date);

        String phoneNo = "+33673780622";
        String message = " Alerte! un mouvement a été détécté : "+formattedDate;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS envoyé.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "erreur.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "ce portable ne dispose pas d'une caméra !", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }


        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() == 1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa

                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(myContext, "votre portable dispose d'une seule caméra !", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.alert);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    flag = true;
                }
            });

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.set("orientation", "portrait");
        mCamera.setParameters(parameters);


    }

    ;

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();

                mPreview.refreshCamera(mCamera);

            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseCamera();
        mediaPlayer.pause();
    }

    private boolean hasCamera(Context context) {

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {

                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(myContext, "Une photo a été prise " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();


                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }


    //make picture and save to a folder
    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        //si le dossier MyCameraApp n'éxiste pas
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //réccuperer l'heure
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //créer un fichier de sauvegarde
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private void releaseCamera() {

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    Handler mHandler;

    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case Bluetooth.SUCCESS_CONNECT:
                        Bluetooth.connectedThread = new Bluetooth.ConnectedThread((BluetoothSocket) msg.obj);
                        Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_LONG).show();
                        String s = "successfully connected";
                        bluetoothGraph.connectedThread.start();
                        break;
                    case Bluetooth.MESSAGE_READ:

                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, 3);

                        Log.d("strIncom Camera ", strIncom);


                        strIncom = strIncom.replace("s", "");

                        if (isFloatNumber(strIncom)) {
                            float distance = Float.parseFloat(strIncom);
                            if (distance < 10 && distance >= 3) {
                                if (!songPlay) songPlay = true;

                                if (songPlay && !mediaPlayer.isPlaying() && flag) {
                                    mediaPlayer.start();
                                }
                                if(!takePhoto){
                                    mCamera.takePicture(null, null, mPicture);
                                    sendSMSMessage();
                                   takePhoto = true;
                                }
                            } else {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                    mediaPlayer.seekTo(0);
                                }
                                songPlay = false;
                                takePhoto = false;
                            }


                        }


                        break;
                }
            }

            public boolean isFloatNumber(String num) {
                //Log.d("checkfloatNum", num);
                try {
                    Double.parseDouble(num);
                } catch (NumberFormatException nfe) {
                    return false;
                }
                return true;
            }

        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bConnectCamera:
                startActivity(new Intent("android.intent.action.BT1"));
                break;
            case R.id.bDisconnectCamera:
                Bluetooth.disconnect();
                break;


        }
    }




}

