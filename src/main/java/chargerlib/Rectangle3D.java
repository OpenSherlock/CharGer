/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib;

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * Mimics the behavior and structure of a reagular awt rectangle except that it also has a depth.
 * By default all dimensions are of type Double.
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class Rectangle3D  {
    
    public double x;
    public double y;
    public double height;
    public double width;
    public double depth;

     /**
     * Create a new 3D rectangle with the given dimensions.
     * @param x
     * @param y
     * @param height
     * @param width
     * @param depth 
     */
    public Rectangle3D( double x, double y, double width, double height,  double depth ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
   /**
     * Create a new 3D rectangle with the given dimensions and depth 1.
     * @param x
     * @param y
     * @param width 
     * @param height
     */
    public Rectangle3D( double x, double y, double width,double height  ) {
        this( x, y, width, height, 1 );
    }
    
    public Rectangle3D( Rectangle2D.Double rect ) {
        this( rect.x, rect.y, rect.width, rect.height );
    }
    
    public Rectangle3D() {
        this( 0, 0, 0, 0 );
    }


    /**
     * Override the 2D dimensions leaving the depth intact.
     * @param rect 
     */
    public void setRectangle2D( Rectangle2D.Double rect ) {
        this.x = rect.getX();
        this.y = rect.getY();
        this.height = rect.getHeight();
        this.width = rect.getWidth();
    }
    
    public Rectangle2D.Double getRectangle2D() {
        return new Rectangle2D.Double( x, y, width, height );
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth( double width ) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight( double height ) {
        this.height = height;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth( double depth ) {
        this.depth = depth;
    }
    
    
}
