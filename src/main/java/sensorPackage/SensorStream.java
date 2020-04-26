package sensorPackage;

import commonPackage.Measurement;

public interface SensorStream {

    void sendMeasurement(Measurement m);

}
