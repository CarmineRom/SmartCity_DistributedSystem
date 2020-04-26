package edgeNodePackage;

import java.util.ArrayList;
import java.util.Calendar;

import commonPackage.EdgeNodeInfo;
import  commonPackage.Measurement;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import protosPackage.NodeServiceGrpc;
import protosPackage.ProtoMessage;

public class SenderMeasThread implements Runnable {

    private EdgeNodeInfo coordinator;
    String nodeId;
    private long midnight;

    public SenderMeasThread(String nodeId){
        this.nodeId=nodeId;
        this.midnight=computeMidnightMilliseconds();
    }

    @Override
    public void run() {

        boolean interrupted=false;
        while(!interrupted){
            try {
                //ATTESA 40 MISURAZIONI (wait() in Manager)
                ArrayList<Measurement> measurements= Manager.getInstance().getMeasurements();

                double mean=0;
                for(Measurement m: measurements){

                    mean+=m.getValue();
                }
                mean=mean/measurements.size();

                long timestamp=System.currentTimeMillis()-midnight;

                Measurement stat=new Measurement(this.nodeId,"",mean,timestamp);

                EdgeNodeInfo currentCoordinator;
                boolean messageSent=false;

                while(!messageSent){
                    currentCoordinator=Manager.getInstance().getCoordinator(true,0);

                    if(currentCoordinator.getId().equals(this.nodeId)){
                        //SONO DIVENTATO COORDINATORE DURANTE L'ATTESA DELLE 40 MISURAZIONI
                        //AGGIUNGI STATISTICA IN MANAGER
                        Manager.getInstance().addStatistic(stat);
                        messageSent=true;
                    }
                    else{
                        this.coordinator=currentCoordinator;
                        //INVIO STATISTICHE AL COORDINATORE
                        ProtoMessage.Measurement.Builder message = ProtoMessage.Measurement.newBuilder().setId(this.nodeId)
                                .setValue(stat.getValue()).setTimestamp(stat.getTimestamp());

                        ManagedChannel channel = ManagedChannelBuilder.forTarget(this.coordinator.getIp()+":"+this.coordinator.getNodePort())
                                .usePlaintext(true)
                                .build();
                        NodeServiceGrpc.NodeServiceBlockingStub stub = NodeServiceGrpc.newBlockingStub(channel);

                        ProtoMessage.Measurement response;

                        try{
                            response = stub.sendStatistic(message.build());
                            System.out.println("SenderMeasThread sent statistic");
                            System.out.println("Coordinator: "+currentCoordinator.getId());
                            System.out.println(message);
                            System.out.println("Global statistic received from coordinator: ");
                            System.out.println(response);
                            messageSent=true;
                        }catch (StatusRuntimeException e){ //COORDINATORE OFFLINE AVVIO NUOVA ELEZIONE
                            Manager.getInstance().setInElection(true);
                        }

                        channel.shutdown();
                    }
                }

            } catch (InterruptedException e) {
                System.out.println("SenderMeasThread Interrupted");
                interrupted=true;
            }
        }
    }

    private long computeMidnightMilliseconds(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
