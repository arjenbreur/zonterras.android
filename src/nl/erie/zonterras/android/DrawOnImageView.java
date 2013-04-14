package nl.erie.zonterras.android;

import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnTouchListener;

public class DrawOnImageView extends ImageView implements OnTouchListener {

    private static final int INVALID_POINTER_ID = -1;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastGestureX;
    private float mLastGestureY;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private boolean drawMode;
    
    Bitmap bmp;
    Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;
    
    Context context;


    
    public DrawOnImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public DrawOnImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }
    
    public float getScaleFactor(){
    	return mScaleFactor;
    }
    
    public void setDrawmode(boolean mode){
		this.drawMode = mode;
    	if(mode==true){
            this.setOnTouchListener(this);
    	}else{
            this.setOnTouchListener(null);
    	}
    }

    public void openImage(Uri imageFileUri){
        try {
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            this.bmp = BitmapFactory.decodeStream(this.context.getContentResolver().openInputStream(
                    imageFileUri), null, bmpFactoryOptions);

            bmpFactoryOptions.inJustDecodeBounds = false;
            this.bmp = BitmapFactory.decodeStream(this.context.getContentResolver().openInputStream(
                    imageFileUri), null, bmpFactoryOptions);

            this.alteredBitmap = Bitmap.createBitmap(
            		this.bmp.getWidth(), 
            		this.bmp.getHeight(), 
            		this.bmp.getConfig()
            );
            this.canvas = new Canvas(this.alteredBitmap);
            this.paint = new Paint();
            this.paint.setColor(Color.GREEN);
            this.paint.setStrokeWidth(5);
            this.matrix = new Matrix();
            this.canvas.drawBitmap(this.bmp, this.matrix, this.paint);

            this.setImageBitmap(this.alteredBitmap);
            

          } catch (Exception e) {
            Log.v("ERROR", e.toString());
          }
    }
    
    public void saveImage(Uri imageFileUri){
		try {
		    OutputStream imageFileOS = this.context.getContentResolver().openOutputStream(imageFileUri);
		    this.alteredBitmap.compress(CompressFormat.JPEG, 90, imageFileOS);
		    Toast t = Toast.makeText(this.context, "Saved!", Toast.LENGTH_SHORT);
		    t.show();
		} catch (Exception e) {
		    Log.v("EXCEPTION", e.getMessage());
		}
    }
    
    
    
    
    
    
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScaleDetector.isInProgress()) {
                    final float x = ev.getX();
                    final float y = ev.getY();

                    mLastTouchX = x;
                    mLastTouchY = y;
                    mActivePointerId = ev.getPointerId(0);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_1_DOWN: {
                if (mScaleDetector.isInProgress()) {
                    final float gx = mScaleDetector.getFocusX();
                    final float gy = mScaleDetector.getFocusY();
                    mLastGestureX = gx;
                    mLastGestureY = gy;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                    final float x = ev.getX(pointerIndex);
                    final float y = ev.getY(pointerIndex);

                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    invalidate();

                    mLastTouchX = x;
                    mLastTouchY = y;
                }
                else{
                    final float gx = mScaleDetector.getFocusX();
                    final float gy = mScaleDetector.getFocusY();

                    final float gdx = gx - mLastGestureX;
                    final float gdy = gy - mLastGestureY;

                    mPosX += gdx;
                    mPosY += gdy;

                    invalidate();

                    mLastGestureX = gx;
                    mLastGestureY = gy;
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                else{
                    final int tempPointerIndex = ev.findPointerIndex(mActivePointerId);
                    mLastTouchX = ev.getX(tempPointerIndex);
                    mLastTouchY = ev.getY(tempPointerIndex);
                }

                break;
            }
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        
        canvas.save();
        canvas.translate(mPosX, mPosY);

        if (mScaleDetector.isInProgress()) {
            canvas.scale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
        }
        else{
            canvas.scale(mScaleFactor, mScaleFactor, mLastGestureX, mLastGestureY);
        }
        super.onDraw(canvas);
        canvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            invalidate();
            return true;
        }
    }
    
    public boolean onTouch(View v, MotionEvent event) {
	    int action = event.getAction();
	    canvas.save();
	    canvas.scale(this.getScaleFactor(),this.getScaleFactor());
	    canvas.translate(100, 100);
	    switch (action) {
		    case MotionEvent.ACTION_DOWN:
		      downx = event.getX();
		      downy = event.getY();
		      break;
		    case MotionEvent.ACTION_MOVE:
		      upx = event.getX();
		      upy = event.getY();
		      
		      canvas.drawLine(downx, downy, upx, upy, paint);
		      this.invalidate();
		      downx = upx;
		      downy = upy;
		      break;
		    case MotionEvent.ACTION_UP:
		      upx = event.getX();
		      upy = event.getY();
		      canvas.drawLine(downx, downy, upx, upy, paint);
		      this.invalidate();
		      break;
		    case MotionEvent.ACTION_CANCEL:
		      break;
		    default:
		      break;
	    }
	    canvas.restore();
	  return true;
  }
}