package com.dantasse.onephotoeveryminute;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


/**
 * Handles saving pictures to the SD card.  The OpemCamera class owns this;
 * when the OpemCamera is destroyed, this FileSaver is also destroyed.
 */
public class FileSaver {
  
  private static SimpleDateFormat formatter = 
    new SimpleDateFormat("yyyyMMdd'-'hhmmss");

  /** The directory where all the files will be saved to */
  public File directory;
  public UiController controller;

  private static FileSaver instance = null;
  public static FileSaver getInstance() {
    if (instance == null) {
      instance = new FileSaver();
    }
    return instance;
  }

  public FileSaver() {
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      // Save things in /sdcard/Pictures, as per 
      // http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
      File root = Environment.getExternalStorageDirectory();
      File picturesDir = new File(root, "Pictures");
/*      File thisTimeDir = new File(picturesDir, formatter.format(new Date()));
      thisTimeDir.mkdirs();
      this.directory = thisTimeDir;*/
      picturesDir.mkdirs();
      this.directory = picturesDir;
    } else {
      // can't find the SD card, should error or something.
    }
  }

  // you have to call this after creating a FileSaver so there's no circular
  // dependency issue. ugh.
  private void setUiController(UiController controller) {
    this.controller = controller;
  }

  /** Saves the data to the file in |directory| */
  public void save(byte[] data, String filename) {

    // ugh ugh ugh.
    setUiController(OpemInjector.injectUiController());
    
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {

      File destFile = new File(directory, filename);
      try {
        destFile.createNewFile();
        BufferedOutputStream outputStream = new BufferedOutputStream(
            new FileOutputStream(destFile));
        outputStream.write(data);
        outputStream.close();
        controller.setOutputDirText("Saved to: " + destFile.getCanonicalPath());
      } catch (IOException e) {
        controller.stopTakingPhotos();
        controller.displayError("Sorry, there was an error saving a picture. " +
        		"Maybe the SD card is full? Or not actually in the device?");
      }
    } else {
      controller.stopTakingPhotos();
      controller.displayError("Can't find the SD card.  You must insert an SD" +
      		" card to save pictures.");
    }
  }
}
