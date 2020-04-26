package edgeNodePackage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.MediaType;

import commonPackage.Measurement;

import protosPackage.ProtoMessage;
import protosPackage.ProtoMessage.StatsMessage;

import java.util.ArrayList;
import java.util.Calendar;

public class CoordinatorThread implements Runnable{

    private String serverCloudIP;
    private int serverCloudPort;
    private long midnight;

    public CoordinatorThread(String serverCloudIP, int serverCloudPort){

        this.serverCloudIP=serverCloudIP;
        this.serverCloudPort=serverCloudPort;
        this.midnight=computeMidnightMilliseconds();
    }

    @Override
    public void run(){
        boolean interrupted=false;
        while(!interrupted){
            try {
                Thread.sleep(5000); //INTERVALLO DI 5 SECONDI
                ArrayList<Measurement> nodesStatistics= Manager.getInstance().getNodesStatistics();

                if(!nodesStatistics.isEmpty()){

                    //COSTRUZIONE PROTOMESSAGE
                    StatsMessage.Builder messageBuilder= StatsMessage.newBuilder();
                    StatsMessage.StatList.Builder statListBuilder = StatsMessage.StatList.newBuilder();
                    StatsMessage.NodeStatistics.Builder nodeStatBuilder = StatsMessage.NodeStatistics.newBuilder();
                    StatsMessage.MeasList.Builder measListBuilder = StatsMessage.MeasList.newBuilder();
                    ProtoMessage.Measurement.Builder measBuilder = ProtoMessage.Measurement.newBuilder();

                    for (Measurement m : nodesStatistics){

                        measBuilder.clear();
                        measListBuilder.clear();
                        nodeStatBuilder.clear();
                        measBuilder.setValue(m.getValue()).setTimestamp(m.getTimestamp()).build();
                        measListBuilder.addMeasurements(measBuilder).build();
                        nodeStatBuilder.setId(m.getId());
                        nodeStatBuilder.setMeasList(measListBuilder).build();
                        statListBuilder.addStatistics(nodeStatBuilder);
                    }

                    messageBuilder.setLocalStats(statListBuilder.build());

                    double globalValue=0;
                    for(Measurement m:nodesStatistics)
                        globalValue+=m.getValue();

                    globalValue=globalValue/nodesStatistics.size();
                    long timestamp=System.currentTimeMillis()-midnight;

                    //AGGIORNAMENTO STATISTICHE GLOBALI E COSTRUZIONE MESSAGGIO
                    Manager.getInstance().updateGlobalStat(new Measurement("","",globalValue,timestamp));
                    measBuilder.clear();
                    measListBuilder.clear();
                    measListBuilder.addMeasurements(measBuilder.setValue(globalValue).setTimestamp(timestamp).build());
                    messageBuilder.setGlobalStats(measListBuilder);

                    Client client=Client.create();
                    WebResource webResource = client.resource("http://"+serverCloudIP+":"+serverCloudPort+"/nodeservices/addstats");
                    ClientResponse clientResponse= webResource.type(MediaType.APPLICATION_OCTET_STREAM)
                            .post(ClientResponse.class,messageBuilder.build().toByteArray());

                    while(!(clientResponse.getStatus()==201)){
                        clientResponse= webResource.type(MediaType.APPLICATION_OCTET_STREAM)
                                .post(ClientResponse.class,messageBuilder.build().toByteArray());
                    }

                    System.out.println("CoordinatorThread sent these statistics to ServerCloud:");
                    System.out.println(messageBuilder);
                }
            } catch (InterruptedException e) {
                System.out.println("CoordinatorThread Interrupted");
                interrupted=true;
            }

        }
    }

    private long computeMidnightMilliseconds(){ //Presa da Simulator
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
