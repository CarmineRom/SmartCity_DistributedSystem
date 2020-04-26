package sensorPackage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import commonPackage.EdgeNodeInfo;
import commonPackage.Measurement;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.commons.io.IOUtils;
import protosPackage.ProtoMessage;
import protosPackage.SensorServiceGrpc;

import java.io.InputStream;
import java.util.Random;

public class sendMeasurementImpl implements  SensorStream {

    int posX;
    int posY;
    EdgeNodeInfo nodeInfo;
    static final String SERVER_CLOUD_IP = "localhost";
    static final int SERVER_CLOUD_PORT = 1337;

    public sendMeasurementImpl(){

        Random rand = new Random();
        this.posX=rand.nextInt(100);
        this.posY=rand.nextInt(100);
        this.nodeInfo=new EdgeNodeInfo();
        getNodeFromServer();
        Thread nodeChecker = new Thread(new NodeChecker(this.posX,this.posY,this.nodeInfo));
        nodeChecker.start();
    }

    public void sendMeasurement(Measurement measurement){

        ManagedChannel channel=null;
        boolean noNodes=false;
        synchronized (this.nodeInfo) {

            if (!(this.nodeInfo.getIp() == null)) {
                System.out.println("Sending Measurements to node ip: " + this.nodeInfo.getIp());
                channel = ManagedChannelBuilder.forTarget(nodeInfo.getIp() + ":" + nodeInfo.getSensorPort())
                        .usePlaintext(true)
                        .build();

            }
            else{
                getNodeFromServer();
                if (!(this.nodeInfo.getIp() == null)) {
                    System.out.println("Sending Measurements to node ip: " + this.nodeInfo.getIp());
                    channel = ManagedChannelBuilder.forTarget(nodeInfo.getIp() + ":" + nodeInfo.getSensorPort())
                            .usePlaintext(true)
                            .build();
                }
                else{
                    noNodes=true;
                    System.out.println("No nodes in the Network. Wasting measurements.");
                }
            }
        }

        if(!noNodes){
            SensorServiceGrpc.SensorServiceBlockingStub stub = SensorServiceGrpc.newBlockingStub(channel);
            ProtoMessage.Measurement.Builder meas = ProtoMessage.Measurement.newBuilder()
                    .setValue(measurement.getValue()).setTimestamp(measurement.getTimestamp());

            try{
                ProtoMessage.Empty response = stub.sendMeasurement(meas.build());
                channel.shutdown();
            }
            catch (StatusRuntimeException e){
                System.out.println("EXCEPTION: Node Offline getting a new one");
                getNodeFromServer();
            }
        }
    }

    private void getNodeFromServer() {

        try{
            Client client=Client.create();
            WebResource restResource = client.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/sensorservice/getnode");

            ClientResponse response = restResource.queryParam("posX",String.valueOf(posX))
                    .queryParam("posY",String.valueOf(posY)).get(ClientResponse.class);

            if(response.getStatus()==200){
                InputStream stream=response.getEntityInputStream();
                ProtoMessage.NodeInfo nodeReceived=ProtoMessage.NodeInfo.parseFrom(IOUtils.toByteArray(stream));

                synchronized (this.nodeInfo){
                    if(this.nodeInfo.getIp()==null || !(this.nodeInfo.getIp().equals(nodeReceived.getIp()))) {

                        this.nodeInfo.setID(nodeReceived.getNodeId());
                        this.nodeInfo.setIP(nodeReceived.getIp());
                        this.nodeInfo.setSensorPort(nodeReceived.getSensorPort());
                    }
                }
            }
            else{
                synchronized (this.nodeInfo){
                    this.nodeInfo.setIP(null);
                }
            }
        }catch (Exception e){
            System.out.println("Exception while getting node from ServerCloud");
        }

    }
}
