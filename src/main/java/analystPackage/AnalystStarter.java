package analystPackage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.io.IOUtils;
import protosPackage.ProtoMessage;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalystStarter {

    static final String SERVER_CLOUD_IP = "localhost";
    static final int SERVER_CLOUD_PORT = 1337;
    static BufferedReader localInput;

    public static void main(String args[]){

        System.out.println("Welcome to the Analyst Application");

        localInput= new BufferedReader(new InputStreamReader(System.in));
        boolean exit=false;

        while(!exit){

            System.out.println("______________MAIN MENU______________");
            System.out.println();
            System.out.println("1. Get the state of the city");
            System.out.println("2. Get last n measurements (globals and locals)");
            System.out.println("3. Get last n measurements of a specific node");
            System.out.println("4. Get the average of the last n global measurements");
            System.out.println("5. Get the average of the last n measurements of a specific node");
            System.out.println("6. Exit");
            boolean goodchoice=false;
            String choice=null;
            do {
                try {
                    choice=localInput.readLine();
                    if(choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4") || choice.equals("5") || choice.equals("6"))
                        goodchoice=true;
                    else
                        System.out.println("Enter a valid number");
                } catch (IOException e) {
                    System.out.println("Error while getting choice. Retry");
                }
            }
            while (!goodchoice);

            switch (choice){
                case "1":
                    exit=getState();
                    break;
                case "2":
                    exit=getAllStatistics();
                    break;
                case "3":
                    exit=getNodeStatistics();
                    break;
                case "4":
                    exit=getAverage(false);
                    break;
                case "5":
                    exit=getAverage(true);
                    break;
                case "6":
                    exit=true;
                    break;
                default:
                    break;
            }
        }
    }

    private static boolean getState() {
        Client client=Client.create();
        WebResource restResource = client.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/analystservices/citystate");
        ClientResponse response= restResource.type(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);

        if(response.getStatus()==204){
            System.out.println("There is no node connected. The Network is Empty");
            return getExit();
        }
        else if(response.getStatus()==200){
            InputStream responseStream = response.getEntityInputStream();
            ProtoMessage.NodesList cityState= null;
            try {
                cityState = ProtoMessage.NodesList.parseFrom(IOUtils.toByteArray(responseStream));
            } catch (IOException e) {
                System.out.println("Error while parsing ProtoMessage");
                return getExit();
            }
            List<ProtoMessage.NodeInfo> nodesList = new ArrayList<>();
            nodesList.addAll(cityState.getNodesList());
            Collections.sort(nodesList,(a, b) -> a.getNodeId().compareTo(b.getNodeId()));
            //Collections.sort(nodesList,null);
            System.out.println("____STATE OF THE CITY____");
            System.out.println("Number of nodes: "+nodesList.size());
            System.out.println();
            for(ProtoMessage.NodeInfo nodeInfo : nodesList){
                System.out.println("ID: "+nodeInfo.getNodeId());
                System.out.println("posX: "+nodeInfo.getPosX());
                System.out.println("posY: "+nodeInfo.getPosY());
                System.out.println();
            }

            System.out.println();
        }
        else{
            System.out.println("Error: "+response.getStatus()+". Reason: "+response.getStatusInfo());
        }

        return getExit();
    }

    private static boolean getAllStatistics(){

        System.out.println("Insert the number of statistics to get");
        String n="0";
        try {
            n=localInput.readLine();
        } catch (IOException e) {
            System.out.println("Exception while getting input");
        }

        Client client=Client.create();
        WebResource restResource = client.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/analystservices/statistics")
                .queryParam("n",n);

        ClientResponse response= restResource.type(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);
        if(response.getStatus()==204){
            System.out.println();
            System.out.println("There are still no statistics available");
            //System.out.println(response.getStatus()+" "+response.getEntity(String.class));

            return getExit();
        }
        else if (response.getStatus()/100==4){
            System.out.println();
            System.out.println(response.getStatus()+" "+response.getStatusInfo()+" - "+response.getEntity(String.class));
            return getExit();
        }
        else if(response.getStatus()/100==5){
            System.out.println();
            System.out.println(response.getStatus()+" "+response.getStatusInfo());
            System.out.println("Servers has encountered an internal problem while processing your request.");
            return getExit();
        }

        ProtoMessage.StatsMessage statistics=null;
        try {
            statistics = ProtoMessage.StatsMessage.parseFrom(IOUtils.toByteArray(response.getEntityInputStream()));
        } catch (IOException e) {
            System.out.println("Error while parsing ProtoMessage");
        }

        System.out.println("Global Statistics:");
        System.out.println(statistics.getGlobalStats());
        System.out.println();
        System.out.println("Local Statistics:");
        System.out.println(statistics.getLocalStats());

        return getExit();
    }

    private static boolean getNodeStatistics(){

        String n="0";
        String nodeId="";
        try{
            System.out.println("Insert the number of statistics to get");
            n=localInput.readLine();
            System.out.println("Insert the id of the node");
            nodeId=localInput.readLine();
        }catch (IOException e){
            System.out.println("Exception while getting input");
        }

        Client client=Client.create();
        WebResource restResource = client.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/analystservices/statistics")
                .queryParam("n",n).queryParam("nodeid",nodeId);

        ClientResponse response= restResource.type(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);

        if(response.getStatus()==204){
            System.out.println();
            System.out.println("There are still no statistics available for the specified node");

            return getExit();
        }
        else if (response.getStatus()/100==4){
            System.out.println();
            System.out.println(response.getStatus()+" "+response.getStatusInfo()+" - "+response.getEntity(String.class));

            return getExit();
        }
        else if(response.getStatus()/100==5){
            System.out.println();
            System.out.println("Servers has encountered an internal problem while processing your request.");
            System.out.println("Response: "+response.getStatus()+" "+response.getStatusInfo()+" - "+response.getEntity(String.class));
            return getExit();
        }

        ProtoMessage.StatsMessage statistics=null;
        try {
            statistics = ProtoMessage.StatsMessage.parseFrom(IOUtils.toByteArray(response.getEntityInputStream()));
        } catch (IOException e) {
            System.out.println("Error while parsing ProtoMessage");
        }

        System.out.println(statistics);

        return getExit();
    }

    private static boolean getAverage(boolean local){

        String nodeId="";
        String n="0";
        try {
            System.out.println("Insert the number of statistics to get");
            n = localInput.readLine();
            if(local){
                System.out.println("Insert the id of the node");
                nodeId = localInput.readLine();
            }
        }
        catch(IOException e){
            System.out.println("Exception while getting input");
        }

        Client client=Client.create();
        WebResource restResource;
        if(local){
            restResource = client.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/analystservices/statistics")
                    .queryParam("n",n).queryParam("average","true").queryParam("nodeid",nodeId);
        }
        else{
            restResource = client.resource("http://"+SERVER_CLOUD_IP+":"+SERVER_CLOUD_PORT+"/analystservices/statistics")
                    .queryParam("n",n).queryParam("average","true");
        }

        ClientResponse response= restResource.type(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);
        if(response.getStatus()==204){
            System.out.println();
            if(local)
                System.out.println("There are still no statistics available for the specified node");
            else
                System.out.println("There are still no global statistics available");

            return getExit();
        }
        else if (response.getStatus()/100==4){
            System.out.println();
            System.out.println(response.getStatus()+" "+response.getStatusInfo()+" - "+response.getEntity(String.class));
            return getExit();
        }
        else if(response.getStatus()/100==5){
            System.out.println();
            System.out.println(response.getStatus()+" "+response.getStatusInfo());
            System.out.println("Servers has encountered an internal problem while processing your request.");
            return getExit();
        }

        ProtoMessage.StatsMessage statistics=null;
        try {
            statistics = ProtoMessage.StatsMessage.parseFrom(IOUtils.toByteArray(response.getEntityInputStream()));
        } catch (IOException e) {
            System.out.println("Error while parsing ProtoMessage");
        }

        System.out.println("Average: "+statistics.getAverage());
        System.out.println("Standard Deviation: "+statistics.getDeviation());

        return getExit();
    }

    private static boolean getExit(){
        System.out.println("1. Return to the Main Menu");
        System.out.println("2. Exit");
        boolean goodchoice=false;
        String choice=null;
        do {
            try {
                choice=localInput.readLine();
                if(choice.equals("1") || choice.equals("2"))
                    goodchoice=true;
                else
                    System.out.println("Enter a valid number");
            } catch (IOException e) {
                System.out.println("Error while getting choice. Retry");
            }
        }
        while (!goodchoice);

        return choice.equals("2");
    }
}
