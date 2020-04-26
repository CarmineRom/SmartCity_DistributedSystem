package edgeNodePackage;

import commonPackage.Measurement;
import protosPackage.SensorServiceGrpc;
import protosPackage.ProtoMessage;
import io.grpc.stub.StreamObserver;

public class SensorGrpcImpl extends SensorServiceGrpc.SensorServiceImplBase {

    @Override
    public void sendMeasurement(ProtoMessage.Measurement measurement, StreamObserver<ProtoMessage.Empty> responseStreamObserver){

        Manager.getInstance().addMeasurement(new Measurement("","",measurement.getValue(),measurement.getTimestamp()));

        ProtoMessage.Empty.Builder response=ProtoMessage.Empty.newBuilder();
        responseStreamObserver.onNext(response.build());
        responseStreamObserver.onCompleted();
    }
}
