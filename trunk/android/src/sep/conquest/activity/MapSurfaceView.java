package sep.conquest.activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

	/**
	 * This rectangle is static and always on the same size as the actual drawn 
	 * surface view. This is an advantage for the autoScale method, which only
	 * can draw within the rectangle and avoids afresh and defective paintings
	 * on the edge. If you do a touch on the display and drag the surface view, 
	 * you actual really move the rectangle on which the new position is
	 * calculated.
	 */
	private static Rect BOUNDS = new Rect(0, 0, 1000, 1000);
	
	/**
	 * This LinkedList saves the id of the roboters and there x and y 
	 * coordinates on the map. This information must be available within the
	 * surface view class to allocate the direct roboter selection by long 
	 * touching them on the map.
	 * 
	 */
	private LinkedList<ePuckPosition> positions;
	
	/**
	 * Makes the DrawThread, which is essential to draw the surface view visible 
	 * and accessable for all methods. This is required to react on the 
	 * different states of the surface view, e.g. when a method sets the thread
	 * paused.
	 */
	private DrawThread thread = null;
	
	/**
	 * Is required to save the coordinates of the touch point adequate 
	 * for the scroll function.
	 */
	private PointF downTouchPoint = null;
	
	/**
	 * Contains the adjustment value of the surface view relative to the
	 * previousOffset values and adds the downTouchPoint to show the map
	 * on the right position.
	 */
	private float currentOffsetX = 0;
	private float currentOffsetY = 0;
	
	/**
	 * Contains the actual offset which is necessary for the scroll function.
	 */
	private float previousOffsetX = 0;
	private float previousOffsetY = 0;
	
	/**
	 * All draw methods use this value to scale the size of their drawable 
	 * object. This value is constantly reduced in autoScaling mode to ensure
	 * an ideal display design.
	 */
	private int scaleValue = 80;
	
	/**
	 * This value is initially set to false and is activated when scallValue
	 * under-runs a threshold and the autoscaling method is replaced by the
	 * scroll function.
	 */
	private boolean scrollAble = false;
	
	/**
	 * Presents the values of the screen size depending on the used smartphone.
	 * This values are set by the setDimensions method.
	 */
	private int displayX = Map.x;
	private int displayY = Map.y - 50;
	
	/**
	 * The constructor resgisters the interface SurfaceHolder.Callback on the
	 * surface view, sets the onTouchListener on the view and creates a new 
	 * thread object.
	 * 
	 * @param context Global information on application environment.
	 * @param attrs A collection of attributes, as found associated with a tag 
	 * 			    in an XML document. 
	 */
	public MapSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		getHolder().addCallback(this);
		setOnTouchListener(this);
		
		thread = new DrawThread();
	}
	
	/**
	 * Must be implemented by the interface SurfaceHolder.Callback, but isn't
	 * used here.
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// Do Nothing
	}

	/**
	 * Is implemented by the interface surfaceHolder.Callback and is called 
	 * when a new surface view is created. It creates an instance of DrawThread
	 * and starts the thread.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new DrawThread();
		thread.start();
		
	}

	/**
	 * Is implemented by the interface surfaceHolder.Callback and is called 
	 * when the actual view is exited. This could also be caused by an intent,
	 * so the thread has to be paused and waits until the end of other threads
	 * to be continued.
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setPaused(true);
        
        while (retry) {
            try {
            	thread.join();
                retry = false;
            } catch (InterruptedException e) {
            	
            }
        }
		
	}

	/**
	 * Is implemented by the interface View.OnTouchListener and is called by
	 * every single touch on the display. If the autoScalling method has already
	 * set the attribute scrollAble to true, it is possible to drag the map 
	 * on the display in the desired position. Therefore the values 
	 * currentOffset, previousOffset and downTouchPoint are used. Another 
	 * function of this method is the selection of a particular e-puck 
	 * directly out of the map. For this reason the time is stopped and if
	 * it exceeds over a special time threshold this is e-puck is selected 
	 * and shown in the spinner menu of the map class. The time measurement is
	 * reasonable to differentiate between e-puck selection and the scroll 
	 * function.  
	 */
	public boolean onTouch(View v, MotionEvent event) {
		if (scrollAble) {
			long touchDuration = 0;
			long touchTime = 0;

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// Track starting point
				downTouchPoint = new PointF(event.getX(), event.getY());
				touchTime = System.currentTimeMillis();

			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				touchDuration = System.currentTimeMillis() - touchTime;

				if (touchDuration > 800) {
					//wie rausfinden ob das geht?!
					//vllt im thread eine neue methode erstellen 
					autoScaling(35);
				} else if (scrollAble) {
					// Clear starting point
					downTouchPoint = null;
					previousOffsetX = currentOffsetX;
					previousOffsetY = currentOffsetY;
				}

			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (scrollAble) {
					currentOffsetX = previousOffsetX
							+ ((event.getX() - downTouchPoint.x));
					currentOffsetY = previousOffsetY
							+ ((event.getY() - downTouchPoint.y));

					if (currentOffsetX < -(BOUNDS.width() - getWidth())) {
						currentOffsetX = -(BOUNDS.width() - getWidth());
					} else if (currentOffsetX > 0) {
						currentOffsetX = 0;
					}

					if (currentOffsetY < -(BOUNDS.height() - getHeight())) {
						currentOffsetY = -(BOUNDS.height() - getHeight());
					} else if (currentOffsetY > 0) {
						currentOffsetY = 0;
					}
				}
			}
		}
		return true;

	}
	
	/**
	 * The inner class extends from Thread and implements the interface 
	 * Observer. It is responsible for drawing on the surface view and contains
	 * suitable methods. The data structure of the map is received by the 
	 * update method and forwarded to the draw method.
	 * 
	 * @author Florian Bürchner
	 *
	 */
	private class DrawThread extends Thread implements Observer {
		
		private boolean paused = false;
		private Paint paint = new Paint();
		
		
		@Override
		public void run() {
			while(!paused){
				
			}
		}
		
		/**
		 * Main Drawing Method
		 */
		private void doDraw(LinkedList map){
			//hier noch Attribute anlegen um die Größe des SurfaceViews festzulegen
			//sowas wie getMinY, getMaxY, getMinX, getMaxX
			//und ein Offset Attribut um negative Werte umzurechnen
			
			if (!scrollAble) {
				autoScaling(60);
			}
			
			Canvas c = getHolder().lockCanvas();
			
			// Translate the drawing matrix to the current drag offset
			Matrix m = new Matrix();
			m.setTranslate(currentOffsetX, currentOffsetY);
			c.setMatrix(m);
			
			// Sample drawing
			paint.setColor(0xff000000);
			c.drawRect(BOUNDS, paint);
			
			//draw shapes and visited rectangles
			paint.setColor(0xff00ff00);

			Iterator mapItr = map.iterator();
			while (mapItr.hasNext()) 
			{
				/**
				//drawVisited(c, paint, no.getXvalue(), no.getYvalue(), no.getVisited());
				Status st = no.getState();
				switch(st) {
				case BottomLeftEdge: drawBottomLeftEdge(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				case BottomT: drawBottomT(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				case Cross: drawCross(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				case BottomRightEdge: drawBottomRightEdge(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				case LeftT: drawLeftT(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				case RightT: drawRightT(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				case TopLeftEdge: drawTopLeftEdge(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				case TopRightEdge: drawTopRightEdge(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				case TopT: drawTopT(c, paint, no.getXvalue()*scaleValue, no.getYvalue()*scaleValue); break;
				}
				*/
			}
			
			
			//drawEpuck(c, paint, 140, 140);
			//drawVisited(c, paint, 140, 140);
			getHolder().unlockCanvasAndPost(c);
		}

		public void setPaused(boolean paused) {
			this.paused = paused;
		}

		public void update(Observable obs, Object map) {
			//dodraw aufrufen
			//anschließend drawEpucks aufrufen
			//eine liste für knoten und eine liste für epucks 
			//hier kommt die ganze karte rein
			
		}
		
		public void drawCross(Canvas c, int x, int y) {
			c.drawLine(x, y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
			c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + scaleValue, paint);
		}
		
		public void drawTopLeftEdge(Canvas c, int x, int y) {
			c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
			c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + (scaleValue/2), y + scaleValue, paint);
		}
		
		public void drawTopRightEdge(Canvas c, int x, int y) {
			c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
			c.drawLine(x + scaleValue, y + (scaleValue/2), x + scaleValue, y + scaleValue, paint);
		}
		
		public void drawBottomRightEdge(Canvas c, int x, int y) {
			c.drawLine(x + (scaleValue/2), y + scaleValue, x + scaleValue, y + scaleValue, paint);
			c.drawLine(x + scaleValue, y + (scaleValue/2), x + scaleValue, y + scaleValue, paint);
		}
		
		public void drawBottomLeftEdge(Canvas c, int x, int y) {
			c.drawLine(x, y + (scaleValue/2), x, y + scaleValue, paint);
			c.drawLine(x, y + scaleValue, x + (scaleValue/2), y + scaleValue, paint);
		}
		
		public void drawTopT(Canvas c, int x, int y) {
			c.drawLine(x, y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
			c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + (scaleValue/2), y + (scaleValue), paint);
		}
		
		public void drawRightT(Canvas c, int x, int y) {
			c.drawLine(x + scaleValue, y, x + scaleValue, y + scaleValue, paint);
			c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
		}
		
		public void drawBottomT(Canvas c, int x, int y) {
			c.drawLine(x, y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
			c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + (scaleValue/2), paint);
		}
		
		public void drawLeftT(Canvas c, int x, int y) {
			c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + scaleValue, paint);
			c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
		}
		
		public void drawVisited(Canvas c, int x, int y, int visitedCounter) {
			//switch color by visited index
			switch(visitedCounter) {
				case 0: break;
				case 1: break;
				case 2: break;
				case 3: break;
				default: paint.setColor(0xffff0000);
			}
			c.drawRect(new Rect(x, y, x + scaleValue, y + scaleValue), paint);
		}
		
		public void drawEpuck(Canvas c, int x, int y) {
			paint.setColor(0xffcccccc);
			c.drawCircle(x, y, (scaleValue/2), paint);
			paint.setColor(0xff0000ff);
			c.drawCircle(x, y, (scaleValue/4), paint);
		}
		
		public void selectEpuck(Canvas c, int x, int y) {
			
		}
		
	}
	
	/**
	 * 
	 * @param maxFieldSize
	 */
	public void autoScaling(int maxFieldSize) {
		int surfaceSize = maxFieldSize *scaleValue;
		//hier muss noch implementiert werden wenn die kästchen displayY übersteigen bevor sie displayX übersteigen
		if (surfaceSize > displayX) {
			scaleValue = displayX/maxFieldSize;
			if (scaleValue < 20) {
				scaleValue = 20;
				if (maxFieldSize*scaleValue < displayY) {
					BOUNDS.set(0, 0, maxFieldSize*scaleValue, displayY);
				} else if (maxFieldSize*scaleValue < displayX) {
					BOUNDS.set(0, 0, displayX, maxFieldSize*scaleValue);
				} else {
					BOUNDS.set(0, 0, maxFieldSize*scaleValue, maxFieldSize*scaleValue);
				}
				scrollAble = true;
			} else {
				BOUNDS.set(0, 0, displayX, displayY);
			}
		}
		
	}
	
	public void setDimensions(int x, int y) {
		displayX = x;
		displayY = y;
	}
	

}
