package edgeNodePackage;

import commonPackage.EdgeNodeInfo;
import commonPackage.Measurement;
import io.grpc.stub.StreamObserver;
import protosPackage.NodeServiceGrpc;
import protosPackage.ProtoMessage;
import protosPackage.ProtoNodeService;


public class NodeGrpcImpl extends NodeServiceGrpc.NodeServiceImplBase {

    String nodeId;

    public NodeGrpcImpl(String nodeId){

        this.nodeId=nodeId;
    }

    @Override
    public void newCoordinator(ProtoNodeService.Request request, StreamObserver<ProtoMessage.Empty> responseStreamObserver){

        ProtoMessage.Empty.Builder response = ProtoMessage.Empty.newBuilder();
        responseStreamObserver.onNext(response.build());
        responseStreamObserver.onCompleted();

        Manager.getInstance().setCoordinator(request.getId());

        System.out.println("New Coordinator elected: "+request.getId());
    }

    @Override
    public void sendStatistic(ProtoMessage.Measurement measurement, StreamObserver<ProtoMessage.Measurement> responseStreamObserver){

        Manager.getInstance().addStatistic(new Measurement(measurement.getId(),"",measurement.getValue(),measurement.getTimestamp()));
        Measurement global=Manager.getInstance().getGlobalStat();
        ProtoMessage.Measurement.Builder response = ProtoMessage.Measurement.newBuilder()
                .setValue(global.getValue()).setTimestamp(global.getTimestamp());
        responseStreamObserver.onNext(response.build());
        responseStreamObserver.onCompleted();
    }

    @Override
    public void election(ProtoNodeService.Request request, StreamObserver<ProtoMessage.Empty> responseStreamObserver){

        Manager.getInstance().setInElection(true);
        System.out.println("Received election Message from: "+request.getId());
        ProtoMessage.Empty.Builder response=ProtoMessage.Empty.newBuilder();
        responseStreamObserver.onNext(response.build());
        responseStreamObserver.onCompleted();
    }

    @Override
    public void hello(ProtoNodeService.HelloRequest request, StreamObserver<ProtoNodeService.Response> responseObserver){

        //ADD IN NODESLIST
        System.out.println("Received hello() from: "+request.getId());
        Manager.getInstance().addToNodesList(new EdgeNodeInfo(request.getId(),request.getIp(),request.getPort()));
        ProtoNodeService.Response.Builder response=ProtoNodeService.Response.newBuilder();
        EdgeNodeInfo coordinatorInfo=null;
        try {
            coordinatorInfo=Manager.getInstance().getCoordinator(false,0 );
        } catch (InterruptedException e) {
            System.out.println("Exception in hello() method Implementation");
            System.out.println(e.toString());
        }

        if(coordinatorInfo==null)//in elezione
            response.setState(0);

        else if(coordinatorInfo.getId()==null)// in entrata
            response.setState(-1);

        else if(coordinatorInfo.getId().equals(this.nodeId)){ //sono coordinatore
            response.setState(2);
            response.setId(this.nodeId);
        }
        else
            response.setState(1);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

}
