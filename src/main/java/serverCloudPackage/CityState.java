package serverCloudPackage;

import commonPackage.EdgeNodeInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CityState {

    private static CityState instance;
    private HashMap<String, EdgeNodeInfo> edgeNodesMap;
    private volatile Boolean fullNodes;

    private CityState(){

        this.edgeNodesMap= new HashMap<String,EdgeNodeInfo>();
        this.fullNodes=false;
    }

    public static CityState getInstance() {

        if(instance==null)
            synchronized(CityState.class) {
                if( instance == null )
                    instance = new CityState();
            }

        return instance;
    }

    //STATO DELLA CITTA
    public ArrayList<EdgeNodeInfo> getState() {

        ArrayList<EdgeNodeInfo> copy= new ArrayList<EdgeNodeInfo>();
        synchronized (edgeNodesMap) {

            copy.addAll(edgeNodesMap.values());
        }

        return copy;
    }

    //RICHIESTA NODO PIU VICINO DA SENSORE
    public EdgeNodeInfo getNearestNode(Point position) {

        double minDist=100*Math.sqrt(2);
        ArrayList<EdgeNodeInfo> copy = new ArrayList<EdgeNodeInfo>();

        synchronized (edgeNodesMap) {

            copy.addAll(edgeNodesMap.values());
        }

        if(!copy.isEmpty()){

            EdgeNodeInfo nearestNode=new EdgeNodeInfo();

            for(EdgeNodeInfo node : copy){

                double dist=node.getPosition().distance(position);
                if(dist<minDist){

                    minDist=dist;
                    nearestNode=node;
                }
            }

            return nearestNode;
        }

        return null;
    }

    //INSERIMENTO NUOVO NODO
    public int addNode(Point position, String id, String ip, int nodePort, int sensorPort) {

        if(position.x < 0 || position.x > 99 || position.y < 0 || position.y > 99)
            return 3;

        if(id.equals("global00"))
            return 2;

        if(!fullNodes) {

            synchronized (edgeNodesMap) {

                if(!edgeNodesMap.containsKey(id)){

                    boolean goodPostion=true;
                    for(EdgeNodeInfo node: edgeNodesMap.values()){

                        if(node.getPosition().distance(position) < 20)
                            goodPostion=false;
                    }

                    if(goodPostion){

                        edgeNodesMap.put(id, new EdgeNodeInfo(position, id, ip, nodePort, sensorPort));

                        if(edgeNodesMap.size()==25)
                            fullNodes=true;

                        return 0; //INSERITO CORRETTAMENTE
                    }
                    else
                        return 1; //POSIZIONE NON CONSENTITA
                }
                else
                    return 2; //ID GIA IN USO
            }
        }
        else return 4;	//QUANTITA MASSIMA DI NODI RAGGIUNTA
    }

    public void removeNode(String nodeId){

        synchronized (edgeNodesMap) {

            edgeNodesMap.remove(nodeId);
        }
    }
}