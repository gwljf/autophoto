package com.dantasse.onephotoeveryminute;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import upload.UploadUtil;
import upload.UploadUtil.OnUploadProcessListener;

import com.dantasse.onephotoeveryminute.uielement.NumberPicker;
import com.dantasse.onephotoeveryminute.UiModel.State;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.ImageView;
import android.widget.TextView;

/**
 * Main activity for OnePhotoEveryMinute.  Also serves as the View class in this
 * Model-View-Controller app.
 * 
 * @author dantasse
 */
public class MainActivity extends Activity implements OnClickListener,OnUploadProcessListener {

  private UiModel model;
  private UiController controller;
  private Button startButton;
  private Button stopButton;
  private Button upload;
  private TextView text01;
//  private ImageView image01;
  private NumberPicker secondPicker;
  private TextView errorText;
  private TextView outputDirText;
  private static String requestURL = "http://128.205.84.92:8080/fileUpload/p/file!upload";
	/**
	 * 去上传文件	 */
	protected static final int TO_UPLOAD_FILE = 1;  
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2;  //
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;
	/**
	 * 上传初始化	 */
	private static final int UPLOAD_INIT_PROCESS = 4;
	/**
	 * 上传中	 */
	private static final int UPLOAD_IN_PROCESS = 5;
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    // This is a little kludgey; ideally, the injector shouldn't really have
    // state, but it's the one "init" method you have to do, so it's not so bad.
    OpemInjector.setView(this);

    model = OpemInjector.injectUiModel();
    controller = OpemInjector.injectUiController();

    startButton = (Button) findViewById(R.id.StartButton);
    startButton.setOnClickListener(this);
    stopButton = (Button) findViewById(R.id.StopButton);
    stopButton.setOnClickListener(this);
    upload = (Button) findViewById(R.id.upload);
    upload.setOnClickListener(this);
    
    text01 = (TextView) findViewById(R.id.TextView01);
//    image01 = (ImageView) findViewById(R.id.ImageView01);
    secondPicker = (NumberPicker) findViewById(R.id.SecondPicker);
    errorText = (TextView) findViewById(R.id.ErrorText);
    outputDirText = (TextView) findViewById(R.id.OutputDirText);
  }
  
  public SurfaceView getCameraSurface() {
    return (SurfaceView) findViewById(R.id.CameraSurface);
  }

  @Override
  public void onPause() {
    super.onPause();
    controller.tearDown();
  }
  
  @Override
  public void onResume() {
    super.onResume();
    controller.setUp();
  }

  public void onClick(View v) {
    if (v.equals(startButton)) {
      controller.startTakingPhotos();
    } else if (v.equals(stopButton)) {
      controller.stopTakingPhotos();
    }else if(v.equals(upload)){
    	
    }
  }

  public void upload(){
	  String fileKey = "pic";
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File root = Environment.getExternalStorageDirectory();
			File  file = new File(root, "Pictures");
			if (file.exists()){
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for(int i = 0; i < files.length; i++){
					upload.UploadUtil uploadUtil = UploadUtil.getInstance();
					uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态
					uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态 	
    				Map<String, String> params = new HashMap<String, String>();
    				params.put("stepcounter", "11111");
    				params.put("angle", "value");
    				uploadUtil.uploadFile( files[i],fileKey, requestURL,params);
    				files[i].delete();// delete the file after upload
				}

			}
		}	
  }

  public int getDurationSeconds() {
    return secondPicker.getCurrent();
  }
  
  public void update() {
    text01.setText("State: " + model.getCurrentState().toString() + 
        "\nPhotos taken: " + model.getPhotoCount());
//    image01.setImageBitmap(model.getCurrentImage());
    boolean isTakingPhotos = (model.getCurrentState().equals(
        State.TAKING_PHOTOS));
    secondPicker.setEnabled(!isTakingPhotos);
    startButton.setEnabled(!isTakingPhotos);
    stopButton.setEnabled(isTakingPhotos);
    errorText.setText(model.getErrorText());
    outputDirText.setText(model.getOutputDirText());
  }

@Override
public void onUploadDone(int responseCode, String message) {
	// TODO Auto-generated method stub
	Message msg = Message.obtain();
	msg.what = UPLOAD_FILE_DONE;
	msg.arg1 = responseCode;
	msg.obj = message;
	handler.sendMessage(msg);
}

@Override
public void onUploadProcess(int uploadSize) {
	// TODO Auto-generated method stub
	Message msg = Message.obtain();
	msg.what = UPLOAD_IN_PROCESS;
	msg.arg1 = uploadSize;
	handler.sendMessage(msg );
}

@Override
public void initUpload(int fileSize) {
	// TODO Auto-generated method stub
	Message msg = Message.obtain();
	msg.what = UPLOAD_INIT_PROCESS;
	msg.arg1 = fileSize;
	handler.sendMessage(msg );
}
private Handler handler = new Handler(){
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case TO_UPLOAD_FILE:
			upload();
			break;
		
		case UPLOAD_INIT_PROCESS:
//			progressBar.setMax(msg.arg1);
			break;
		case UPLOAD_IN_PROCESS:
//			progressBar.setProgress(msg.arg1);
			break;
		case UPLOAD_FILE_DONE:
//			String result = "响应码："+msg.arg1+"\n响应信息："+msg.obj+"\n耗时："+UploadUtil.getRequestTime()+"秒";
//			uploadImageResult.setText(result);
			break;
		default:
			break;
		}
		super.handleMessage(msg);
	}
	
};
}