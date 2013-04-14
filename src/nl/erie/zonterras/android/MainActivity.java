package nl.erie.zonterras.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import android.content.ContentValues;
import android.provider.MediaStore.Images.Media;


public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

  Button chooseButton;
  ToggleButton drawmodeToggleButton;
  Button saveButton;
  LinearLayout imageviewContainer;
  DrawOnImageView drawOnImageView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    chooseButton = (Button) this.findViewById(R.id.ChoosePictureButton);
    drawmodeToggleButton = (ToggleButton) this.findViewById(R.id.toggleDrawmode);
    saveButton = (Button) this.findViewById(R.id.SavePictureButton);
    
    drawOnImageView = new DrawOnImageView(this, null);
    imageviewContainer = (LinearLayout) this.findViewById(R.id.imageviewContainer);
    imageviewContainer.addView(drawOnImageView);

    // set listeners
    chooseButton.setOnClickListener(this);
    drawmodeToggleButton.setOnCheckedChangeListener(this);
    saveButton.setOnClickListener(this);

    // disable buttons
    drawmodeToggleButton.setEnabled(false);
    saveButton.setEnabled(false);
    
  }

  public void onClick(View v) {
    if (v == chooseButton) {
      Intent choosePictureIntent = new Intent(
          Intent.ACTION_PICK,
          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
      );
      startActivityForResult(choosePictureIntent, 0);
    } else if (v == saveButton) {
      if (drawOnImageView.alteredBitmap != null) {
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(Media.DISPLAY_NAME, "Draw On Me");
        Uri imageFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, contentValues);
        drawOnImageView.saveImage(imageFileUri);
      }
    }
  }
  
  @Override
  public void onCheckedChanged(CompoundButton view, boolean checked) {
    boolean onoff = ((ToggleButton) view).isChecked();
	drawOnImageView.setDrawmode(onoff);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (resultCode == RESULT_OK) {
      Uri imageFileUri = intent.getData();
      drawOnImageView.openImage(imageFileUri);
      // enable buttons
      drawmodeToggleButton.setEnabled(true);
      saveButton.setEnabled(true);
    }
  }  
  

}