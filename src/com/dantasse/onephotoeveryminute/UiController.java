package com.dantasse.onephotoeveryminute;

import com.dantasse.onephotoeveryminute.UiModel.State;

//import android.graphics.Bitmap;
import android.os.Handler;

public class UiController {

  // new Handler() grabs the current thread.
  private Handler handler = new Handler();
  private OpemCamera camera;
  private UiModel model;
  private MainActivity view;  
  
  private Runnable takePhotoTask = new Runnable() {
    public void run() {
      camera.takePhoto();
      model.incrementPhotoCount();
      handler.postDelayed(this, model.getDurationSeconds() * 1000);
    }
  };
  
  public UiController(UiModel model, MainActivity view, OpemCamera camera) {
    this.model = model;
    this.view = view;
    this.camera = camera;
  }
  
  /** Called on resume, needed because the camera is released on pause. */
  public void setUp() {
    camera.setUp();
  }
  
  public void tearDown() {
    stopTakingPhotos();
    camera.tearDown();
  }

  public void startTakingPhotos() {
    int durationSeconds = view.getDurationSeconds();
    if (durationSeconds <= 0) {
      model.setErrorText("There must be at least 1 second between pictures.");
      return;
    }
    model.setDurationSeconds(durationSeconds);
    model.setCurrentState(UiModel.State.TAKING_PHOTOS);
    handler.removeCallbacks(takePhotoTask);
    handler.post(takePhotoTask);
  }

  public void stopTakingPhotos() {
    model.setCurrentState(UiModel.State.NOT_TAKING_PHOTOS);
    handler.removeCallbacks(takePhotoTask);
  }

//  public void displayImage(Bitmap image) {
//    model.setCurrentImage(image);
//  }
  
  public void displayError(String errorText) {
    model.setErrorText(errorText);
    model.setCurrentState(State.NOT_TAKING_PHOTOS);
  }

  public void setOutputDirText(String outputDirText) {
    model.setOutputDirText(outputDirText);
  }
}
