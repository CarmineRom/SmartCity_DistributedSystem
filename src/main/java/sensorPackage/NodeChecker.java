package sensorPackage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import commonPackage.EdgeNodeInfo;
import org.apache.commons.io.IOUtils;
import protosPackage.ProtoMessage;

import java.io.InputStream;

public class NodeChecker implements Runnable {

    EdgeNodeInfo nodeInfo;
    int posX;
    int posY;
    static final String SERVER_CLOUD_IP = "localhost";
    static final int SERVER_CLOUD_PORT = 1337;

    public NodeChecker(int posX, int posY, EdgeNodeInfo nodeInfo){
        this.nodeInfo=nodeInfo;
        this.posX=posX;
        this.posY=posY;
    }

    @Override
    public void run(){

        while(true){
            try{
                Thread.sleep(10000); //INTERVALLO DI 10 SECONDI
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
            }
            catch (InterruptedException e){
                System.out.println("NodeChecker interrupted");
            }
            catch (Exception e){
                System.out.println("Exception in NodeChecker. ServerCloud Unavailable?");
            }
        }
    }
}
