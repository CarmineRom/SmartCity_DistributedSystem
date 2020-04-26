package sensorPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SensorStarter {

    private static final String SERVER_CLOUD_IP = "localhost";
    private static final int SERVER_CLOUD_PORT = 1337;

    public static void main(String[] args) throws IOException {

        BufferedReader localInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Insert the number of sensors to initialize");
        int numSensors=Integer.parseInt(localInput.readLine());
        ArrayList<PM10Simulator> sensors=new ArrayList<>(numSensors);

        for(int i=0;i<numSensors;i++){

            PM10Simulator sensor = new PM10Simulator(new sendMeasurementImpl());
            sensors.add(sensor);
            sensor.start();
        }
        System.in.read();
        System.out.println("Stopped");
        for(PM10Simulator s: sensors)
            s.stopMeGently();

        localInput.close();
        System.exit(0);
    }
}
