package serverCloudPackage;

import commonPackage.Measurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Statistics {

    private static Statistics instance;
    private HashMap<String, ArrayList<Measurement>> localMeasurements;
    private ArrayList<Measurement> globalMeasurements;

    private Statistics(){

        globalMeasurements=new ArrayList<Measurement>();

        localMeasurements=new HashMap<String, ArrayList<Measurement>>();
    }

    public static Statistics getInstance() {

        if(instance==null)
            synchronized(Statistics.class) {
                if( instance == null )
                    instance = new Statistics();
            }

        return instance;
    }

    public void addNode(String newNodeId){

        synchronized (localMeasurements) {

            localMeasurements.put(newNodeId, new ArrayList<Measurement>());
        }

    }

    public void removeNode(String nodeId){

        synchronized (localMeasurements) {

            localMeasurements.remove(nodeId);
        }
    }

    public void addMeasurements(HashMap<String, Measurement> newLocal, Measurement newGlobal) {

        synchronized (globalMeasurements) {
            synchronized (localMeasurements) {

                for(Map.Entry<String, Measurement> newElement : newLocal.entrySet())
                {
                    if(localMeasurements.containsKey(newElement.getKey())){

                        localMeasurements.get(newElement.getKey()).add(0,newElement.getValue()); //INSERIMENTO IN TESTA
                    }
                }

                //INSERIMENTO NUOVE STATISTICHE GLOBALI
                globalMeasurements.add(0,newGlobal);
            }
        }
    }

    //ULTIME N STATISTICHE GLOBALI
    private ArrayList<Measurement> getGlobalMeasurements(int n){

        ArrayList<Measurement> copy=new ArrayList<Measurement>();
        synchronized (globalMeasurements) {

            if(globalMeasurements.size() > n)
                copy.addAll(globalMeasurements.subList(0, n));
            else
                copy.addAll(globalMeasurements);
        }

        return copy;
    }

    //STATISTICHE GLOBALI E LOCALI
    public HashMap<String, ArrayList<Measurement>> getAllMeasurements(int n){

        HashMap<String, ArrayList<Measurement>> copy = new HashMap<String, ArrayList<Measurement>>();
        ArrayList<Measurement> global=new ArrayList<Measurement>();

        synchronized (globalMeasurements) {

            if(!globalMeasurements.isEmpty()){

                synchronized (localMeasurements) {

                    //global00 identifica le statistiche globali nella struttura ritornata con un unico metodo get()
                    copy.put("global00", getGlobalMeasurements(n));
                    copy.putAll(getLocalMeasurements(n));
                }
            }
        }

        return copy;
    }

    //ULTIME N STATISICHE LOCALI
    private HashMap<String, ArrayList<Measurement>> getLocalMeasurements(int n){

        //CREAZIONE COPIA
        HashMap<String, ArrayList<Measurement>> copy = new HashMap<String, ArrayList<Measurement>>();
        //ArrayList<Measurement> copy;
        synchronized (localMeasurements) {

            for(Map.Entry<String,ArrayList<Measurement>> element : localMeasurements.entrySet()){
                ArrayList<Measurement> tempList=new ArrayList<>();
                if(element.getValue().size() > n)
                    //VERIFICARE
                    tempList.addAll(element.getValue().subList(0, n));
                else
                    tempList.addAll(element.getValue());

                copy.put(element.getKey(),tempList);
            }
        }

        return copy;
    }

    //ULTIME N STAITSTICHE DI UN SINGOLO NODO
    public ArrayList<Measurement> getNodeMeasures(String nodeId, int n){

        ArrayList<Measurement> copy=new ArrayList<Measurement>();

        synchronized (localMeasurements) {

            if(localMeasurements.containsKey(nodeId)){
                if(localMeasurements.get(nodeId).size()>n)
                    copy.addAll(localMeasurements.get(nodeId).subList(0,n));
                else
                    copy.addAll(localMeasurements.get(nodeId));
            }
            else
                return null;
        }

        return copy;

    }

    //MEDIA ULTIME N STATISTICHE SPECIFICO NODO
    public MeanDeviation getAverageNode(String nodeId, int n){

        ArrayList<Measurement> copy= getNodeMeasures(nodeId, n);
        if(copy==null)
            return null;

        MeanDeviation result = new MeanDeviation();
        if(!copy.isEmpty())
            result.setMeanDeviation(copy);

        return result;
    }

    //MEDIA ULTIME N STATISTICHE GLOBALI
    public MeanDeviation getAverageGlobal(int n){

        ArrayList<Measurement> copy = getGlobalMeasurements(n);
        MeanDeviation result = new MeanDeviation();

        if(!copy.isEmpty())
            result.setMeanDeviation(copy);

        return result;

    }
}
