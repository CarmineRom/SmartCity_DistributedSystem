package edgeNodePackage;

import commonPackage.EdgeNodeInfo;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import protosPackage.NodeServiceGrpc;
import protosPackage.ProtoMessage;
import protosPackage.ProtoNodeService;

import java.util.ArrayList;
import java.util.Random;

public class ElectionThread implements Runnable {

    private String nodeId;
    private boolean firstTime;
    private boolean interrupted;

    public  ElectionThread(String nodeId){

        this.nodeId=nodeId;
        this.firstTime=true;
        this.interrupted=false;
    }

    @Override
    public void run(){
        while(!interrupted){
            try {
                if(firstTime){ //PRIMO AVVIO ATTESA PRESENTAZIONE NODI
                    Manager.getInstance().waitInitialize(); //PERMETTE DI IGNORARE I MESSAGGI DI ELEZIONE
                    firstTime=false;
                }

                //ATTESA ELEZIONE
                Manager.getInstance().getElectionSignal();
                System.out.println("ElectionThread got signal. Start election");

                //INIZIO ELEZIONE
                //PRELIEVO LISTA NODI AGGIORNATA
                ArrayList<EdgeNodeInfo> nodesList = Manager.getInstance().getNodesList();
                boolean stopped=false;
                if(!nodesList.isEmpty()){

                    String higherNode=nodesList.get(nodesList.size()-1).getId();

                    if(higherNode.compareTo(this.nodeId)<0){ //SONO IL NODO CON ID PIU ALTO
                        Manager.getInstance().setCoordinator(this.nodeId);
                        sendVictory();
                    }
                    else{
                        stopped=false;
                        int i=nodesList.size()-1;

                        //CONTATTA TUTTI I NODI CON ID MAGGIORE
                        //ORDINE INVIO MESSAGGIO DI ELEZIONE DECRESCENTE
                        while(i>=0 && nodesList.get(i).getId().compareTo(this.nodeId)>0){
                            EdgeNodeInfo node=nodesList.get(i);
                            ManagedChannel channel = ManagedChannelBuilder.forTarget(node.getIp()+":"+node.getNodePort())
                                    .usePlaintext(true)
                                    .build();

                            NodeServiceGrpc.NodeServiceBlockingStub stub = NodeServiceGrpc.newBlockingStub(channel);
                            ProtoNodeService.Request request = ProtoNodeService.Request.newBuilder().setId(this.nodeId).build();
                            try{
                                ProtoMessage.Empty response = stub.election(request);
                                //SE RISPONDE -> STOP
                                stopped=true;

                            }catch (StatusRuntimeException e){
                                //SE NON RISPONDE -> RIMOZIONE DALLA LISTA NODI
                                Manager.getInstance().removeFromNodesList(node.getId());
                            }

                            channel.shutdown();
                            i--;
                        }

                        if(!stopped){ //NODI CON ID SUPERIORE NON HANNO RISPOSTO O NON ESISTENTI
                            Manager.getInstance().setCoordinator(this.nodeId);
                            sendVictory();
                        }
                        else{
                            //ATTESA MESSAGGIO VITTORIA TEMPORIZZATO
                            //System.out.println("Election Thread WAITING FOR VICTORY");
                            EdgeNodeInfo tmpCoord=Manager.getInstance().getCoordinator(true,10000);
                            //System.out.println("Election Thread TIME EXPIRED");
                        }

                    }
                }
                else{ //NON CI SONO ALTRI NODI NELLA RETE
                    Manager.getInstance().setCoordinator(this.nodeId);
                }
            } catch (InterruptedException e) {
                System.out.println("Election Thread Interrupted");
                interrupted=true;
            }
        }
    }

    private void sendVictory() {
        ArrayList<EdgeNodeInfo> nodesList= null;
        nodesList = Manager.getInstance().getNodesList();
        if(!nodesList.isEmpty() && !(nodesList.get(nodesList.size()-1).getId().compareTo(this.nodeId)>0)){
            for(EdgeNodeInfo node : nodesList){
                try{
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(node.getIp()+":"+node.getNodePort())
                        .usePlaintext(true)
                        .build();

                    NodeServiceGrpc.NodeServiceBlockingStub stub = NodeServiceGrpc.newBlockingStub(channel);
                    ProtoNodeService.Request request = ProtoNodeService.Request.newBuilder().setId(this.nodeId).build();

                    ProtoMessage.Empty response = stub.newCoordinator(request);
                    channel.shutdown();
                }catch (StatusRuntimeException e){
                }
            }
        }

    }
}



