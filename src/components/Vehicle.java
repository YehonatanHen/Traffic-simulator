package components;

import Mediator.Driver;
import utilities.Utilities;
import utilities.VehicleType;
import utilities.Timer;
import GUI.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class represent Vehicle on the map.
 *
 * @author Yehonatan Hen
 * @author Rotem Librati
 * @see VehicleType
 * @see Route
 * @see RouteParts
 * @see Road
 */
public class Vehicle implements Runnable,Utilities,Timer,Cloneable{
    private int id;
    private VehicleType vehicleType;
    private Route currentRoute;
    private RouteParts currentRoutePart;// The current type of route part when vehicle locating.
    private int timeFromStartRoute; //time in pulses from the start
    private int timeOnCurrentPart; //time in pulses from the checkIn at this part
    private static int objectCount=1; //Counter to num of objects
    private Road lastRoad; //Last road which car drove in or driving currently
    private String status=null; //Keep the status,will be in use for prints.
    private double X;//location of vehicle in X axis
    private double Y;//location of vehicle in Y axis
    private BigBrother bigBrother;
    private Moked moked;
    private Driver driver;

    /**
     * Vehicle randomal constructor.
     *
     * @param road
     */
    public Vehicle(Road road){
        super();
        id=objectCount;
        vehicleType=VehicleType.values()[getRandomInt(0,VehicleType.values().length)];//Randomise car type
        timeFromStartRoute=0;
        timeOnCurrentPart=0;
        lastRoad=road;
        lastRoad.addVehicleToWaitingVehicles(this);
        currentRoute=new Route(road,this);
        currentRoutePart=currentRoute.getRouteParts().get(0);
        successMessage(toString());
        currentRoute.checkIn(this);
        currentRoutePart.checkIn(this);
        objectCount++;
        X=lastRoad.getStartJunction().getX();
        Y=lastRoad.getStartJunction().getY();
        //implement the big brother in each car (created only once)
        bigBrother =BigBrother.getInstance();
        //Create mediator
        driver=new Driver(this);
    }

    public Vehicle(VehicleType vt){
        super();
        id=objectCount;
        this.vehicleType=vt;
        timeFromStartRoute=0;
        timeOnCurrentPart=0;
        lastRoad=Driving.map.getRoads().get(getRandomInt(0,Driving.map.getRoads().size()));
        lastRoad.addVehicleToWaitingVehicles(this);
        currentRoute=new Route(lastRoad,this);
        currentRoutePart=currentRoute.getRouteParts().get(0);
        successMessage(toString());
        currentRoute.checkIn(this);
        currentRoutePart.checkIn(this);
        objectCount++;
        X=lastRoad.getStartJunction().getX();
        Y=lastRoad.getStartJunction().getY();
        //implement the big brother in each car (created only once)
        bigBrother =BigBrother.getInstance();
        driver=new Driver(this);
    }

    //getters
    public int getid(){return id;}
    public VehicleType getVehicleType(){return vehicleType;}
    public Route getCurrentRoute(){return currentRoute;}
    public RouteParts getCurrentRoutePart(){return currentRoutePart;}
    public int getTimeFromStartRoute(){return timeFromStartRoute;}
    public int getTimeOnCurrentPart(){return timeOnCurrentPart;}
    public int getObjectCount(){return objectCount;}
    public Road getLastRoad(){return lastRoad;}
    public String getStatus(){return status;}

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public BigBrother getBigBrother() {
        return bigBrother;
    }

    public Driver getDriver() {
        return driver;
    }

    //setters
    public void setId(final int id){this.id=id;}
    public void setVehicleType(VehicleType v){vehicleType=v;}
    public void setCurrentRoute(Route current){currentRoute=current;}
    public void setCurrentRoutePart(RouteParts rp){currentRoutePart=rp;}
    public void setTimeFromStartRoute(final int time){timeFromStartRoute=time;}
    public void setTimeOnCurrentPart(final int time){timeOnCurrentPart=time;}
    public void setObjectCount(final int oc){objectCount=oc;}
    public void setLastRoad(Road r){lastRoad=r;}
    public void setStatus(final String s){status=s;}

    /**
     * Made check out if car can finish the current part and then checkIn to the next part.
     * else-stay at the part
     */
    public synchronized void move() {
        if (currentRoutePart.canLeave(this)) {
            currentRoutePart.checkOut(this);
            if (currentRoutePart.findNextPart(this) != null) {
                currentRoutePart = currentRoutePart.findNextPart(this);
                currentRoutePart.checkIn(this);
                timeOnCurrentPart=0;
            }
            else if (currentRoute.canLeave(this)) {
            currentRoute.checkOut(this);
            currentRoutePart = currentRoute.findNextPart(this);
            currentRoute.checkIn(this);
            currentRoutePart.checkIn(this);
            timeOnCurrentPart = 0;
            timeFromStartRoute = 0;
        }
        }else currentRoute.stayOnCurrentPart(this);
    }

    /**
     * Advance values of timeFromStartRoute and timeOnCurrentPart
     * call move() function.
     */
    public synchronized void incrementDrivingTime(){
        //Advance time in the route
        timeFromStartRoute+=1;
        //Advance time in current route part
        timeOnCurrentPart+=1;
        //Execute movement
        move();
    }

    @Override
    public String toString(){
        return "Vehicle " + objectCount+ ": "+  vehicleType.name() + ", average speed: "+ vehicleType.getAverageSpeed();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Vehicle){
            return vehicleType.equals(((Vehicle) o).vehicleType) &&
                    currentRoutePart.equals(((Vehicle) o).currentRoutePart) &&
                    timeOnCurrentPart==((Vehicle) o).timeOnCurrentPart &&
                    timeFromStartRoute==((Vehicle) o).timeFromStartRoute &&
                    lastRoad.equals(((Vehicle) o).lastRoad) &&
                    status.equals(((Vehicle) o).status);
        }
        return false;
    }

    /**
     * Function clone the vehicle
     * @return clone
     */
    public Object clone(){
        Vehicle v=new Vehicle(this.vehicleType);
        return v;
    }

    /**
     * Function change the location of the vehicle while his thread alive
     */
    @Override
    public void run() {
            if (!currentRoutePart.canLeave(this)) {
                if (currentRoutePart instanceof Road) {
                    double A = timeOnCurrentPart * Math.min(getLastRoad().getMaxSpeed(), vehicleType.getAverageSpeed());
                    double B = lastRoad.getLength() - A;
                    X = ((lastRoad.getStartJunction().getX() * B + lastRoad.getEndJunction().getX() * A) / (A + B));
                    Y = ((lastRoad.getStartJunction().getY() * B + lastRoad.getEndJunction().getY() * A) / (A + B));
                }
                if (currentRoutePart instanceof Junction) {
                    X = ((Junction) currentRoutePart).getX();
                    Y = ((Junction) currentRoutePart).getY();
                }
            }
        }
}
