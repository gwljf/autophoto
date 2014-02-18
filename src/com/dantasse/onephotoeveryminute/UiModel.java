package com.dantasse.onephotoeveryminute;

import android.graphics.Bitmap;

public class UiModel {

  public enum State {
    NOT_TAKING_PHOTOS,
    TAKING_PHOTOS
  }
  
  private MainActivity view;
  private State currentState = State.NOT_TAKING_PHOTOS;
  private int photoCount = 0;
  private Bitmap currentImage;
  private int durationSeconds = 0;
  private String errorText = "";
  private String outputDirText = "";
  
  private static UiModel instance = null;
  public static UiModel getInstance() {
    if (instance == null) {
      instance = new UiModel(OpemInjector.getView());
    }
    return instance;
  }
  
  public UiModel(MainActivity view) {
    this.view = view;
  }
  
  public void setCurrentState(State newState) {
    this.currentState = newState;
    view.update();
  }
  public State getCurrentState() {
    return currentState;
  }
  
  public void incrementPhotoCount() {
    photoCount++;
    view.update();
  }
  public int getPhotoCount() {
    return photoCount;
  }
  
  public void setCurrentImage(Bitmap newImage) {
    this.currentImage = newImage;
    view.update();
  }
  public Bitmap getCurrentImage() {
    return currentImage;
  }

  /** Set the amount of time, in seconds, between pictures taken. */
  public void setDurationSeconds(int durationSeconds) {
    this.durationSeconds = durationSeconds;
    view.update();
  }
  /** Get the amount of time, in seconds, between pictures taken. */
  public int getDurationSeconds() {
    return durationSeconds;
  }

  public void setErrorText(String errorText) {
    this.errorText = errorText;
    view.update();
  }

  public String getErrorText() {
    return errorText;
  }

  public void setOutputDirText(String outputDirText) {
    this.outputDirText = outputDirText;
    view.update();
  }

  public String getOutputDirText() {
    return outputDirText;
  }
}
