package nl.erie.zonterras.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.io.Console;
import java.io.OutputStream;
import android.content.ContentValues;
import android.graphics.Bitmap.CompressFormat;
import android.provider.MediaStore.Images.Media;
import android.widget.Toast;


//public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener, OnTouchListener {
public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

  Button chooseButton;
  ToggleButton drawmodeToggleButton;
  Button saveButton;
  LinearLayout imageviewContainer;
  ZoomableImageView choosenImageView;
  
//  Bitmap bmp;
//  Bitmap alteredBitmap;
//  Canvas canvas;
//  Paint paint;
//  Matrix matrix;
//  float downx = 0;
//  float downy = 0;
//  float upx = 0;
//  float upy = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    chooseButton = (Button) this.findViewById(R.id.ChoosePictureButton);
    drawmodeToggleButton = (ToggleButton) this.findViewById(R.id.toggleDrawmode);
    saveButton = (Button) this.findViewById(R.id.SavePictureButton);
    
    choosenImageView = new ZoomableImageView(this, null);
    imageviewContainer = (LinearLayout) this.findViewById(R.id.imageviewContainer);
    imageviewContainer.addView(choosenImageView);

    chooseButton.setOnClickListener(this);
    drawmodeToggleButton.setOnCheckedChangeListener(this);
    drawmodeToggleButton.setEnabled(false);
    saveButton.setOnClickListener(this);
    saveButton.setEnabled(false);
    
  }

  public void onClick(View v) {

    if (v == chooseButton) {
      Intent choosePictureIntent = new Intent(
          Intent.ACTION_PICK,
          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      startActivityForResult(choosePictureIntent, 0);

      
    } else if (v == saveButton) {

      if (choosenImageView.alteredBitmap != null) {
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(Media.DISPLAY_NAME, "Draw On Me");

        Uri imageFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
          OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
          choosenImageView.alteredBitmap.compress(CompressFormat.JPEG, 90, imageFileOS);
          Toast t = Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT);
          t.show();

        } catch (Exception e) {
          Log.v("EXCEPTION", e.getMessage());
        }
      }
    }
  }
  
  @Override
  public void onCheckedChanged(CompoundButton view, boolean checked) {
    boolean on = ((ToggleButton) view).isChecked();
    if (on) {
        choosenImageView.setOnTouchListener(choosenImageView);
    } else {
        choosenImageView.setOnTouchListener(null);
    }
  }

  protected void onActivityResult(int requestCode, int resultCode,
      Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    if (resultCode == RESULT_OK) {
      Uri imageFileUri = intent.getData();
      choosenImageView.setImageFileUri(imageFileUri);
      // enable buttons
      drawmodeToggleButton.setEnabled(true);
      saveButton.setEnabled(true);

    }
  }  
  

}