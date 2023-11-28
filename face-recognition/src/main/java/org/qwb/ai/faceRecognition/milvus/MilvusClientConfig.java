//package org.qwb.ai.faceRecognition.milvus;
//
//import io.milvus.client.MilvusServiceClient;
//import io.milvus.grpc.DataType;
//import io.milvus.param.*;
//import io.milvus.param.collection.CreateCollectionParam;
//import io.milvus.param.collection.FieldType;
//import io.milvus.param.collection.HasCollectionParam;
//import io.milvus.param.collection.LoadCollectionParam;
//import io.milvus.param.index.CreateIndexParam;
//import io.milvus.param.partition.CreatePartitionParam;
//import io.milvus.param.partition.HasPartitionParam;
//import jakarta.annotation.PostConstruct;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
///**
// * @author demoQ
// * @date 2022/9/25 15:22
// */
//@Configuration
//public class MilvusClientConfig {
//
//    @Value("${milvus.host}")
//    private String host;
//    @Value("${milvus.port}")
//    private Integer port;
//    @Value("${milvus.collection}")
//    private String collection;
//
//    private final Logger logger = LoggerFactory.getLogger(MilvusClientConfig.class);
//
//    @Bean
//    public MilvusServiceClient milvusServiceClient() {
//        ConnectParam param = ConnectParam.newBuilder()
//                .withHost(host)
//                .withPort(port)
//                .build();
//        return new MilvusServiceClient(param);
//    }
//
//    @PostConstruct
//    public void createSimCollection() {
//        MilvusServiceClient milvusServiceClient = milvusServiceClient();
//        R<Boolean> respHasCollection = milvusServiceClient.hasCollection(
//                HasCollectionParam.newBuilder()
//                        .withCollectionName(this.collection)
//                        .build()
//        );
//        R<Boolean> partitionR = milvusServiceClient.hasPartition(
//                HasPartitionParam.newBuilder()
//                        .withCollectionName(this.collection)
//                        .withPartitionName("ylj")
//                        .build()
//        );
//        if(respHasCollection.getData() == Boolean.TRUE) {
//            logger.info("Milvus集合已存在！");
//        }else{
//            logger.info("创建Milvus集合：" + this.collection);
//            FieldType imageId = FieldType.newBuilder()
//                    .withName("face_id")
//                    .withPrimaryKey(true)
//                    .withDataType(DataType.Int64)
//                    .build();
//            FieldType embedding = FieldType.newBuilder()
//                    .withName("face_embedding")
//                    .withDataType(DataType.FloatVector)
//                    .withDimension(512)
//                    .build();
//            CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
//                    .withCollectionName(collection)
//                    .withDescription("人脸向量检索")
//                    .addFieldType(imageId)
//                    .addFieldType(embedding)
//                    .build();
//            CreateIndexParam indexParam = CreateIndexParam.newBuilder()
//                    .withCollectionName(collection)
//                    .withMetricType(MetricType.L2)
//                    .withFieldName("face_embedding")
//                    .withIndexType(IndexType.FLAT)
//                    .build();
//            R<RpcStatus> partitionStatus = milvusServiceClient.createPartition(
//                    CreatePartitionParam.newBuilder()
//                            .withCollectionName(collection)
//                            .withPartitionName("ylj")
//                            .build()
//            );
//            R<RpcStatus> collectionStatus = milvusServiceClient.createCollection(createCollectionReq);
//            R<RpcStatus> indexStatus = milvusServiceClient.createIndex(indexParam);
//            if (partitionStatus.getStatus() != R.Status.Success.getCode()){
//                logger.error(partitionStatus.getMessage());
//            }
//            if (collectionStatus.getStatus() != R.Status.Success.getCode()){
//                logger.error(collectionStatus.getMessage());
//            }
//            if (indexStatus.getStatus() != R.Status.Success.getCode()){
//                logger.error(indexStatus.getMessage());
//            }
//            logger.info("创建创建Milvus集合：" + collection + "成功！");
//            logger.info("创建创建Milvus分区【ylj】成功！");
//            logger.info("创建创建Milvus索引成功！");
//            milvusServiceClient.loadCollection(
//                    LoadCollectionParam.newBuilder()
//                            .withCollectionName(collection)
//                            .build()
//            );
//            logger.info("创建创建Milvus索引成功！");
//        }
//        if (partitionR.getData() == Boolean.TRUE){
//            logger.info("Milvus分区已存在！");
//        }else {
//            milvusServiceClient.createPartition(
//                    CreatePartitionParam.newBuilder()
//                            .withCollectionName(collection)
//                            .withPartitionName("ylj")
//                            .build()
//            );
//            logger.info("创建创建Milvus分区【ylj】成功！");
//        }
//    }
//
//}
