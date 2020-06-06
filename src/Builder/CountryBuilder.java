package Builder;

import AbstractFactory.*;
import components.*;
import utilities.Utilities;
import utilities.VehicleType;

import java.util.ArrayList;
import java.util.Random;

public class CountryBuilder implements mapBuilder,Utilities{
    private MapB map;
    @Override
    public void buildRoads() {
        if (map.getJuntions() != null) {
            for (int i = 0; i < map.getJuntions().size(); i++)
                for (int j = 0; j < map.getJuntions().size(); j++) {
                    //Add new roads only if junctions aren't the sane and random value equals 1 (in that way not all of the junctions will connect)
                    if (i != j && getRandomInt(0, 2) == 1) {
                        map.getRoads().add(new Road(map.getJuntions().get(i), map.getJuntions().get(j)));
                        if (map.getJuntions().get(j) instanceof LightedJunction) {
                            ((LightedJunction) map.getJuntions().get(j)).getLights().getRoads().add(map.getRoads().get(map.getRoads().size() - 1));
                        }
                    }
                }
        }
    }

    @Override
    public void buildJunctions() {
        ArrayList <Junction> junctions=new ArrayList<>();
        String [] types=new String[]{Junction.class.getName(), LightedJunction.class.getName()};
        for(int i=0;i<6;i++){
            junctions.add(JFactory.getJunction("country"));
        }
        map.setJunctions(junctions);
    }

    @Override
    public void buildVehicles() {
        ArrayList<Vehicle> allowedVehicles=new ArrayList<>();
        allowedVehicles.add(new Vehicle(((twoWheelVehicle)Factory.getFactory(2)).getVehicle("fast")));
        allowedVehicles.add(new Vehicle(((twoWheelVehicle)Factory.getFactory(4)).getVehicle("private")));
        allowedVehicles.add(new Vehicle(((twoWheelVehicle)Factory.getFactory(4)).getVehicle("work")));
        allowedVehicles.add(new Vehicle(((twoWheelVehicle)Factory.getFactory(4)).getVehicle("public")));
        allowedVehicles.add(new Vehicle(((twoWheelVehicle)Factory.getFactory(10)).getVehicle("work")));
        ArrayList<Vehicle> vehicles=new ArrayList<>();
        for(int i=0;i<10;i++){
            vehicles.add((Vehicle) allowedVehicles.get(getRandomInt(0,10)).clone());
        }
        map.setVehicles(vehicles);
    }

    @Override
    public MapB getMap() {
        return map;
    }
}