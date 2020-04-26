package protosPackage;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: ProtoNodeService.proto")
public final class NodeServiceGrpc {

  private NodeServiceGrpc() {}

  public static final String SERVICE_NAME = "NodeService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<protosPackage.ProtoMessage.Measurement,
      protosPackage.ProtoMessage.Measurement> METHOD_SEND_STATISTIC =
      io.grpc.MethodDescriptor.<protosPackage.ProtoMessage.Measurement, protosPackage.ProtoMessage.Measurement>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "NodeService", "sendStatistic"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              protosPackage.ProtoMessage.Measurement.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              protosPackage.ProtoMessage.Measurement.getDefaultInstance()))
          .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("sendStatistic"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<protosPackage.ProtoNodeService.HelloRequest,
      protosPackage.ProtoNodeService.Response> METHOD_HELLO =
      io.grpc.MethodDescriptor.<protosPackage.ProtoNodeService.HelloRequest, protosPackage.ProtoNodeService.Response>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "NodeService", "hello"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              protosPackage.ProtoNodeService.HelloRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              protosPackage.ProtoNodeService.Response.getDefaultInstance()))
          .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("hello"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<protosPackage.ProtoNodeService.Request,
      protosPackage.ProtoMessage.Empty> METHOD_ELECTION =
      io.grpc.MethodDescriptor.<protosPackage.ProtoNodeService.Request, protosPackage.ProtoMessage.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "NodeService", "election"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              protosPackage.ProtoNodeService.Request.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              protosPackage.ProtoMessage.Empty.getDefaultInstance()))
          .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("election"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<protosPackage.ProtoNodeService.Request,
      protosPackage.ProtoMessage.Empty> METHOD_NEW_COORDINATOR =
      io.grpc.MethodDescriptor.<protosPackage.ProtoNodeService.Request, protosPackage.ProtoMessage.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "NodeService", "newCoordinator"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              protosPackage.ProtoNodeService.Request.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              protosPackage.ProtoMessage.Empty.getDefaultInstance()))
          .setSchemaDescriptor(new NodeServiceMethodDescriptorSupplier("newCoordinator"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NodeServiceStub newStub(io.grpc.Channel channel) {
    return new NodeServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NodeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new NodeServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NodeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new NodeServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class NodeServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *USATO DA NODI
     * </pre>
     */
    public void sendStatistic(protosPackage.ProtoMessage.Measurement request,
        io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Measurement> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SEND_STATISTIC, responseObserver);
    }

    /**
     */
    public void hello(protosPackage.ProtoNodeService.HelloRequest request,
        io.grpc.stub.StreamObserver<protosPackage.ProtoNodeService.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_HELLO, responseObserver);
    }

    /**
     */
    public void election(protosPackage.ProtoNodeService.Request request,
        io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ELECTION, responseObserver);
    }

    /**
     */
    public void newCoordinator(protosPackage.ProtoNodeService.Request request,
        io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_NEW_COORDINATOR, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_SEND_STATISTIC,
            asyncUnaryCall(
              new MethodHandlers<
                protosPackage.ProtoMessage.Measurement,
                protosPackage.ProtoMessage.Measurement>(
                  this, METHODID_SEND_STATISTIC)))
          .addMethod(
            METHOD_HELLO,
            asyncUnaryCall(
              new MethodHandlers<
                protosPackage.ProtoNodeService.HelloRequest,
                protosPackage.ProtoNodeService.Response>(
                  this, METHODID_HELLO)))
          .addMethod(
            METHOD_ELECTION,
            asyncUnaryCall(
              new MethodHandlers<
                protosPackage.ProtoNodeService.Request,
                protosPackage.ProtoMessage.Empty>(
                  this, METHODID_ELECTION)))
          .addMethod(
            METHOD_NEW_COORDINATOR,
            asyncUnaryCall(
              new MethodHandlers<
                protosPackage.ProtoNodeService.Request,
                protosPackage.ProtoMessage.Empty>(
                  this, METHODID_NEW_COORDINATOR)))
          .build();
    }
  }

  /**
   */
  public static final class NodeServiceStub extends io.grpc.stub.AbstractStub<NodeServiceStub> {
    private NodeServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     *USATO DA NODI
     * </pre>
     */
    public void sendStatistic(protosPackage.ProtoMessage.Measurement request,
        io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Measurement> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEND_STATISTIC, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void hello(protosPackage.ProtoNodeService.HelloRequest request,
        io.grpc.stub.StreamObserver<protosPackage.ProtoNodeService.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HELLO, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void election(protosPackage.ProtoNodeService.Request request,
        io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ELECTION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void newCoordinator(protosPackage.ProtoNodeService.Request request,
        io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_NEW_COORDINATOR, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NodeServiceBlockingStub extends io.grpc.stub.AbstractStub<NodeServiceBlockingStub> {
    private NodeServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *USATO DA NODI
     * </pre>
     */
    public protosPackage.ProtoMessage.Measurement sendStatistic(protosPackage.ProtoMessage.Measurement request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SEND_STATISTIC, getCallOptions(), request);
    }

    /**
     */
    public protosPackage.ProtoNodeService.Response hello(protosPackage.ProtoNodeService.HelloRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HELLO, getCallOptions(), request);
    }

    /**
     */
    public protosPackage.ProtoMessage.Empty election(protosPackage.ProtoNodeService.Request request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ELECTION, getCallOptions(), request);
    }

    /**
     */
    public protosPackage.ProtoMessage.Empty newCoordinator(protosPackage.ProtoNodeService.Request request) {
      return blockingUnaryCall(
          getChannel(), METHOD_NEW_COORDINATOR, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NodeServiceFutureStub extends io.grpc.stub.AbstractStub<NodeServiceFutureStub> {
    private NodeServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NodeServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NodeServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NodeServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *USATO DA NODI
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<protosPackage.ProtoMessage.Measurement> sendStatistic(
        protosPackage.ProtoMessage.Measurement request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEND_STATISTIC, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<protosPackage.ProtoNodeService.Response> hello(
        protosPackage.ProtoNodeService.HelloRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HELLO, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<protosPackage.ProtoMessage.Empty> election(
        protosPackage.ProtoNodeService.Request request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ELECTION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<protosPackage.ProtoMessage.Empty> newCoordinator(
        protosPackage.ProtoNodeService.Request request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_NEW_COORDINATOR, getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_STATISTIC = 0;
  private static final int METHODID_HELLO = 1;
  private static final int METHODID_ELECTION = 2;
  private static final int METHODID_NEW_COORDINATOR = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NodeServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(NodeServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_STATISTIC:
          serviceImpl.sendStatistic((protosPackage.ProtoMessage.Measurement) request,
              (io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Measurement>) responseObserver);
          break;
        case METHODID_HELLO:
          serviceImpl.hello((protosPackage.ProtoNodeService.HelloRequest) request,
              (io.grpc.stub.StreamObserver<protosPackage.ProtoNodeService.Response>) responseObserver);
          break;
        case METHODID_ELECTION:
          serviceImpl.election((protosPackage.ProtoNodeService.Request) request,
              (io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Empty>) responseObserver);
          break;
        case METHODID_NEW_COORDINATOR:
          serviceImpl.newCoordinator((protosPackage.ProtoNodeService.Request) request,
              (io.grpc.stub.StreamObserver<protosPackage.ProtoMessage.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class NodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NodeServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return protosPackage.ProtoNodeService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NodeService");
    }
  }

  private static final class NodeServiceFileDescriptorSupplier
      extends NodeServiceBaseDescriptorSupplier {
    NodeServiceFileDescriptorSupplier() {}
  }

  private static final class NodeServiceMethodDescriptorSupplier
      extends NodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    NodeServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NodeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NodeServiceFileDescriptorSupplier())
              .addMethod(METHOD_SEND_STATISTIC)
              .addMethod(METHOD_HELLO)
              .addMethod(METHOD_ELECTION)
              .addMethod(METHOD_NEW_COORDINATOR)
              .build();
        }
      }
    }
    return result;
  }
}
