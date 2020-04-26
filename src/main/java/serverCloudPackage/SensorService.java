package serverCloudPackage;

import commonPackage.EdgeNodeInfo;
import protosPackage.ProtoMessage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.awt.*;
import java.io.InputStream;

@Path("sensorservice")
public class SensorService {
    
    @Path("getnode")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getNearestNode(@QueryParam("posX") String xPos,@QueryParam("posY") String yPos){

		if(Integer.valueOf(xPos)<0 || Integer.valueOf(xPos)>99 || Integer.valueOf(yPos)<0 || Integer.valueOf(yPos)>99)
			return Response.status(Status.NOT_ACCEPTABLE).build();

		EdgeNodeInfo result=CityState.getInstance().getNearestNode(new Point(Integer.parseInt(xPos),Integer.parseInt(yPos)));

		if(result==null) //NON CI SONO NODI
			return Response.noContent().build();

		ProtoMessage.NodeInfo.Builder nearestNodeBuilder = ProtoMessage.NodeInfo.newBuilder();
		nearestNodeBuilder.setNodeId(result.getId()).setIp(result.getIp()).setSensorPort(result.getSensorPort()); //AL SENSORE NON INTERESSA NODEPORT E POSIZIONE E NEMMENO ID

		return Response.ok(nearestNodeBuilder.build().toByteArray(),MediaType.APPLICATION_OCTET_STREAM).build();
    }
}
