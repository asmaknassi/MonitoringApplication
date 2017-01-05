package projetrev.com.personcapparis8;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import projetrev.com.personcapparis8.dataBase.PersonDataSource;

import static projetrev.com.personcapparis8.R.id.Graph;

public class GraphActivity extends Activity implements View.OnClickListener {

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (Bluetooth.connectedThread != null) {
            Bluetooth.connectedThread.write("Q");
        }//Stop streaming
        super.onBackPressed();
    }

    Boolean active = false;
    Paint paint, paintPoint, paintDraw;
    Path path, pathPoint, pathValue;
    LinkedList<Point> listPoint;
    ArrayList<Point> pointGraphX;
    ArrayList<Point> pointGraphY;
    int[] valuePointX;
    Boolean scrollActive = false;
    private PersonDataSource datasource;

    int largeur, hauteur, compteur;


    SeekBar seekReg;
    int point1, point2;

    int sleepValue = 300;
    Button bConnect, bDisconnect, effacer,bchargement;
    FrameLayout preview;
    SurfaceHolder mHolder;
    GraphViewPerso graphPerso;

    Handler handlerGraph;

    {
        handlerGraph = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case Bluetooth.SUCCESS_CONNECT:
                        Bluetooth.connectedThread = new Bluetooth.ConnectedThread((BluetoothSocket) msg.obj);
                        Toast.makeText(getApplicationContext(), "Connecté!", Toast.LENGTH_LONG).show();
                        String s = "successfully connected";
                        Bluetooth.connectedThread.start();
                        break;
                    case Bluetooth.MESSAGE_READ:

                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, 3);                 // create string from bytes array
                        Log.d("strIncom", strIncom);
                        float graphHauteur = pointGraphY.get(pointGraphY.size() - 1).y;

                        if (isFloatNumber(strIncom)) {
                            Double distance = Double.parseDouble(strIncom);
                            datasource.createPerson(""+distance, "", "");

                            if (compteur > pointGraphX.get(valuePointX.length - 1).x - 80) {
                                scrollActive = true;
                                Log.d("strIncom", "jj");
                                addX();
                                compteur -= 80;

                            } else {
                                compteur += 80;
                                listPoint.add(new Point(compteur, (float) ((graphHauteur - (distance )))));
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        datasource = new PersonDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //set background color
//		LinearLayout background = (LinearLayout)findViewById(R.id.bg);
//		background.setBackgroundColor(Color.BLACK);
        //init();
        ButtonInit();
        preview = (FrameLayout) findViewById(R.id.Graph);

        graphPerso = new GraphViewPerso(this);

        preview.addView(graphPerso);


        //initParam();


    }




    void ButtonInit() {
        bConnect = (Button) findViewById(R.id.bConnect);
        seekReg = (SeekBar) findViewById(R.id.barReg);
        bConnect.setOnClickListener(this);
        bDisconnect = (Button) findViewById(R.id.bDisconnect);
        effacer = (Button) findViewById(R.id.effacer);
        effacer.setOnClickListener(this);
        bDisconnect.setOnClickListener(this);
        bchargement = (Button) findViewById(R.id.bdchargement);
        bchargement.setOnClickListener(this);
/*
        seekReg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 5;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress += progresValue;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sleepValue = progress*100;
                progress = 5;

            }
        });*/


    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.bConnect:
                startActivity(new Intent("android.intent.action.BT1"));
                break;

            case R.id.bDisconnect:
                Bluetooth.disconnect();
                break;

            case R.id.effacer:
                Log.e("init", "init param");
                initParam();
                break;

            case R.id.bdchargement:
                initParam();
                int compt = 80;
                float graphHauteur = pointGraphY.get(pointGraphY.size() - 1).y;
                if(datasource.getAllPerson().size()> 24){

                    for (int i = datasource.getAllPerson().size()-15 ; i < datasource.getAllPerson().size();i++){
                     float dist =   graphHauteur - ( Float.parseFloat(datasource.getAllPerson().get(i).getDistance()));


                        listPoint.add(new Point(compt, dist));

                        compt+=80;
                    }
                    compt = 80;
                }else {
                    for (int i = 0; i < datasource.getAllPerson().size(); i++) {

                        float dist =   graphHauteur - ( Float.parseFloat(datasource.getAllPerson().get(i).getDistance()));

                        listPoint.add(new Point(compt, dist));

                        compt+=80;
                    }
                    compt = 80;
                }
                break;
        }
    }


    public void initParam() {

        Bluetooth.gethandler(handlerGraph);


        paint = new Paint();

        paint.setColor(Color.WHITE);
        compteur = 0;
        scrollActive = false;

        paint.setStrokeWidth(3);

        paint.setStyle(Paint.Style.STROKE);

        paintPoint = new Paint();


        paintPoint.setColor(Color.WHITE);

        paintPoint.setStrokeWidth(3);

        paintPoint.setStyle(Paint.Style.STROKE);
        paintDraw = new Paint();


        paintDraw.setColor(Color.RED);

        paintDraw.setStrokeWidth(3);

        paintDraw.setTextSize(30);


        paintDraw.setStyle(Paint.Style.STROKE);


        path = new Path();
        listPoint = new LinkedList<Point>();
        pointGraphX = new ArrayList<Point>();
        pointGraphY = new ArrayList<Point>();
        valuePointX = new int[14];

        pathPoint = new Path();
        pathValue = new Path();


        initPointGraph();

        Log.e("init","init param");
    }


    public void initLayout() {
        largeur = preview.getWidth();
        hauteur = preview.getHeight() - 10;
    }

    public void addX() {
        int a = valuePointX[0];
        int b = valuePointX.length;

        for (int i = 0; i < b; i++) {
            valuePointX[i] = valuePointX[i] + 1;
        }
        for (int i = 0; i < listPoint.size()-1; i++) {
            if(listPoint.get(i).x>80 && i+2 < listPoint.size()) {
                listPoint.set(i, new Point(listPoint.get(i + 1).x - 80, listPoint.get(i + 1).y));
                listPoint.set(i+1, new Point(listPoint.get(i + 1).x, listPoint.get(i + 2).y));
            }
        }


    }

    public void initPointGraph() {
        //Display display = getWindowManager().getDefaultDisplay();
        int x = 80;
        int y = 40;


        while (y < hauteur) {
            pointGraphY.add(new Point(x, y));
            y = y + 80;
        }
        Log.e("", "ffff " + hauteur);

        y = (int) pointGraphY.get(pointGraphY.size() - 1).y;
        int g = 0;

        while (x < largeur) {
            pointGraphX.add(new Point(x, y));

            g++;

            x = x + 80;
        }
        valuePointX = new int[g];
        for (int i = 0; i < g; i++) {
            valuePointX[i] = i;
        }

    }


    public void DessinGraphX(Canvas canvas) {

        Paint textPain = new Paint();
        //paint.setTextSize(20);
        textPain.setStrokeWidth(1);
        textPain.setColor(Color.WHITE);

        textPain.setStyle(Paint.Style.FILL);
        path.moveTo(pointGraphX.get(0).x, pointGraphX.get(0).y);

        for (int i = 1; i < pointGraphX.size(); i++) {

            path.lineTo(pointGraphX.get(i).x, pointGraphX.get(i).y);

        }

        canvas.drawPath(path, paint);

    }


    public void dessinPointX(Canvas canvas) {
        Paint textPain = new Paint();
        //paint.setTextSize(20);
        textPain.setStrokeWidth(1);
        textPain.setColor(Color.WHITE);
        textPain.setTextSize(14);
        textPain.setStyle(Paint.Style.FILL);

        for (int i = 1; i < pointGraphX.size(); i++) {
            canvas.drawText(".", pointGraphX.get(i).x, pointGraphX.get(0).y, textPain);

        }
    }

    public void dessinValueX(Canvas canvas) {
        Paint textPain = new Paint();
        //paint.setTextSize(20);
        textPain.setStrokeWidth(1);
        textPain.setColor(Color.WHITE);
        textPain.setTextSize(14);
        textPain.setStyle(Paint.Style.FILL);
        for (int i = 0; i < pointGraphX.size(); i++) {
            Log.e("value", "" + valuePointX[i]);
            canvas.drawText("" + valuePointX[i], pointGraphX.get(i).x, pointGraphX.get(0).y + 20, textPain);

        }

    }


    public void DessinGraphY(Canvas canvas) {

        path.moveTo(pointGraphY.get(0).x, pointGraphY.get(0).y);

        for (int i = 1; i < pointGraphY.size(); i++) {

            path.lineTo(pointGraphY.get(i).x, pointGraphY.get(i).y);

        }
        canvas.drawPath(path, paint);

        for (int i = pointGraphY.size() - 1; i > 0; i--) {
            canvas.drawText("" + (int) (pointGraphY.get(i).x / 80), pointGraphY.get(i).x, pointGraphY.get(i).y, paintPoint);

        }
        paint.setTextSize(20);
        paint.setStrokeWidth(1);
        int j = 0;
        for (int i = pointGraphY.size() - 1; i > 0; i--) {

            canvas.drawText("" + (int) (pointGraphY.get(i).y), 10, pointGraphY.get(j).y, paint);
            j++;
        }
        paint.setStrokeWidth(3);

    for (int i = 1; i < listPoint.size(); i++) {
        pathValue.reset();
        pathValue.moveTo(listPoint.get(i - 1).x, listPoint.get(i - 1).y);
        pathValue.lineTo(listPoint.get(i).x, listPoint.get(i).y);
        pathValue.moveTo(listPoint.get(i).x, listPoint.get(i).y);
pathValue.close();
        canvas.drawPath(pathValue, paintDraw);

    }
    }

    public class GraphViewPerso extends SurfaceView implements SurfaceHolder.Callback, Runnable {


        Thread thread = null;

        public GraphViewPerso(Context context) {
            super(context);
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            thread = new Thread(this);
            thread.start();
            setFocusable(true);
        }


        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            largeur = preview.getWidth();
            hauteur = preview.getHeight() - 10;
        }


        public GraphViewPerso(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            initParam();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

        @Override
        public void run() {
            Canvas canvas = null;
            while (active) {
                if (mHolder.getSurface().isValid()) {
                    canvas = mHolder.lockCanvas();
                    try {
                        Thread.sleep(sleepValue);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    canvas.drawColor(Color.BLACK);
                    DessinGraphY(canvas);
                    DessinGraphX(canvas);
                    dessinPointX(canvas);
                    dessinValueX(canvas);


                    mHolder.unlockCanvasAndPost(canvas);

                }

            }

        }

        public void onPause() {
            active = false;
            // -- Tant que on est en Pause
            while (true) {
                try {
                    thread.join(); // --tente de relancer le Thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            thread = null;
        }

        public void onResume() {

            active = true;
            // -- On peut dessiner, donc on cr�� un Tread pour dessiner !
            thread = new Thread(this); // -- This appele ici la method run() de la
            // class
            thread.start();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        graphPerso.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        graphPerso.onPause();
    }
}
