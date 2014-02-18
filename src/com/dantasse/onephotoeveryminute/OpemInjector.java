package com.dantasse.onephotoeveryminute;

/**
 * Static methods for dependency injection goodness.
 */
public class OpemInjector {
  
  private static MainActivity view = null;
  public static MainActivity getView() {
    return view;
  }
  public static void setView(MainActivity view) {
    OpemInjector.view = view; 
  }

  private static UiController controller = null;
  public static UiController injectUiController() {
    if (controller == null) {
      controller = new UiController(injectUiModel(), view, injectOpemCamera());
    }
    return controller;
  }
  
  public static UiModel injectUiModel() {
    return UiModel.getInstance();
  }
  
  public static OpemCamera injectOpemCamera() {
    return OpemCamera.getInstance();
  }
  
//  public static FileSaver injectFileSaver() {
//
//  }
}
