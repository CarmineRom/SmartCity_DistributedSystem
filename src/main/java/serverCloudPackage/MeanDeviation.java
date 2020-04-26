package serverCloudPackage;

import commonPackage.Measurement;

import java.util.ArrayList;

public class MeanDeviation {

    double mean;
    double deviation;
    
    public MeanDeviation(){

        mean=0;
        deviation=0;
    }
    
    public void setMeanDeviation(ArrayList<Measurement> measurements){
        double tempMean=0;
        for(Measurement m : measurements)
            tempMean+=m.getValue();

        this.mean=tempMean/measurements.size();

        double tempDeviation=0;
        for(Measurement m : measurements)
            tempDeviation+=((m.getValue()*m.getValue())-(this.mean*this.mean));

        this.deviation=Math.sqrt(tempDeviation/measurements.size());
    }
    
}
