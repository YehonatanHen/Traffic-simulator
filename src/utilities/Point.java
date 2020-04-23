package utilities;


/**
 * class represent a point on the map.
 *
 * @author Yehonatan Hen-207630112
 * @author Rotem Librati-
 * @see Utilities
 */
public abstract class Point implements Utilities {
    private final int minVal=0;//Minimal values of coordinates X and Y
    private final int maxX=800;//Maximal value of X
    private final int maxY=600;//Maximal value of Y
    //x and y are coordinates of some point on the map in cartesian axis system
    private double x;
    private double y;

    /**
     * Point constructor
     *
     * @param x
     * @param y
     */
    Point(double x,double y){
        if(checkValue(x,minVal,maxX)) this.x=x;
        else {
           double wrongVal=x;
           x=getRandomDouble(minVal,maxX);
           correctingMessage(wrongVal,x,"x");
        }
        if(checkValue(y,minVal,maxY)) this.y=y;
        else {
            double wrongVal=y;
            x=getRandomDouble(minVal,maxY);
            correctingMessage(wrongVal,y,"y");
        }
        successMessage(toString());
    }

    /**
     * Point constructor-make point with randomal values
     */
    Point(){
        x=getRandomDouble(minVal,maxX);
        y=getRandomDouble(minVal,maxY);
        successMessage(toString());
    }

    //getters
    public double getX(){return x;}
    public double getY(){return y;}
    //setters
    public void setX(double x){this.x=x;}
    public void setY(double y){this.y=y;}

    public String toString(){
        return "Point ("+x+" , "+y+")";
    }

    public boolean equals(Object o){
        if (o instanceof Point){
            return (((Point) o).x==x && ((Point)o).y==y);
        }
        return false;
    }

    /**
     * Function calculate the distance between 2 points
     * by formula.
     *
     * @param other
     * @return distance between this to other point.
     */
    public double calcDistance(Point other){
        return Math.sqrt(Math.pow(x - other.getX(), 2) +
                Math.pow(y - other.getY(), 2));
    }

}
