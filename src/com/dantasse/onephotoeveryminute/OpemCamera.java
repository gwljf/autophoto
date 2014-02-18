package com.dantasse.onephotoeveryminute;

import java.io.IOException;
import java.text.NumberFormat;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OpemCamera implements SurfaceHolder.Callback {

  private Camera camera = null;
  private SurfaceView surfaceView = null;
  private FileSaver fileSaver = null;
  private NumberFormat numberFormat = NumberFormat.getIntegerInstance();
  private boolean previewRunning = false;

  private int counter = 0;
  // Called when the picture's jpeg data is available.
  private PictureCallback jpegCallback = new PictureCallback() {
    public void onPictureTaken(byte[] data, Camera callbackCamera) {
      numberFormat.setMinimumIntegerDigits(3);
      fileSaver.save(data, numberFormat.format(counter) + ".jpg");
      counter++;
    }
  };

  /**
   * There should only be one OpemCamera.
   */
  private static OpemCamera instance = null;
  public static OpemCamera getInstance() {
    if (instance == null) {
      instance = new OpemCamera(OpemInjector.getView().getCameraSurface(),
          new FileSaver());
    }
    return instance;
  }

  OpemCamera(SurfaceView surfaceView, FileSaver fileSaver) {
    this.surfaceView = surfaceView;
    this.surfaceView.getHolder().addCallback(this);
    this.surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    this.fileSaver = fileSaver;
  }

  /** Called on app resume, needed because the camera is released on pause. */
  public void setUp() {
    if (fileSaver == null) {
      fileSaver = new FileSaver();
    }
  }

  /** Called on pause. */
  public void tearDown() {
    fileSaver = null;
  }

  public void takePhoto() {
    camera.takePicture(null, null, jpegCallback);
  }

  public void surfaceCreated(SurfaceHolder holder) {
    camera = Camera.open();
    try {
      camera.setPreviewDisplay(holder);
    } catch (IOException exception) {
      camera.release();
      camera = null;
      // TODO: add more exception handling logic here
    }
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width,
      int height) {
    if (previewRunning) {
      camera.stopPreview();
    }

    Camera.Parameters params = camera.getParameters();
    // oh my god I don't care about the preview, it can be whatever size
    Size previewSize = params.getSupportedPreviewSizes().get(0);
    params.setPreviewSize(previewSize.width, previewSize.height);
    int previewFormat = params.getSupportedPreviewFormats().get(0);
    params.setPreviewFormat(previewFormat);
    int previewFrameRate = params.getSupportedPreviewFrameRates().get(0);
    params.setPreviewFrameRate(previewFrameRate);
    // TODO(dantasse) add flash/focus/something?
    camera.setParameters(params);

    camera.startPreview();
    previewRunning = true;
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    camera.stopPreview();
    previewRunning = false;
    camera.release();
    camera = null;
  }
}
