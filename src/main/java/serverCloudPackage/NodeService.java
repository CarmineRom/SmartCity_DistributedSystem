package serverCloudPackage;

import com.google.protobuf.InvalidProtocolBufferException;
import commonPackage.EdgeNodeInfo;
import commonPackage.Measurement;
import org.apache.commons.io.IOUtils;
import protosPackage.ProtoMessage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

@Path("nodeservices")
public class NodeService {
    
    @Path("citystate")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getCityState(){	
	
        ArrayList<EdgeNodeInfo> cityState = CityState.getInstance().getState();

        //INUTILE PERCHE' LA RICHIESTA VIENE ESEGUITA DA UN NODO CHE QUINDI E' SICURAMENTE GIA PRESENTE
        if(cityState.isEmpty())
            return Response.noContent().build();

        ProtoMessage.NodesList.Builder nodesListBuilder = ProtoMessage.NodesList.newBuilder();
        ProtoMessage.NodeInfo.Builder nodeBuilder = ProtoMessage.NodeInfo.newBuilder();

        for(EdgeNodeInfo node : cityState){

            nodeBuilder.setNodeId(node.getId());
            nodeBuilder.setIp(node.getIp());
            nodeBuilder.setNodePort(node.getNodePort());

            nodesListBuilder.addNodes(nodeBuilder.build());
        }

        return Response.ok(nodesListBuilder.build().toByteArray(),MediaType.APPLICATION_OCTET_STREAM).build();
    }    
    
    @Path("addstats")
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response addStats(InputStream inputStream) throws InvalidProtocolBufferException, IOException{

        ProtoMessage.StatsMessage allStats = ProtoMessage.StatsMessage.parseFrom(IOUtils.toByteArray(inputStream));
        ProtoMessage.Measurement globalStat = allStats.getGlobalStats().getMeasurements(0);

        Measurement newGlobal = new Measurement("global00", "", globalStat.getValue(), globalStat.getTimestamp());

        HashMap<String, Measurement> newLocal = new HashMap<String, Measurement>();

        for(ProtoMessage.StatsMessage.NodeStatistics nodeStat : allStats.getLocalStats().getStatisticsList()){

            ProtoMessage.Measurement measurement = nodeStat.getMeasList().getMeasurements(0);
            newLocal.put(nodeStat.getId(), new Measurement(nodeStat.getId(), "", measurement.getValue(), measurement.getTimestamp()));
        }

        Statistics.getInstance().addMeasurements(newLocal, newGlobal);

        return Response.status(Status.CREATED).build();
    }
    
    
    @Path("addnode")
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response addNode(InputStream inputStream) throws InvalidProtocolBufferException, IOException{

        ProtoMessage.NodeInfo newNode = ProtoMessage.NodeInfo.parseFrom(IOUtils.toByteArray(inputStream));
        int result = CityState.getInstance().addNode(new Point(newNode.getPosX(), newNode.getPosY()), newNode.getNodeId(), newNode.getIp(), newNode.getNodePort(), newNode.getSensorPort());

        if(result==0){
            Statistics.getInstance().addNode(newNode.getNodeId());
            return Response.status(Status.CREATED).entity(String.valueOf(result)).build();
        }
      
        String entity;
        switch (result) {
      	    case 1:
      	        entity="INVALID POSITION";
                break;
      	    case 2:
      	        entity="ID IN USE OR NOT ALLOWED";
                break;
      	    case 3:
                entity="OUT OF CITY";
                break;
            case 4:
                entity="FULL NODES";
    	        break;
            default:
                entity="";
    	        break;
        }
        return Response.status(Status.NOT_ACCEPTABLE).entity(entity).build();
    }
  
    @Path("removenode")
    @DELETE
    public Response removeNode(@QueryParam("nodeId") String nodeId){

        CityState.getInstance().removeNode(nodeId);
        Statistics.getInstance().removeNode(nodeId);
      
        return Response.ok().build();
    }
}
