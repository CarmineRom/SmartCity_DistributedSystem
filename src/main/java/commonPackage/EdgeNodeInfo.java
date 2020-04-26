package commonPackage;

import java.awt.*;

public class EdgeNodeInfo implements Comparable<EdgeNodeInfo>{
    
    private Point position;
    private String ID;
    private String IP;
    private int nodePort;
    private int sensorPort;
    
    public EdgeNodeInfo(){}

    public EdgeNodeInfo(String id, String ip, int nodePort){

        this.ID=id;
        this.IP=ip;
        this.nodePort=nodePort;
    }

    public EdgeNodeInfo(EdgeNodeInfo edgeNodeInfo){

        this.ID=edgeNodeInfo.getId();
        this.IP=edgeNodeInfo.getIp();
        this.nodePort=edgeNodeInfo.getNodePort();
        this.sensorPort=edgeNodeInfo.getSensorPort();
        if(edgeNodeInfo.position!=null)
            this.position=new Point(edgeNodeInfo.position.x,edgeNodeInfo.position.y);
    }

    public EdgeNodeInfo(int x, int y, String id, String ip, int nodePort, int sensorPort) {
        this.position=new Point(x, y);
        this.ID=id;
        this.IP=ip;
        this.nodePort=nodePort;
        this.sensorPort=sensorPort;
    }
    
    public EdgeNodeInfo(Point position, String id, String ip, int nodePort, int sensorPort) {
        this.position=position;
        this.ID=id;
        this.IP=ip;
        this.nodePort=nodePort;
        this.sensorPort=sensorPort;
    }
    public void setID(String id){
        this.ID=id;
    }

    public void setIP(String ip){
        this.IP=ip;
    }

    public void setNodePort(int nodePort){
        this.nodePort=nodePort;
    }

    public void setSensorPort(int sensorPort){
        this.sensorPort=sensorPort;
    }

    public Point getPosition() {
	    return position;
    }
    
    public String getId() {	
	    return ID;
    }
    
    public String getIp() {
	    return IP;
    }
    
    public int getNodePort() {
	    return nodePort;
    }
    
    public int getSensorPort() {
	    return sensorPort;
    }

    @Override
    public int compareTo(EdgeNodeInfo other)
    {
        return this.ID.compareTo(other.getId());
    }

}
