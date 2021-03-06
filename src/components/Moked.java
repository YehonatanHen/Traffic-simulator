package components;

import Mediator.Driver;
import state.NotReadedState;
import state.ReadedState;
import state.ReportReadedContext;
import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Moked class- ReadWriteLock design pattern
 * @author Yehonatan Hen
 *  @author Rotem Librati
 */
public class Moked {
    private final Map<String, Data> m = new TreeMap<String, Data>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    private static File file;
    private FileReader fr;
    private FileWriter fw;
    private static int counter=0;
    private ReportReadedContext ctx;
    private static boolean state; //state dp
    String fileName="report.txt";
    private ArrayList<Driver> drivers;
    public Moked(){
        ctx=new ReportReadedContext();
        ctx.setState(new ReadedState());
        state=ctx.getState().isReaded();
        file=new File(fileName);
        try {
            fr=new FileReader(file);
            fw=new FileWriter(file);
        }catch (IOException f) {
            System.out.println(f);
        }
        drivers=new ArrayList<>();
    }

    /**
     * Read from file
     */
    public void confirm(Vehicle v) {
        r.lock();
        try {
            v.getDriver().receiveReport(fileName);
            v.getDriver().readReport();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            r.unlock();
            //When reports confirmed,states chenged to true.
            ctx.setState(new ReadedState());
            state=ctx.getState().isReaded();
        }
    }

    /**
     * Write data to a file
     * @param vehicle
     */
    public void put(Vehicle vehicle) {
        w.lock();
        try {
            fw.write("Report #"+(counter++)+"; Time from start route: "+vehicle.getTimeFromStartRoute()+ ", Vehicle ID: " +vehicle.getid()+".\n");
            fw.flush();
        }catch (IOException e){ System.out.println(e);}
        finally {
            w.unlock();
            //When new reports added,state change to false
            ctx.setState(new NotReadedState());
            state=ctx.getState().isReaded();
        }
    }

    /**
     * Function read the whole report and return it as a string variable
     * @return
     */
    public String readAllReport(){
        r.lock();
        String str="";
        try {
            int data=fr.read();
            while(data!=-1){
                str+=(char)data;
                data=fr.read();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            r.unlock();
            return str;
        }
    }

    public static boolean getState() {
        return state;
    }

    public void addDriver(Vehicle v){
        drivers.add(v.getDriver());
    }
}
