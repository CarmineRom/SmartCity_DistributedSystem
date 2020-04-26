package edgeNodePackage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import commonPackage.EdgeNodeInfo;
import io.grpc.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import protosPackage.NodeServiceGrpc;
import protosPackage.ProtoMessage;
import protosPackage.ProtoNodeService;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

public class NodeStarter {
    
    private static final String SERVER_CLOUD_IP = "localhost";
    private static final int SERVER_CLOUD_PORT = 1337;
    private static BufferedReader localInput;
    private static String IP;
    private static String ID;
    private static int nodePort;
    private static int sensorPort;
    private static Point position;

    public static void main(String[] args) throws IOException {

        localInput=new BufferedReader(new InputStreamReader(System.in));
        position=new Point();

        //SETTAGGIO PARAMETRI
        System.out.println("NODE SETTINGS");
        Random random=new Random();
        System.out.println("IP (Last field)");
        String lastField=localInput.readLine();
        int last=checkIp(lastField);
        System.out.println("Choose how to generate id:");
        System.out.println("1. Manual");
        System.out.println("2. Random");
        if((localInput.readLine()).equals("1")){
            System.out.println("Insert ID:");
            ID=localInput.readLine();
        }
        else
            ID=getSaltString();

        while(ID.length()<1){
            System.out.println("Id cannot be empty. Insert a new one: ");
            ID=localInput.readLine();
        }

        System.out.println("ID generated: "+ID);

        nodePort=3000+last;
        sensorPort=4000+last;

        //SCELTA GENERATORE POSIZIONE
        System.out.println("Choose how to generate position:");
        System.out.println("1. Manual");
        System.out.println("2. Random");

        if((localInput.readLine()).equals("1")){
            System.out.println("X:");
            position.x=Integer.parseInt(localInput.readLine());
            System.out.println("Y:");
            position.y=Integer.parseInt(localInput.readLine());
        }
        else{
            position=new Point(random.nextInt(100), random.nextInt(100));
            System.out.println("Position generated: ("+position.x+","+position.y+")");
            System.out.println();
        }

        Client restClient = Client.create();

        //RICHIESTA ACCESSO
        WebResource restResource = restClient.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/nodeservices/addnode");
        ProtoMessage.NodeInfo.Builder accessRequestBuilder = ProtoMessage.NodeInfo.newBuilder();

        boolean entered=false;
        int positionAttempts=0; //tentativi
        System.out.println("Access request to http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/nodeservices/addnode");

        //DO-WHILE -->10 Tentativi
        do {
            try {
                //AccessRequest
                accessRequestBuilder.setNodeId(ID).setIp(IP).setPosX(position.x).setPosY(position.y).setNodePort(nodePort).setSensorPort(sensorPort);
                ClientResponse accessResponse;

                accessResponse= restResource.type(MediaType.APPLICATION_OCTET_STREAM)
                        .post(ClientResponse.class,accessRequestBuilder.build().toByteArray());

                positionAttempts++;

                System.out.println("Attempt: "+positionAttempts);
                if (accessResponse.getStatus() != 201) { //ERROR

                    System.out.println("SERVER RESPONSE: "+accessResponse.getStatus()+" - "+accessResponse.getStatusInfo());

                    if(accessResponse.getStatus()/100==5){ //5xx-SERVERSIDE ERROR
                        System.out.println("Servers has encountered an internal problem while processing your request. Insert '1' to retry or '0' to exit.");
                        int i = Integer.parseInt(localInput.readLine());
                        if(i==0)
                            System.exit(0);
                    }

                    if(accessResponse.getStatus()/100==4){ //4xx-CLIENT ERROR

                        if(accessResponse.getStatus()==406){  //406- NOT_ACCEPTABLE

                            String reason=accessResponse.getEntity(String.class);
                            System.out.println("Reason: "+reason);
                            System.out.println();
                            switch (reason) {
                                case "INVALID POSITION":
                                    if(positionAttempts<10){
                                        System.out.println("The specified position is not allowed");
                                        System.out.println("Choose how to generate position:");
                                        System.out.println("1. Manual");
                                        System.out.println("2. Random");

                                        String t=localInput.readLine();
                                        if(t.equals("1")){
                                            System.out.println("X:");
                                            position.x=Integer.parseInt(localInput.readLine());
                                            System.out.println("Y:");
                                            position.y=Integer.parseInt(localInput.readLine());
                                        }
                                        else{
                                            position=new Point(random.nextInt(100), random.nextInt(100));
                                            System.out.println("Position generated: ("+position.x+","+position.y+")");
                                        }
                                    }
                                    break;

                                case "ID IN USE OR NOT ALLOWED":
                                    System.out.println("The specified Id is already in use or not allowed, insert a new Id:");
                                    ID=localInput.readLine();
                                    break;

                                case "OUT OF CITY":
                                    System.out.println("The specified position is outside the city, insert a new position with X and Y in [0,99]:");
                                    System.out.println("X:");
                                    position.x=Integer.parseInt(localInput.readLine());
                                    System.out.println("Y:");
                                    position.y=Integer.parseInt(localInput.readLine());
                                    break;

                                case "FULL NODES":
                                    System.out.println("The Network has no free good positions for a new node or it has reached the maximum number of connected nodes.");
                                    System.out.println("The application will be closed.");
                                    System.in.read();
                                    System.exit(0);
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                }
                else{ //ACCESSO ALLA RETE ESEGUITO CON SUCCESSO

                    System.out.println("Server Response: "+accessResponse.getStatus()+" "+accessResponse.getStatusInfo());
                    System.out.println("Access to the network successful");
                    System.out.println();
                    entered=true;
                }
            }catch (ClientHandlerException e){

                System.err.println("Exception in Access Request : Connection error ");
                System.err.println("ServerCloud could be unavailable");
                System.out.println("Press 0 to exit or any other button to retry");
                String i = localInput.readLine();
                if(i.equals("0"))
                    System.exit(0);
            }
            catch (Exception e ){

                System.err.println("Exception in Access Request: Generic Exception:");
                System.err.println(e.toString());
                System.out.println("Press 0 to exit or any other button to retry");
                String i = localInput.readLine();
                if(i.equals("0"))
                    System.exit(0);
            }
        } while (!entered && positionAttempts<10);

        if(!entered){
            System.out.println("Maximum number of attempts reached, the application will be closed");
            System.in.read();
            System.exit(0);
        }

        //SETTAGGIO PARAMETRI NODO NELLA CLASSE MANAGER E ATTIVAZIONE THREADS DI GESTIONE
        Manager.getInstance().setNodeInfo(new EdgeNodeInfo(position,ID,IP,nodePort,sensorPort));
        Thread coordinatorThread = new Thread(new CoordinatorThread(SERVER_CLOUD_IP,SERVER_CLOUD_PORT));
        Thread electionThread = new Thread(new ElectionThread(ID));
        Thread senderMeasThread=new Thread(new SenderMeasThread(ID));
        coordinatorThread.start();
        electionThread.start();
        senderMeasThread.start();

        //ATTIVAZIONE SERVER GRPC PER RICEVERE MISURAZIONI DAI SENSORI DURANTE LA PRESENTAZIONE (NON TUTTI I NODI MI CONOSCONO)
        Server grpcNodeServer = ServerBuilder.forPort(nodePort).addService(new NodeGrpcImpl(ID)).build();
        Server grpcSensorServer = ServerBuilder.forPort(sensorPort).addService(new SensorGrpcImpl()).build();
        grpcNodeServer.start();
        grpcSensorServer.start();

        // RICHIESTA LISTA NODI
        restResource = restClient.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/nodeservices/citystate");
        ClientResponse getListResponse= restResource.type(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);

        if(getListResponse.getStatus()==200){

            InputStream responseStream = getListResponse.getEntityInputStream();
            ProtoMessage.NodesList cityState = ProtoMessage.NodesList.parseFrom(IOUtils.toByteArray(responseStream));
            List<ProtoMessage.NodeInfo> list = new ArrayList<>();
            list.addAll(cityState.getNodesList());

            if(list.size()==1){ //UNICO NODO NELLA RETE - SONO COORDINATORE
                System.out.println("I'm alone so I'm COORDINATOR");
                Manager.getInstance().setCoordinator(ID);
                Manager.getInstance().initialize();
            }
            else{ //SONO PRESENTI ALTRI NODI NELLA RETE - PRESENTAZIONE E RICERCA COORDINATORE

                list.sort((a,b) -> a.getNodeId().compareTo(b.getNodeId())); //CRESCENTE
                System.out.println("Nodes already in the network:");
                System.out.println(cityState.toString());

                String coordinatorId=null;
                boolean coordinatorFound = false;
                for(ProtoMessage.NodeInfo node : list){

                    if(!node.getNodeId().equals(ID)){

                        //PRESENTAZIONE -> hello()
                        ManagedChannel channel = ManagedChannelBuilder.forTarget(node.getIp()+":"+node.getNodePort())
                                .usePlaintext(true)
                                .build();
                        NodeServiceGrpc.NodeServiceBlockingStub stub = NodeServiceGrpc.newBlockingStub(channel);
                        ProtoNodeService.HelloRequest request = ProtoNodeService.HelloRequest.newBuilder()
                                .setId(ID).setIp(IP).setPort(nodePort).build();
                        try{
                            System.out.println("Sending hello() to " +node.getNodeId());

                            ProtoNodeService.Response response = stub.hello(request);

                            //SE RISPONDE VIENE AGGIUNTO ALLA LISTA
                            Manager.getInstance().addToNodesList(new EdgeNodeInfo(node.getNodeId(),
                                    node.getIp(), node.getNodePort()));

                            if(response.getState()==2){
                                coordinatorFound=true;
                                coordinatorId=response.getId();
                            }
                        }catch (StatusRuntimeException e){
                            System.out.println("No answer hello() from: "+node.getNodeId());
                        }

                        channel.shutdown();
                    }

                }
                System.out.println("Coordinator found?: "+coordinatorFound+" ___ coordinatorId?: "+coordinatorId);

                if(coordinatorFound)
                    Manager.getInstance().setCoordinator(coordinatorId);
                else
                    Manager.getInstance().setInElection(true);

                Manager.getInstance().initialize();
            }
        }
        else //ERRORE DURANTE LA RICHIESTA (STATUS DELLA RESPONSE != 200)
            System.out.println("Error: "+getListResponse.getStatus()+" "+getListResponse.getStatusInfo());

        //USCITA IN MODO CONTROLLATO:
        //INFORMO IL SERVER DELL'USCITA
        //SPENGO I SERVER GRPC
        //INTERROMPO SENDERMEASTHREAD
        //INTERROMPO COORDINATORTHREAD
        //INTERROMPO ELECTIONTHREAD

        System.out.println();
        System.out.println("Hit return to leave the Network and shutdown the node");
        System.out.println();
        System.in.read();

        restResource = restClient.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/nodeservices/removenode").queryParam("nodeId",ID);
        ClientResponse deleteResponse= restResource.type(MediaType.APPLICATION_OCTET_STREAM).delete(ClientResponse.class);
        while(!(deleteResponse.getStatus()==200)){ //RICHIESTA RIMOZIONE FIN QUANDO VIENE ACCETTATA
            deleteResponse= restResource.type(MediaType.APPLICATION_OCTET_STREAM).delete(ClientResponse.class);
            System.out.println("Bad delete answer");
        }
        System.out.println("Node removed from Server");

        grpcNodeServer.shutdown();
        grpcSensorServer.shutdown();
        senderMeasThread.interrupt();
        coordinatorThread.interrupt();
        electionThread.interrupt();

        System.out.println("Closing");
        localInput.close();
        System.exit(0);
    }

    private static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // lunghezza della stringa generata
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    private static int checkIp(String lastField){
        String tempIp="127.0.1."+lastField;
        boolean ipCorrect=InetAddressValidator.getInstance().isValid(tempIp);
        String last=lastField;
        while(!ipCorrect){
            try {
                System.out.println("The specified IP is not valid, insert a new one (last field):");
                last=localInput.readLine();
                tempIp="127.0.1."+last;
                ipCorrect=InetAddressValidator.getInstance().isValid(tempIp);
            }
            catch (IOException e){
                System.out.println("Error while getting input");
            }
        }
        IP=tempIp;
        return Integer.valueOf(last);
    }
}
