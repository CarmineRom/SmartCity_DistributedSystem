package edgeNodePackage;

import commonPackage.*;

import java.util.*;

public class Manager {

    private static Manager instance;
    private EdgeNodeInfo nodeInfo;
    private HashMap<String,EdgeNodeInfo> edgeNodesList;
    private PriorityQueue<Measurement> sensorMeasurements;
    private HashMap<String,Measurement> nodesStatistics;
    private Measurement globalStat;
    private EdgeNodeInfo coordinatorInfo;
    private Object inElectionDummy;
    private Object initNodesListDummy;
    private boolean initialized;
    private volatile boolean inElection;

    private Manager(){

        this.edgeNodesList=new HashMap<String,EdgeNodeInfo>();
        this.sensorMeasurements=new PriorityQueue<Measurement>();
        this.nodesStatistics=new HashMap<String, Measurement>();
        this.inElectionDummy=new Object();
        this.initNodesListDummy=new Object();
        this.inElection=false;
        this.coordinatorInfo=new EdgeNodeInfo();
        this.globalStat=new Measurement("","",0,0);
    }

    public static Manager getInstance(){

        if(instance==null)
            synchronized(Manager.class) {
                if( instance == null )
                    instance = new Manager();
            }
        return instance;
    }

    public void setNodeInfo(EdgeNodeInfo nodeInfo){ //NON SYNCHRONIZED PERCHE ESEGUITO UNA SOLA VOLTA DA UN SOLO THREAD

        this.nodeInfo=new EdgeNodeInfo(nodeInfo);
    }

    public void initialize(){
        synchronized (initNodesListDummy){
            initialized=true;
            initNodesListDummy.notify();
        }
    }

    public boolean waitInitialize() throws InterruptedException {
        synchronized (initNodesListDummy){
            while(!initialized)
                initNodesListDummy.wait();

            return true;
        }
    }

    public void addToNodesList(EdgeNodeInfo newNode){

        synchronized (inElectionDummy){
            synchronized (this.edgeNodesList){
                this.edgeNodesList.put(newNode.getId(),newNode);
            }
        }
    }

    public ArrayList<EdgeNodeInfo> getNodesList(){

        ArrayList<EdgeNodeInfo> list = new ArrayList<EdgeNodeInfo>();
        synchronized (this.edgeNodesList){

            for(EdgeNodeInfo node : edgeNodesList.values()){
                list.add(new EdgeNodeInfo(node));
            }
        }
        list.sort(null); //ORDINAMENTO CRESCENTE
        return list;
    }

    public void removeFromNodesList(String id){

        synchronized (this.edgeNodesList){

            this.edgeNodesList.remove(id);
        }
    }

    public void setInElection(boolean value) {
        synchronized (inElectionDummy){
            this.inElection=value;
            inElectionDummy.notifyAll();
        }
    }

    public boolean getElectionSignal() throws InterruptedException {
        synchronized (inElectionDummy){
            while(!inElection)
                inElectionDummy.wait();

            return true;
        }
    }

    public void setCoordinator(String newCoordinatorId){

        EdgeNodeInfo copy=null;
        synchronized (this.coordinatorInfo){
            if(newCoordinatorId.equals(this.nodeInfo.getId())){
                this.coordinatorInfo.setID(this.nodeInfo.getId());
                this.coordinatorInfo.setNodePort(this.nodeInfo.getNodePort());
                this.coordinatorInfo.setIP(this.nodeInfo.getIp());
            }
            else{
                synchronized (this.edgeNodesList) {
                    if (!(edgeNodesList.get(newCoordinatorId) == null)) {
                        copy = edgeNodesList.get(newCoordinatorId);
                        this.coordinatorInfo.setID(copy.getId());
                        this.coordinatorInfo.setNodePort(copy.getNodePort());
                        this.coordinatorInfo.setIP(copy.getIp());
                    }
                }
            }

            inElection=false;
            this.coordinatorInfo.notifyAll();
        }
    }

    public EdgeNodeInfo getCoordinator(boolean waiting,int millis) throws InterruptedException {
        //WAITING=TRUE METTE IL THREAD IN WAIT IN CASO DI ELEZIONE IN CORSO

        EdgeNodeInfo copy;
        if(waiting){
            synchronized (this.coordinatorInfo){
                while(inElection || this.coordinatorInfo.getId()==null){ //SECONDA CONDIZIONE POSSIBILE IN ENTRATA
                    if(millis>0) {
                        this.coordinatorInfo.wait(millis); //ELECTIONTHREAD
                        if(inElection)
                            return null;
                    }
                    else
                        this.coordinatorInfo.wait(); //SENDERMEASTHREAD
                }
                copy = new EdgeNodeInfo(this.coordinatorInfo);
                return copy;
            }
        }
        else{
            synchronized (this.coordinatorInfo){  //hello() GrpC
                if(!inElection){
                    copy = new EdgeNodeInfo(this.coordinatorInfo);
                    return copy;
                }
                else
                    return null;
            }
        }
    }

    public void addMeasurement(Measurement newMeasurement) {

        synchronized (sensorMeasurements){
            sensorMeasurements.add(newMeasurement);
            if(sensorMeasurements.size()>=40)
                sensorMeasurements.notify();
        }
    }

    public ArrayList<Measurement> getMeasurements() throws InterruptedException {

        ArrayList<Measurement> measurements=new ArrayList<>();
        Object[] overlap;
        synchronized (sensorMeasurements){

            while(sensorMeasurements.size()<40){

                sensorMeasurements.wait();
            }

            //RIMOZIONE PRIMI VENTI
            for (int i=0; i<20; i++)
            {
                measurements.add(sensorMeasurements.poll());
            }
            overlap=sensorMeasurements.toArray(); //SOLO LETTURA DEI PROSSIMI VENTI QUINDI COPIA
            sensorMeasurements.notify();
        }

        Arrays.sort(overlap); //SORT PERCHE toArray() NON GARANTISCE L'ORDINE

        for(int i=0;i<20;i++)
        {
            Measurement m = (Measurement) overlap[i];
            measurements.add(m);
        }
        measurements.sort(null);
        return measurements;
    }

    public void addStatistic(Measurement stat){

        synchronized (nodesStatistics){
            if(nodesStatistics.containsKey(stat.getId())){
                nodesStatistics.replace(stat.getId(),stat); //LA STATISTICA PIU NUOVA DI UN NODO RIMPIAZZA QUELLA VECCHIA
            }
            else
                nodesStatistics.put(stat.getId(),stat);

            nodesStatistics.notify();
        }
    }

    public ArrayList<Measurement> getNodesStatistics() throws InterruptedException {

        ArrayList<Measurement> statistics=new ArrayList<>();
        synchronized (nodesStatistics){

            while(nodesStatistics.isEmpty())
                nodesStatistics.wait();

            statistics.addAll(nodesStatistics.values());
            nodesStatistics.clear();
        }

        return statistics;
    }

    public void updateGlobalStat(Measurement newStat){

        synchronized (this.globalStat){
            this.globalStat = new Measurement("","",newStat.getValue(),newStat.getTimestamp());
        }
    }

    public Measurement getGlobalStat(){

        synchronized (this.globalStat){
            Measurement res = new Measurement("","",this.globalStat.getValue(),this.globalStat.getTimestamp());
            return res;
        }
    }
}
