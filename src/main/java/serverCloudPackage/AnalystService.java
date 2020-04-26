package serverCloudPackage;

import commonPackage.EdgeNodeInfo;
import commonPackage.Measurement;
import protosPackage.ProtoMessage;
import protosPackage.ProtoMessage.StatsMessage;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Path("analystservices")
public class AnalystService {
    
    @Context
	protected UriInfo uriInfo;

    @Path("statistics")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getStats(@QueryParam("nodeid") String nodeId,@QueryParam("n") int n, @QueryParam("average") boolean average){	

		if(!uriInfo.getQueryParameters().containsKey("n"))
			return Response.status(Status.BAD_REQUEST).entity("Parameter n is missing").build();


		if(n<1)
			return Response.status(Status.BAD_REQUEST).entity("Parameter n should be greater than 0").build();

		StatsMessage.Builder responseBuilder= StatsMessage.newBuilder();
		StatsMessage.StatList.Builder statListBuilder = StatsMessage.StatList.newBuilder();
		StatsMessage.NodeStatistics.Builder nodeStatBuilder = StatsMessage.NodeStatistics.newBuilder();
		StatsMessage.MeasList.Builder measListBuilder = StatsMessage.MeasList.newBuilder();
		ProtoMessage.Measurement.Builder measBuilder = ProtoMessage.Measurement.newBuilder();

		if(nodeId==null){ //STATISTICHE GLOBALI E LOCALI

			if(average==true){ //SOLO GLOBALI CON MEDIA

				MeanDeviation result = Statistics.getInstance().getAverageGlobal(n);

				if(result.mean==0 && result.deviation==0)
					return Response.noContent().build();


				responseBuilder.setAverage(result.mean).setDeviation(result.deviation);
				responseBuilder.setLocalStats(statListBuilder.build());
				responseBuilder.setGlobalStats(measListBuilder.build());

				return Response.ok(responseBuilder.build().toByteArray(),MediaType.APPLICATION_OCTET_STREAM).build();
			}

			//GLOBALI E LOCALI SENZA MEDIA
			//UNICA CHIAMATA PER ENTRAMBE LE STATISTICHE
			HashMap<String, ArrayList<Measurement>> result=Statistics.getInstance().getAllMeasurements(n);

			if (result.isEmpty())
				return Response.noContent().build();

			ArrayList<Measurement> globalStats = result.get("global00");
			for(Measurement measurement : globalStats){

				measBuilder.setTimestamp(measurement.getTimestamp()).setValue(measurement.getValue());
				measListBuilder.addMeasurements(measBuilder.build());
			}

			responseBuilder.setGlobalStats(measListBuilder.build());
			measListBuilder.clear();
			result.remove("global00");

			for(Map.Entry<String, ArrayList<Measurement>> element : result.entrySet()){

				nodeStatBuilder.setId(element.getKey());

				for(Measurement measurement : element.getValue()){

					measBuilder.setTimestamp(measurement.getTimestamp()).setValue(measurement.getValue());
					measListBuilder.addMeasurements(measBuilder.build());
				}

				nodeStatBuilder.setMeasList(measListBuilder.build());
				statListBuilder.addStatistics(nodeStatBuilder.build());

				measListBuilder.clear();
				nodeStatBuilder.clear();
			}

			responseBuilder.setLocalStats(statListBuilder.build());

			return Response.ok(responseBuilder.build().toByteArray(),MediaType.APPLICATION_OCTET_STREAM).build();
		}

		//STATISTICHE SOLO LOCALI
		if(average==true){

			MeanDeviation result = Statistics.getInstance().getAverageNode(nodeId, n);

			if(result==null)
				return Response.status(Status.NOT_FOUND).entity("The node '"+nodeId+"' does not exists or has gone offline recently").build();

			if(result.mean==0 && result.deviation==0)
				return Response.noContent().build();

			responseBuilder.setAverage(result.mean).setDeviation(result.deviation);
			responseBuilder.setLocalStats(statListBuilder.build());
			responseBuilder.setGlobalStats(measListBuilder.build());

			return Response.ok(responseBuilder.build().toByteArray(),MediaType.APPLICATION_OCTET_STREAM).build();
		}

		//LOCALE SENZA MEDIA
		ArrayList<Measurement> result =  Statistics.getInstance().getNodeMeasures(nodeId, n);

		if(result==null){
			return Response.status(Status.NOT_FOUND).entity("The node '"+nodeId+"' does not exists or has gone offline recently").build();
		}

		if(result.isEmpty()){
			return Response.noContent().build();
		}

		for(Measurement measurement : result){

			measBuilder.setTimestamp(measurement.getTimestamp()).setValue(measurement.getValue());
			measListBuilder.addMeasurements(measBuilder.build());
	 	}

	 	nodeStatBuilder.setMeasList(measListBuilder.build());
	 	statListBuilder.addStatistics(nodeStatBuilder.build());
	 	responseBuilder.setLocalStats(statListBuilder.build());

	 	return Response.ok(responseBuilder.build().toByteArray(),MediaType.APPLICATION_OCTET_STREAM).build();
    }
        
    @Path("citystate")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getCityState(){	

		ArrayList<EdgeNodeInfo> cityState = CityState.getInstance().getState();
		if(cityState.isEmpty())
			return Response.noContent().build();

		ProtoMessage.NodesList.Builder nodesListBuilder = ProtoMessage.NodesList.newBuilder();
		ProtoMessage.NodeInfo.Builder nodeBuilder = ProtoMessage.NodeInfo.newBuilder();

		for(EdgeNodeInfo node : cityState){

			nodeBuilder.setNodeId(node.getId());
			nodeBuilder.setPosX(node.getPosition().x);
			nodeBuilder.setPosY(node.getPosition().y);

			nodesListBuilder.addNodes(nodeBuilder.build());
			nodeBuilder.clear();
		}

		return Response.ok(nodesListBuilder.build().toByteArray(),MediaType.APPLICATION_OCTET_STREAM).build();
	}
}
