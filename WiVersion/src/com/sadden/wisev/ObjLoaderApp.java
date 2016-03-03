package com.sadden.wisev;


import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Debug;
import android.telephony.CellLocation;

/**
 * The initial Android Activity, setting and initiating
 * the OpenGL ES Renderer Class @see Lesson02.java
 * 
 * @author Savas Ziplies (nea/INsanityDesign)
 */
public class ObjLoaderApp extends Activity {

	/** The OpenGL View */


	/**
	 * Initiate the OpenGL View and set our own
	 * Renderer (@see Lesson02.java)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	//	Debug.startMethodTracing("calc");
		super.onCreate(savedInstanceState);
		setContentView(new MyRenderer(this));
	}

	/**
	 * Remember to resume the glSurface
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Also pause the glSurface
	 */
	@Override
	protected void onPause() {
		Debug.stopMethodTracing();
		super.onPause();
	}
	@Override
	protected void onDestroy(){
		Debug.stopMethodTracing();
	}

}