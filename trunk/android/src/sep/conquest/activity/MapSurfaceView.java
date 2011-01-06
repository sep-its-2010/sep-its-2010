package sep.conquest.activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sep.conquest.model.GridMap;
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
import android.widget.Spinner;

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
     * This is the start value for drawn objects at the beginning of an
     * exploration. It is used by the attribute scaleValue.
     */
    private static final int DRAW_SIZE = 80;

    /**
     * This rectangle is static and always on the same size as the actual drawn
     * surface view. This is an advantage for the autoScale method, which only
     * can draw within the rectangle and avoids afresh and defective paintings
     * on the edge. If you do a touch on the display and drag the surface view,
     * you actual really move the rectangle on which the new position is
     * calculated.
     */
    private Rect mBounds = new Rect(0, 0, 0, 0);

    /**
     * This LinkedList saves the id of the robots and there x and y
     * coordinates on the map. This information must be available within the
     * surface view class to allocate the direct robot selection by long
     * touching them on the map.
     *
     */
    private LinkedList < EpuckPosition > mPositions;

    /**
     * Saves a reference to the original map. Must be available for drawing
     * operations.
     */
    private List < MapNode > mMap;

    /**
     * Saves a reference to the borders of the map.
     */
    private int[] mBorders;

    /**
     * Saves the id of the current selected robot.
     */
    private Spinner mRobotSelect;

    /**
     * Makes the DrawThread, which is essential to draw the surface view visible
     * and accessable for all methods. This is required to react on the
     * different states of the surface view, e.g. when a method sets the thread
     * paused.
     */
    private DrawThread mThread;


    /**
     * Is required to save the coordinates of the touch point adequate
     * for the scroll function.
     */
    private PointF mDownTouchPoint;

    /**
     * Contains the adjustment value of the surface view relative to the
     * previousOffset values and adds the downTouchPoint to show the map
     * on the right position.
     */
    private float mCurrentOffsetX = 0;

    /**
     * @see sep.conquest.activity.MapSurfaceView.currentOffsetX
     */
    private float mCurrentOffsetY = 0;

    /**
     * Contains the actual offset which is necessary for the scroll function.
     */
    private float mPreviousOffsetX = 0;

    /**
     * @see sep.conquest.activity.MapSurfaceView.previousOffsetX
     */
    private float mPreviousOffsetY = 0;

    /**
     * All draw methods use this value to scale the size of their drawable
     * object. This value is constantly reduced in autoScaling mode to ensure
     * an ideal display design.
     */
    private int mScaleValue = DRAW_SIZE;

    /**
     * This attribute saves the id of the robot which is selected. If no
     * robot is selected it is set to -1.
     */
    private String mSelectedRobot;

    /**
     * Saves the current map mode.
     */
    private MapMode mMode;

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

        mMap = new LinkedList < MapNode >();
        mPositions = new LinkedList < EpuckPosition >();
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
                               final int width, final int height) { }

    /**
     * Is implemented by the interface surfaceHolder.Callback and is called
     * when a new surface view is created. It creates an instance of DrawThread
     * and starts the thread.
     *
     * @param holder Holds the display surface and is able to control it.
     */
    public final void surfaceCreated(final SurfaceHolder holder) {
        mThread = new DrawThread();
        mThread.start();

    }

    /**
     * Is implemented by the interface surfaceHolder.Callback and is called
     * when the actual view is exited. This could also be caused by an intent,
     * so the thread has to be paused and waits until the end of other threads
     * to be continued.
     *
     * @param holder Holds the display surface and is able to control it.
     */
    public final void surfaceDestroyed(final SurfaceHolder holder) {
        boolean retry = true;
        mThread.setPaused(true);
        
        mMap = new LinkedList();
        mPositions = new LinkedList();
        
        while (retry) {
            try {
                mThread.join();
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
     * @return Returns true when a robot was selected or the map was scrolled.
     */
    public final boolean onTouch(final View v, final MotionEvent event) {
        if ((mPositions != null) && (mMode == MapMode.EXPLORATION)) {
            int threshold = 40;
            Iterator < EpuckPosition > it = mPositions.iterator();
            int x = (int) event.getX();
            int y = (int) event.getY();

            while (it.hasNext()) {
                EpuckPosition e = (EpuckPosition) it.next();
                int xCoord = (int) ((e.getX() * mScaleValue) + mCurrentOffsetX);
                int yCoord = (int) ((e.getY() * mScaleValue) + mCurrentOffsetY);
                if (Math.abs(xCoord - x) < threshold
                    && Math.abs(yCoord - y) < threshold) {
                    mSelectedRobot = e.getID();
                    mRobotSelect.setSelection(mPositions.indexOf(e) + 1);
                }
            }
        }

        if (mMode != MapMode.PREVIEW && mMode != MapMode.IMPORT) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            // Track starting point
            mDownTouchPoint = new PointF(event.getX(), event.getY());

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // Clear starting point
            mDownTouchPoint = null;
            mPreviousOffsetX = mCurrentOffsetX;
            mPreviousOffsetY = mCurrentOffsetY;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            mCurrentOffsetX = mPreviousOffsetX
                              + ((event.getX() - mDownTouchPoint.x));
            mCurrentOffsetY = mPreviousOffsetY
                              + ((event.getY() - mDownTouchPoint.y));

            if (mCurrentOffsetX < -(mBounds.width() - getWidth())) {
                mCurrentOffsetX = -(mBounds.width() - getWidth());
            } else if (mCurrentOffsetX > 0) {
                mCurrentOffsetX = 0;
            }

            if (mCurrentOffsetY < -(mBounds.height() - getHeight())) {
                mCurrentOffsetY = -(mBounds.height() - getHeight());
            } else if (mCurrentOffsetY > 0) {
                mCurrentOffsetY = 0;
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
    private class DrawThread extends Thread {

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
        public void run() {
            while (!paused) {
                doDraw();
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
         */
        private void doDraw() {
            //Lock the canvas to do draw operations on it.
            Canvas c = getHolder().lockCanvas();

            //Set current size of canvas
            int displayX = c.getWidth();
            int displayY = c.getHeight();

            // Translate the drawing matrix to the current drag offset
            Matrix m = new Matrix();
            m.setTranslate(mCurrentOffsetX, mCurrentOffsetY);
            c.setMatrix(m);

            //Read attributes of current map - react on negative values
            int minX = 0;
            int minY = 0;
            int maxX = 0;
            int maxY = 0;
            if (mBorders != null) {
            minX = mBorders[0];
            minY = mBorders[1];
            maxX = mBorders[2];
            maxY = mBorders[3];
            }
            
            int offsetX = 0;
            int offsetY = 0;
            if (minY < 0) {
            	offsetY = Math.abs(minY);
            } else if (minY > 0) {
            	offsetY = -minY;
            }
            if (minX < 0) {
            	offsetX = Math.abs(minX);
            } else if (minX > 0) {
            	offsetX = -minX;
            }

            //scale the map or scroll it
            if (mMode == MapMode.PREVIEW || mMode == MapMode.IMPORT) {
                autoScaling(c, (maxX - minX + 1),
                               (maxY - minY + 1), displayX, displayY);
            } else {
                int boundX = 0;
                int boundY = 0;
                
                if (displayX < (maxX - minX + 1) * mScaleValue) {
                    boundX = (maxX - minX + 1) * mScaleValue;
                } else {
                    boundX = displayX;
                }
                if (displayY < (maxY - minY + 1) * mScaleValue) {
                    boundY = (maxY - minY + 1) * mScaleValue;
                } else {
                    boundY = displayY;
                }
                mBounds.set(0, 0, boundX, boundY);
            }

            //Draw background for scrollable rect
            paint.setColor(0xff000000);
            c.drawRect(mBounds, paint);

            //Draw First Layer
            if (mMap != null) {
                drawFirstLayer(c, offsetX, offsetY);
            }

            //Draw Second Layer
            if (mPositions != null) {
                drawSecondLayer(c, offsetX, offsetY);
            }

            //Unlock canvas and draw on display.
            getHolder().unlockCanvasAndPost(c);
        }

        /**
         * Sets the attribute paused depending on exigency to true or false.
         * The run method is halted or continued and so the whole thread is
         * paused or not.
         *
         * @param pause If true the thread is paused.
         */
        public void setPaused(final boolean pause) {
            paused = pause;
        }

        /**
         * Draws the first layer on the canvas. Overrun node colors and nodes
         * themselves.
         *
         * @param c Canvas to draw on.
         * @param offsetX Offset x.
         * @param offsetY Offset y.
         */
        private void drawFirstLayer(final Canvas c, final int offsetX,
                final int offsetY) {
            // Draw first layer: Visited Rects and Nodes
        	synchronized(mMap) {
        		Iterator < MapNode > mapIt = mMap.iterator();
            MapNode mn;
            while (mapIt.hasNext()) {
                mn = mapIt.next();
                int xValue = (mn.getXValue() + offsetX) * mScaleValue;
                int yValue = (mn.getYValue() + offsetY) * mScaleValue;

                drawVisited(c, xValue, yValue, mn.getVisitCounter(), paint);

                paint.setColor(0xff000000);
                switch (mn.getNodeType()) {
                case BOTTOMLEFTEDGE:
                    drawTopRightEdge(c, xValue, yValue);
                    break;
                case BOTTOMRIGHTEDGE:
                    drawTopLeftEdge(c, xValue, yValue);
                    break;
                case BOTTOMT:
                    drawTopT(c, xValue, yValue);
                    break;
                case TOPLEFTEDGE:
                    drawBottomRightEdge(c, xValue, yValue);
                    break;
                case TOPRIGHTEDGE:
                    drawBottomLeftEdge(c, xValue, yValue);
                    break;
                case TOPT:
                    drawBottomT(c, xValue, yValue);
                    break;
                case LEFTT:
                    drawRightT(c, xValue, yValue);
                    break;
                case RIGHTT:
                    drawLeftT(c, xValue, yValue);
                    break;
                case CROSS:
                    drawCross(c, xValue, yValue);
                    break;
                default:
                    break;
                }
            }
        	}
        }

        /**
         * Draws the second layer on canvas. The robots.
         *
         * @param c Canvas to draw on.
         * @param offsetX Offset x.
         * @param offsetY Offset y.
         */
        private void drawSecondLayer(final Canvas c, final int offsetX,
                                     final int offsetY) {
			// Draw the second layer: Robots
			synchronized (mPositions) {
				Iterator<EpuckPosition> posIt = mPositions.iterator();
				EpuckPosition epp;
				while (posIt.hasNext()) {
					epp = posIt.next();
					int x = (epp.getX() + offsetX) * mScaleValue;
					int y = (epp.getY() + offsetY) * mScaleValue;
					if (epp.getID().equals(mSelectedRobot)) {
						selectEpuck(c, x, y);
					} else {
						drawEpuck(c, x, y);
					}
					paint.setStrokeWidth(5.0f);
					switch (epp.getOrientation()) {
					case DOWN: drawOrientationUp(c, x, y); break;
					case UP: drawOrientationDown(c, x, y); break;
					case LEFT: drawOrientationLeft(c, x, y); break;
					case RIGHT: drawOrientationRight(c, x, y); break;
					}
					paint.setStrokeWidth(0f);
				}
			}
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
        private void drawCross(final Canvas c, final int x, final int y) {
            c.drawLine(x, y + (mScaleValue / 2), x + mScaleValue,
                                                 y + (mScaleValue / 2), paint);
            c.drawLine(x + (mScaleValue / 2), y, x + (mScaleValue / 2),
                                                 y + mScaleValue, paint);
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
        private void drawTopLeftEdge(final Canvas c, final int x, final int y) {
            c.drawLine(x + (mScaleValue / 2), y + (mScaleValue / 2),
                       x + mScaleValue, y + (mScaleValue / 2), paint);
            c.drawLine(x + (mScaleValue / 2), y + (mScaleValue / 2),
                       x + (mScaleValue / 2), y + mScaleValue, paint);
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
        private void drawTopRightEdge(final Canvas c, final int x,
                                      final int y) {
            c.drawLine(x, y + (mScaleValue / 2), x + (mScaleValue / 2),
                          y + (mScaleValue / 2), paint);
            c.drawLine(x + (mScaleValue / 2), y + (mScaleValue / 2),
                       x + (mScaleValue / 2), y + mScaleValue, paint);
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
        private void drawBottomRightEdge(final Canvas c, final int x,
                                         final int y) {
            c.drawLine(x, y + (mScaleValue / 2), x + (mScaleValue / 2),
                          y + (mScaleValue / 2), paint);
            c.drawLine(x + (mScaleValue / 2), y, x + (mScaleValue / 2),
                       y + (mScaleValue / 2), paint);
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
        private void drawBottomLeftEdge(final Canvas c, final int x,
                                        final int y) {
            c.drawLine(x + (mScaleValue / 2), y, x + (mScaleValue / 2),
                       y + (mScaleValue / 2), paint);
            c.drawLine(x + (mScaleValue / 2), y + (mScaleValue / 2),
                       x + mScaleValue, y + (mScaleValue / 2), paint);
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
        private void drawTopT(final Canvas c, final int x, final int y) {
            c.drawLine(x, y + (mScaleValue / 2), x + mScaleValue,
                          y + (mScaleValue / 2), paint);
            c.drawLine(x + (mScaleValue / 2), y + (mScaleValue / 2),
                       x + (mScaleValue / 2), y + (mScaleValue), paint);
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
        private void drawRightT(final Canvas c, final int x, final int y) {
            c.drawLine(x + (mScaleValue / 2), y, x + (mScaleValue / 2),
                       y + mScaleValue, paint);
            c.drawLine(x, y + (mScaleValue / 2), x + (mScaleValue / 2),
                          y + (mScaleValue / 2), paint);
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
        private void drawBottomT(final Canvas c, final int x, final int y) {
            c.drawLine(x, y + (mScaleValue / 2), x + mScaleValue,
                          y + (mScaleValue / 2), paint);
            c.drawLine(x + (mScaleValue / 2), y, x + (mScaleValue / 2),
                       y + (mScaleValue / 2), paint);
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
        private void drawLeftT(final Canvas c, final int x, final int y) {
            c.drawLine(x + (mScaleValue / 2), y, x + (mScaleValue / 2),
                       y + mScaleValue, paint);
            c.drawLine(x + (mScaleValue / 2), y + (mScaleValue / 2),
                       x + mScaleValue, y + (mScaleValue / 2), paint);
        }

        /**
         * This method draws the rectangles for multiple crossed nodes in
         * different red intensities.
         *
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         * @param visitedCounter Number of crossings over one node.
         * @param p Paint to set the node color.
         */
        private void drawVisited(final Canvas c, final int x, final int y,
                                 final int visitedCounter, final Paint p) {
            //switch color by visited index
            switch(visitedCounter) {
                case 1: paint.setColor(0xffffffff);
                        break;
                case 2: paint.setColor(0xfff08080);
                        break;
                case 3: paint.setColor(0xffff6347);
                        break;
                case 4: paint.setColor(0xffff4500);
                        break;
                case 5: paint.setColor(0xffff0000);
                        break;
                case 6: paint.setColor(0xffb22222);
                        break;
                default: paint.setColor(0xff444444);
            }
            c.drawRect(x, y, x + mScaleValue, y + mScaleValue, p);
        }

        /**
         * This method draws the e-pucks into the map.
         *
         * @param c Canvas offers drawing operations on surface view.
         * @param x X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        private void drawEpuck(final Canvas c, final int x, final int y) {
            paint.setColor(0xffcccccc);
            c.drawCircle((x + (mScaleValue / 2)), (y + (mScaleValue / 2)),
                         (mScaleValue / 2), paint);
            paint.setColor(0xff0000ff);
            c.drawCircle((x + (mScaleValue / 2)), (y + (mScaleValue / 2)),
                         (mScaleValue / 4), paint);
        }

        /**
         * If a e-puck is selected it is drawn in another color that we can
         * differ it from the other ones.
         *
         * @param c Canvas offers drawing operations on surface view.
         * @param x    X coordinate of the node.
         * @param y Y coordinate of the node.
         */
        private void selectEpuck(final Canvas c, final int x, final int y) {
            paint.setColor(0xffcccccc);
            c.drawCircle((x + (mScaleValue / 2)), (y + (mScaleValue / 2)),
                         (mScaleValue / 2), paint);
            paint.setColor(0xffff0000);
            c.drawCircle((x + (mScaleValue / 2)), (y + (mScaleValue / 2)),
                         (mScaleValue / 4), paint);
        }
        
		private void drawOrientationDown(final Canvas c, final int x, 
				final int y) {
			paint.setColor(0xff000000);
			c.drawLine(x + (mScaleValue / 2), y, x + (mScaleValue / 2), y + mScaleValue, paint);
			c.drawLine(x, y + (mScaleValue / 2), x + (mScaleValue / 2), y + mScaleValue, paint);
			c.drawLine(x + mScaleValue, y + (mScaleValue / 2), x + (mScaleValue / 2), y + mScaleValue, paint);
		}

		private void drawOrientationUp(final Canvas c, final int x,
				final int y) {
			paint.setColor(0xff000000);
			c.drawLine(x + (mScaleValue / 2), y, x + (mScaleValue / 2), y + mScaleValue, paint);
			c.drawLine(x, y + (mScaleValue / 2), x + (mScaleValue / 2), y, paint);
			c.drawLine(x + (mScaleValue / 2), y, x + mScaleValue, y + (mScaleValue / 2), paint);
		}

		private void drawOrientationLeft(final Canvas c, final int x,
				final int y) {
			paint.setColor(0xff000000);
			c.drawLine(x, y + (mScaleValue / 2), x + mScaleValue, y + (mScaleValue / 2), paint);
			c.drawLine(x + (mScaleValue / 2), y, x, y + (mScaleValue / 2), paint);
			c.drawLine(x, y + (mScaleValue / 2), x + (mScaleValue / 2), y + mScaleValue, paint);
		}

		private void drawOrientationRight(final Canvas c, final int x,
				final int y) {
			paint.setColor(0xff000000);
			c.drawLine(x, y + (mScaleValue / 2), x + mScaleValue, y + (mScaleValue / 2), paint);
			c.drawLine(x + (mScaleValue / 2), y, x + mScaleValue, y + (mScaleValue / 2), paint);
			c.drawLine(x + (mScaleValue / 2), y + mScaleValue, x + mScaleValue, y + (mScaleValue / 2), paint);
		}

    }

    /**
     * If the explored map is bigger than the display size this method
     * calculates a new scaleValue to get a map size, which fits the screen
     * ideal. If the scaleValue under-runs a threshold, e.g. when the e-pucks
     * aren't visible any more on the display, the scaleValue is set to 20 and
     * the map is set to scrollable then.
     *
     * @param c Canvas to draw on.
     * @param fieldSizeX The maximal expansion in width.
     * @param fieldSizeY The maximal expansion in height.
     * @param displayX The x display size.
     * @param displayY The y display size.
     */
    public final void autoScaling(final Canvas c, final int fieldSizeX,
           final int fieldSizeY, final int displayX, final int displayY) {
        int xSize = fieldSizeX * mScaleValue;
        int ySize = fieldSizeY * mScaleValue;

        if (xSize > displayX && ySize < displayY) {
            float newX = (float) displayX / xSize;
                c.scale(newX, newX);
        } else if (ySize > displayY && xSize < displayX) {
            float newY = (float) displayY / ySize;
                c.scale(newY, newY);
        } else if (ySize > displayY && xSize > displayX) {
            float newX = (float) displayX / xSize;
            float newY = (float) displayY / ySize;
            float newSize = Math.min(newX, newY);
                c.scale(newSize, newSize);
        }

    }

    /**
     * Sets the id of the chosen robot.
     *
     * @param id ID or name of the robot.
     */
    public final void setSelectedRobot(final String id) {
        mSelectedRobot = id;
    }

    /**
     * Set the list of the robot positions.
     *
     * @param list List which contains the position objects.
     */
    public final void setRobotPosition(
                      final LinkedList < EpuckPosition > list) {
    	synchronized(mPositions) {
        mPositions = (LinkedList<EpuckPosition>) list.clone();
    	}
    }

    /**
     * Set the list with nodes. Includes the whole map.
     *
     * @param list List with all nodes.
     * @param array The borders of the map.
     */
    public final void setMap(final List < MapNode > list, final int[] array) {
        mMap = list;
        mBorders = array;
    }

    /**
     * Allow access on spinner for Surface View to choose a robot out the map
     * by a touch event.
     *
     * @param selector The spinner object.
     */
    public final void setSpinner(final Spinner selector) {
        mRobotSelect = selector;
    }

    /**
     * Set the map mode. Needed for imoprt mode to disable spinner and
     * scrolling.
     *
     * @param mode The current map mode.
     */
    public final void setMode(final MapMode mode) {
        mMode = mode;
    }

}
