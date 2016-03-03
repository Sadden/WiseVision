package com.sadden.wisev;

import java.util.Arrays;
import java.util.Calendar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;




import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Debug;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener,
		CvCameraViewListener2 {

	Switch IsAccelerate;
	boolean isA;
	boolean isFirstTime;
	Button openObj;
	TextView textview_x;
	TextView textview_y;
	TextView textview_z;
	SensorManager sensorManager;
	Sensor sensor;
	Calendar calendar;
	float px;
	float py;
	float pz;
	MyRenderer render;

	// ***********************************//
	// opencv>>>
	private static final String TAG = "OCVSample::Activity";

	private CameraBridgeViewBase mOpenCvCameraView;
	private boolean mIsJavaCamera = true;
	private MenuItem mItemSwitchCamera = null;
	private Mat mRgba;
	private Mat mGray;
	private Mat mTmp;

	private Size mSize0;
	private Mat mIntermediateMat;
	private MatOfInt mChannels[];
	private MatOfInt mHistSize;
	private int mHistSizeNum = 25;
	private Mat mMat0;
	private float[] mBuff;
	private MatOfFloat mRanges;
	private Point mP1;
	private Point mP2;
	private Scalar mColorsRGB[];
	private Scalar mColorsHue[];
	private Scalar mWhilte;
	private Mat mSepiaKernel;
	private Button mBtn = null;
	private int mProcessMethod = 0;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public MainActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Debug.startMethodTracing("calc");

		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
		setContentView(R.layout.activity_main);

		//*******************opencv******************
		if (mIsJavaCamera)  
            mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);  
        else  
            mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);  
  
  
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);  
          
        mOpenCvCameraView.setCvCameraViewListener(this);  
        mBtn = (Button) findViewById(R.id.Button_altopencv);  
        mBtn.setOnClickListener(new View.OnClickListener(){  
         @Override    
            public void onClick(View v) {  
          mProcessMethod++;  
          if(mProcessMethod>8) mProcessMethod=0;  
            }    
        });  
		
		
		
		
		
		
		
		
		isFirstTime = true;
		px = py = pz = 100;
		IsAccelerate = (Switch) findViewById(R.id.Switch1);
		textview_x = (TextView) findViewById(R.id.Text_x);
		textview_y = (TextView) findViewById(R.id.Text_y);
		textview_z = (TextView) findViewById(R.id.Text_z);
		textview_x.setVisibility(View.INVISIBLE);
		textview_y.setVisibility(View.INVISIBLE);
		textview_z.setVisibility(View.INVISIBLE);
		isA = false;
		openObj = (Button) findViewById(R.id.Button1);
		FrameLayout frame1 = (FrameLayout) findViewById(R.id.FrameLayout1);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		Log.i("mainA", "before new");
		render = new MyRenderer(this);
		Log.i("mainA", "after new");

		if (sensorManager == null) {
			Log.i("fucker", "deveice not support SensorManager");
		}
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);

		IsAccelerate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					isA = true;
					textview_x.setVisibility(View.VISIBLE);
					textview_y.setVisibility(View.VISIBLE);
					textview_z.setVisibility(View.VISIBLE);
				} else {
					isA = false;
					textview_x.setVisibility(View.INVISIBLE);
					textview_y.setVisibility(View.INVISIBLE);
					textview_z.setVisibility(View.INVISIBLE);
				}
			}
		});

		openObj.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isFirstTime) {
					RelativeLayout relative1 = (RelativeLayout) findViewById(R.id.relativelayout2);

					relative1.addView(render);
					isFirstTime = false;
				} else {
					render.setVisibility(View.INVISIBLE);
					isFirstTime = true;
					Toast.makeText(MainActivity.this,
							"why you click twice? it will shut down",
							Toast.LENGTH_LONG).show();
				}

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor == null) {
			return;
		}

		float dx, dy, dz;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = (float) event.values[0];
			float y = (float) event.values[1];
			float z = (float) event.values[2];

			if (px > 90 || pz > 90 || py > 90) {
				px = x;
				pz = z;
				py = y;
			}

			dx = x - px;
			dy = y - py;
			dz = z - pz;

			px = x;
			py = y;
			pz = z;

			textview_x.setText("x axe acceleration=" + String.valueOf(x));
			textview_y.setText("y axe acceleration=" + String.valueOf(y));
			textview_z.setText("z axe acceleration=" + String.valueOf(z));

			if (isA) {

				render.Changez(dz);

			}

		}
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub

		 mRgba = new Mat(height, width, CvType.CV_8UC4);  
	     mGray = new Mat(height, width, CvType.CV_8UC1);  
	     mTmp = new Mat(height, width, CvType.CV_8UC4);  
	       
	      mIntermediateMat = new Mat();  
	         mSize0 = new Size();  
	         mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };  
	         mBuff = new float[mHistSizeNum];  
	         mHistSize = new MatOfInt(mHistSizeNum);  
	         mRanges = new MatOfFloat(0f, 256f);  
	         mMat0 = new Mat();  
	         mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };  
	         mColorsHue = new Scalar[] {  
	                 new Scalar(255, 0, 0, 255), new Scalar(255, 60, 0, 255), new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),  
	                 new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255), new Scalar(20, 255, 0, 255), new Scalar(0, 255, 30, 255),  
	                 new Scalar(0, 255, 85, 255), new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),  
	                 new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255), new Scalar(0, 0, 255, 255), new Scalar(64, 0, 255, 255), new Scalar(120, 0, 255, 255),  
	                 new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255), new Scalar(255, 0, 0, 255)  
	         };  
	         mWhilte = Scalar.all(255);  
	         mP1 = new Point();  
	         mP2 = new Point();  
	  
	         // Fill sepia kernel  
	         mSepiaKernel = new Mat(4, 4, CvType.CV_32F);  
	         mSepiaKernel.put(0, 0, /* R */0.189f, 0.769f, 0.393f, 0f);  
	         mSepiaKernel.put(1, 0, /* G */0.168f, 0.686f, 0.349f, 0f);  
	         mSepiaKernel.put(2, 0, /* B */0.131f, 0.534f, 0.272f, 0f);  
	         mSepiaKernel.put(3, 0, /* A */0.000f, 0.000f, 0.000f, 1f);  
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

		 mRgba.release();  
	     mGray.release();  
	     mTmp.release();  
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {  
	       
	     mRgba = inputFrame.rgba();  
	     Size sizeRgba = mRgba.size();  
	     int rows = (int) sizeRgba.height;  
	        int cols = (int) sizeRgba.width;  
	        Mat rgbaInnerWindow;  
	          
	        int left = cols / 8;  
	        int top = rows / 8;  
	  
	        int width = cols * 3 / 4;  
	        int height = rows * 3 / 4;  
	        //»Ò¶ÈÍ¼  
	     if(mProcessMethod==1)  
	      Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);  
	     //Canny±ßÔµ¼ì²â  
	     else if(mProcessMethod==2)  
	     {  
	      mRgba = inputFrame.rgba();  
	      Imgproc.Canny(inputFrame.gray(), mTmp, 80, 100);  
	      Imgproc.cvtColor(mTmp, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);  
	     }  
	     //Hist  
	     else if(mProcessMethod==3)  
	     {  
	       Mat hist = new Mat();  
	             int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);  
	             if(thikness > 5) thikness = 5;  
	             int offset = (int) ((sizeRgba.width - (5*mHistSizeNum + 4*10)*thikness)/2);  
	              
	   // RGB  
	             for(int c=0; c<3; c++) {  
	                 Imgproc.calcHist(Arrays.asList(mRgba), mChannels[c], mMat0, hist, mHistSize, mRanges);  
	                 Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);  
	                 hist.get(0, 0, mBuff);  
	                 for(int h=0; h<mHistSizeNum; h++) {  
	                     mP1.x = mP2.x = offset + (c * (mHistSizeNum + 10) + h) * thikness;  
	                     mP1.y = sizeRgba.height-1;  
	                     mP2.y = mP1.y - 2 - (int)mBuff[h];  
	                     Core.line(mRgba, mP1, mP2, mColorsRGB[c], thikness);  
	                 }  
	             }  
	             // Value and Hue  
	             Imgproc.cvtColor(mRgba, mTmp, Imgproc.COLOR_RGB2HSV_FULL);  
	             // Value  
	             Imgproc.calcHist(Arrays.asList(mTmp), mChannels[2], mMat0, hist, mHistSize, mRanges);  
	             Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);  
	             hist.get(0, 0, mBuff);  
	             for(int h=0; h<mHistSizeNum; h++) {  
	                 mP1.x = mP2.x = offset + (3 * (mHistSizeNum + 10) + h) * thikness;  
	                 mP1.y = sizeRgba.height-1;  
	                 mP2.y = mP1.y - 2 - (int)mBuff[h];  
	                 Core.line(mRgba, mP1, mP2, mWhilte, thikness);  
	             }  
	     }  
	     //inner Window Sobel  
	     else if(mProcessMethod==4)  
	     {  
	      Mat gray = inputFrame.gray();  
	            Mat grayInnerWindow = gray.submat(top, top + height, left, left + width);  
	            rgbaInnerWindow = mRgba.submat(top, top + height, left, left + width);  
	            Imgproc.Sobel(grayInnerWindow, mIntermediateMat, CvType.CV_8U, 1, 1);  
	            Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 10, 0);  
	            Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);  
	            grayInnerWindow.release();  
	            rgbaInnerWindow.release();  
	     }  
	     //SEPIA  
	     else if(mProcessMethod==5)  
	     {  
	      rgbaInnerWindow = mRgba.submat(top, top + height, left, left + width);  
	            Core.transform(rgbaInnerWindow, rgbaInnerWindow, mSepiaKernel);  
	            rgbaInnerWindow.release();  
	     }  
	     //ZOOM  
	     else if(mProcessMethod==6)  
	     {  
	      Mat zoomCorner = mRgba.submat(0, rows / 2 - rows / 10, 0, cols / 2 - cols / 10);  
	            Mat mZoomWindow = mRgba.submat(rows / 2 - 9 * rows / 100, rows / 2 + 9 * rows / 100, cols / 2 - 9 * cols / 100, cols / 2 + 9 * cols / 100);  
	            Imgproc.resize(mZoomWindow, zoomCorner, zoomCorner.size());  
	            Size wsize = mZoomWindow.size();  
	            Core.rectangle(mZoomWindow, new Point(1, 1), new Point(wsize.width - 2, wsize.height - 2), new Scalar(255, 0, 0, 255), 2);  
	            zoomCorner.release();  
	            mZoomWindow.release();  
	     }  
	     //PIXELIZE  
	     else if(mProcessMethod==7)  
	     {  
	      rgbaInnerWindow = mRgba.submat(top, top + height, left, left + width);  
	            Imgproc.resize(rgbaInnerWindow, mIntermediateMat, mSize0, 0.1, 0.1, Imgproc.INTER_NEAREST);  
	            Imgproc.resize(mIntermediateMat, rgbaInnerWindow, rgbaInnerWindow.size(), 0., 0., Imgproc.INTER_NEAREST);  
	            rgbaInnerWindow.release();  
	     }  
	     //POSTERIZE  
	     else if(mProcessMethod==8)  
	     {  
	      rgbaInnerWindow = mRgba.submat(top, top + height, left, left + width);  
	            Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);  
	            rgbaInnerWindow.setTo(new Scalar(0, 0, 0, 255), mIntermediateMat);  
	            Core.convertScaleAbs(rgbaInnerWindow, mIntermediateMat, 1./16, 0);  
	            Core.convertScaleAbs(mIntermediateMat, rgbaInnerWindow, 16, 0);  
	            rgbaInnerWindow.release();  
	     }  
	     else  
	      mRgba = inputFrame.rgba();  
	     return mRgba;  
	    }
	
	@Override  
    public void onPause()  
    {  
        super.onPause();  
        if (mOpenCvCameraView != null)  
            mOpenCvCameraView.disableView();  
    }  
  
    @Override  
    public void onResume()  
    {  
        super.onResume();  
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);  
    }  
  
    public void onDestroy() {  
        super.onDestroy();  
        if (mOpenCvCameraView != null)  
            mOpenCvCameraView.disableView();  
    }  
	

}
