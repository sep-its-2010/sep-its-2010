package sep.conquest.activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import sep.conquest.R;
import sep.conquest.model.MapNode;

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

/**
 * The class MapSurfaceView represents the map which is shown in the Map class
 * and dynamically reacts on changes in the data structure. It contains an
 * inner class, which is responsible for the drawings on the surface view from
 * which this class extends of. It implements the interfaces
 * SurfaceHolder.Callback, which implements methods to control the surface
 * view and View.OnTouchListener, which fires an event by every touch on the
 * display.
 *
 * @author Florian Buerchner
 *
 */
public class MapSurfaceView extends SurfaceView
             implements SurfaceHolder.Callback, View.OnTouchListener {

    /**
     * This rectangle is static and always on the same size as the actual drawn
     * surface view. This is an advantage for the autoScale method, which only
     * can draw within the rectangle and avoids afresh and defective paintings
     * on the edge. If you do a touch on the display and drag the surface view,
     * you actual really move the rectangle on which the new position is
     * calculated.
     */
    private static Rect bounds = new Rect(0, 0, 0, 0);

    /**
     * This LinkedList saves the id of the robots and there x and y
     * coordinates on the map. This information must be available within the
     * surface view class to allocate the direct robot selection by long
     * touching them on the map.
     *
     */
    private LinkedList<EpuckPosition> positions;

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

    /**
     * @see sep.conquest.activity.MapSurfaceView.currentOffsetX
     */
    private float currentOffsetY = 0;

    /**
     * Contains the actual offset which is necessary for the scroll function.
     */
    private float previousOffsetX = 0;

    /**
     * @see sep.conquest.activity.MapSurfaceView.previousOffsetX
     */
    private float previousOffsetY = 0;

    /**
     * This is the start value for drawn objects at the beginning of an
     * exploration. It is used by the attribute scaleValue.
     */
    private static final int STARTVALUE = 80;

    /**
     * All draw methods use this value to scale the size of their drawable
     * object. This value is constantly reduced in autoScaling mode to ensure
     * an ideal display design.
     */
    private int scaleValue = STARTVALUE;
    
    private float scaleFactor;

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
    private int displayX;

    /**
     * @see sep.conquest.activity.MapSurfaceView.displayX
     */
    private int displayY;

    /**
     * The constructor resgisters the interface SurfaceHolder.Callback on the
     * surface view, sets the onTouchListener on the view and creates a new
     * thread object.
     *
     * @param context Global information on application environment.
     * @param attrs A collection of attributes, as found associated with a tag
     *                 in an XML document.
     */
    public MapSurfaceView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        setOnTouchListener(this);

        thread = new DrawThread();
    }

    /**
     * Must be implemented by the interface SurfaceHolder.Callback, but isn't
     * used here.
     *
     * @param holder Allows you to modify the surface.
     * @param format New pixel format of the surface.
     * @param width New width of the surface.
     * @param height New height of the surface.
     */
    public void surfaceChanged(final SurfaceHolder holder, final int format,
                               final int width, final int height) {
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
        positions = new LinkedList<EpuckPosition>();
        positions.add(new EpuckPosition(3, 3, 1224));
        positions.add(new EpuckPosition(10, 10, 1225));
        //for testing only
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
            	return;
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
     * 
     * @param v View the touch was made on.
     * @param event Event to get x and y coordinates.
     */
	public boolean onTouch(View v, MotionEvent event) {
		if (scrollAble) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			// Track starting point
			downTouchPoint = new PointF(event.getX(), event.getY());
			//check e-puck positions
			if (positions != null) {
				Iterator it = positions.iterator();
				while(it.hasNext()) {
					EpuckPosition e = (EpuckPosition) it.next();
					int xCoord = (int) (((e.getX()+1)*scaleValue) + currentOffsetX);
					int yCoord = (int) (((e.getY()+1)*scaleValue) + currentOffsetY);
					int test = (int) event.getX();
					int test2 = (int) event.getY();
					if (Math.abs(xCoord-test) < 40 && Math.abs(yCoord-test2) < 40) {
						int ha = e.getID();
					}
				}
			}
			
		} else if(event.getAction() == MotionEvent.ACTION_UP){
			// Clear starting point
			downTouchPoint = null;
			previousOffsetX = currentOffsetX;
			previousOffsetY = currentOffsetY;
			
		} else if(event.getAction() == MotionEvent.ACTION_MOVE){
			
			currentOffsetX = previousOffsetX + ((event.getX() - downTouchPoint.x));
			currentOffsetY = previousOffsetY + ((event.getY() - downTouchPoint.y));
			
			if(currentOffsetX < -(bounds.width() - getWidth())){
				currentOffsetX = -(bounds.width() - getWidth());
			} else if(currentOffsetX > 0){
				currentOffsetX = 0;
			}
			
			if(currentOffsetY < -(bounds.height() - getHeight())){
				currentOffsetY = -(bounds.height() - getHeight());
			} else if(currentOffsetY > 0){
				currentOffsetY = 0;
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
     * @author Florian Buerchner
     *
     */
    private class DrawThread extends Thread implements Observer {

        /**
         * Is initially set to false and is used to pause the thread. This case
         * occurs when an intent is created and the method surfaceDestroyed is
         * called.
         */
        private boolean paused = false;

        /**
         * Paint must be visible for all methods to set the color for multiple
         * crossed nodes, which differ to the other ones. It is also required
         * to draw the e-pucks and in another color for selected ones.
         */
        private Paint paint = new Paint();

        /**
         * This class extends of Thread and this method must be implemented.
         * It runs persistently in the background and executes an infinite loop.
         * This is necessary to react permanently on changes on the map, which
         * are provided by the update method. This infinite loop is only be
         * stopped when the thread is paused.
         */
        @Override
        public void run() {
            while(!paused){
            	doDraw(null);
            }
        } 

        /**
         * As attribute this method gets the whole map as LinkedList assigned
         * by the update method. At first it is checked if the size of map
         * must be scaled to fit into the screen. This case can only occur
         * if the map isn't scrollable yet. Otherwise when the map was scrolled
         * the actual and correct sector is displayed by using a matrix which
         * calculates the new position. Then it iterates through the list and
         * for every single node type the right draw method is called. How
         * often this node was crossed by e-pucks is painted previously. At the
         * last layer the positions of the e-pucks are painted.
         * 
         * @param map Complete map explored by robots.
         */
        private void doDraw(LinkedList map){
            //lock the canvas to do draw operations on it
        	int testX = 20;
        	int testY = 30;
        	Canvas c = getHolder().lockCanvas();
            
        	displayX = c.getWidth();
        	displayY = c.getHeight();
            // Translate the drawing matrix to the current drag offset
            Matrix m = new Matrix();
            m.setTranslate(currentOffsetX, currentOffsetY);
            c.setMatrix(m);
            
            //check if map is scrollable otherwise scale it
            if (!scrollAble) {
                autoScaling(c, testX, testY);
            } else {
            	int boundX = 0;
            	int boundY = 0;
            	if (displayX < testX*scaleValue) {
            		boundX = testX*scaleValue;
            	} else {
            		boundX = displayX;
            	}
            	if (displayY < testY*scaleValue) {
            		boundY = testY * scaleValue;
            	} else {
            		boundY = displayY;
            	}
            	bounds.set(0, 0, boundX, boundY);
            }
            
            // Sample drawing
            paint.setColor(0xff000000);
            c.drawRect(bounds, paint);
            
            //draw shapes and visited rectangles
            paint.setColor(0xff00ff00);

            Iterator<MapNode> it = map.iterator();
            //switch()
            /**
            for (int k = 0; k < testY; k++) {
            	for (int l = 0; l < testX; l++) {
            		drawCross(c, l*scaleValue, k*scaleValue);
            	}
            }
            */
            drawEpuck(c, 3, 3);
            drawEpuck(c, 10, 10);
            getHolder().unlockCanvasAndPost(c);
        }

        /**
         * Sets the attribute paused depending on exigency to true or false.
         * The run method is halted or continued and so the whole thread is
         * paused or not.
         * 
         * @param paused If true the thread is paused.
         */
        public void setPaused(boolean paused) {
            this.paused = paused;
        }

        /**
         * Implemented by the interface Observer this method is called by the
         * observables and a message object is assigned. This object has to be
         * casted to the correct type and contains new node informations and
         * e-puck positions. Subsequently the draw method is called with the
         * map object to draw the new events into the surface view. Additionally
         * the new e-puck positions are are added to the list positions. They
         * are required to select an e-puck directly out of the map.
         */
        public void update(Observable obs, Object map) {
            //dodraw aufrufen
            //anschließend drawEpucks aufrufen
            //eine liste für knoten und eine liste für epucks 
            //hier kommt die ganze karte rein
            
        }
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the method doDraw
         * when the node type is Cross and draws a cross as node. 
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        public void drawCross(Canvas c, int x, int y) {
            c.drawLine(x, y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
            c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + scaleValue, paint);
        }
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the doDraw method
         * when the node type is TopLeftEdge. It draws a edge node with the 
         * corner in the top left angle. The edge consists of two lines with 
         * equal length, which are 90 degrees towards each other.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        public void drawTopLeftEdge(Canvas c, int x, int y) {
            c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
            c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + (scaleValue/2), y + scaleValue, paint);
        }
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the doDraw method
         * when the node type is TopRightEdge. It draws a edge node with the 
         * corner in the top right angle. The edge consists of two lines with 
         * equal length, which are 90 degrees towards each other.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
    	public void drawTopRightEdge(Canvas c, int x, int y) {
    		c.drawLine(x, y + (scaleValue/2), x + (scaleValue/2), y + (scaleValue/2), paint);
    		c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + (scaleValue/2), y + scaleValue, paint);
    	}
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the doDraw method
         * when the node type is DrawBottomRightEdge. It draws a edge node with 
         * the corner in the bottom right angle. The edge consists of two lines 
         * with equal length, which are 90 degrees towards each other.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
    	public void drawBottomRightEdge(Canvas c, int x, int y) {
    		c.drawLine(x, y + (scaleValue/2), x + (scaleValue/2), y + (scaleValue/2), paint);
    		c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + (scaleValue/2), paint);
    	}
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the doDraw method
         * when the node type is DrawBottomLeftEdge. It draws a edge node with 
         * the corner in the bottom left angle. The edge consists of two lines 
         * with equal length, which are 90 degrees towards each other.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
    	public void drawBottomLeftEdge(Canvas c, int x, int y) {
    		c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + (scaleValue/2), paint);
    		c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
    	}
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the doDraw method
         * when the node type is TopT. It draws a node, which looks like a T.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        public void drawTopT(Canvas c, int x, int y) {
            c.drawLine(x, y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
            c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + (scaleValue/2), y + (scaleValue), paint);
        }
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the doDraw method
         * when the node type is RightT. It draws a T node, which is rotated
         * 90 degrees clockwise.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
    	public void drawRightT(Canvas c, int x, int y) {
    		c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + scaleValue, paint);
    		c.drawLine(x, y + (scaleValue/2), x + (scaleValue/2), y + (scaleValue/2), paint);
    	}
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the doDraw method
         * when the node type is BottomT. It draws a T node, which is rotated
         * 180 degrees clockwise.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        public void drawBottomT(Canvas c, int x, int y) {
            c.drawLine(x, y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
            c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + (scaleValue/2), paint);
        }
        
        /**
         * This method gets a canvas as attribute, which provides drawing
         * operations on the surface view. The values x and y define the 
         * position of the node. This method is called by the doDraw method
         * when the node type is RightT. It draws a T node, which is rotated
         * 90 degrees anti-clockwise.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        public void drawLeftT(Canvas c, int x, int y) {
            c.drawLine(x + (scaleValue/2), y, x + (scaleValue/2), y + scaleValue, paint);
            c.drawLine(x + (scaleValue/2), y + (scaleValue/2), x + scaleValue, y + (scaleValue/2), paint);
        }
        
        /**
         * This method draws the rectangles for multiple crossed nodes in
         * different red intensities. 
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         * @param visitedCounter Number of crossings over one node.
         */
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
        
        /**
         * This method draws the e-pucks into the map.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        public void drawEpuck(Canvas c, int x, int y) {
            paint.setColor(0xffcccccc);
            c.drawCircle(((x*scaleValue)+(scaleValue/2)), ((y*scaleValue)+(scaleValue/2)), (scaleValue/2), paint);
            paint.setColor(0xff0000ff);
            c.drawCircle(((x*scaleValue)+(scaleValue/2)), ((y*scaleValue)+(scaleValue/2)), (scaleValue/4), paint);
        }
        
        /**
         * If a e-puck is selected it is drawn in another color that we can
         * differ it from the other ones.
         * 
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        public void selectEpuck(Canvas c, int x, int y) {
            
        }
        
    }
    
    /**
     * If the explored map is bigger than the display size this method 
     * calculates a new scaleValue to get a map size, which fits the screen
     * ideal. If the scaleValue under-runs a threshold, e.g. when the e-pucks
     * aren't visible any more on the display, the scaleValue is set to 20 and 
     * the map is set to scrollable then.
     * 
     * @param fieldSizeX The maximal expansion in width.
     * @param fieldSizeY The maximal expansion in height.
     */
    public void autoScaling(Canvas c, int fieldSizeX, int fieldSizeY) {
		int xSize = fieldSizeX * scaleValue;
		int ySize = fieldSizeY * scaleValue;
		
		if (xSize > displayX && ySize < displayY) {
			float newX = (float) displayX/xSize;
			if (newX < 0.4f) {
				scrollAble = true;
				bounds.set(0, 0, xSize, ySize);
			} else {
				c.scale(newX, newX);
				scaleFactor = newX;
			}
		} else if (ySize > displayY && xSize < displayX) {
			float newY = (float) displayY/ySize;
			if (newY < 0.4f) {
				scrollAble = true;
			} else {
				c.scale(newY, newY);
				scaleFactor = newY;
			}
		} else if (ySize > displayY && xSize > displayX) {
			float newX = (float) displayX/xSize;
			float newY = (float) displayY/ySize;
			float newSize = Math.min(newX, newY);
			if (newSize < 0.4f) {
				scrollAble = true;
			} else {
				c.scale(newSize, newSize);
				scaleFactor = newSize;
			}
		}
        
    }
    
    /**
     * This method sets the attributes displayX and displayY to the actual
     * display size depending on the smartphone. It is called by the Map class
     * because you need a class, which extends Activity to get access to the
     * default window manager.
     * 
     * @param x This is the x value of the display.
     * @param y This is the y value of the display.
     */
    public void setDimensions(int x, int y) {
        displayX = x;
        displayY = y - 50;
    }
    
    /**
     * This is a getter method for the e-puck positions and ids to be 
     * synchronized with the map and the spinner in the map class.
     * 
     * @return Epuck position list.
     */
    public LinkedList<EpuckPosition> getEpuckPositions() {
    	return positions;
    }
    

}
