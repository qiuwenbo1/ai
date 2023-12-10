package org.qwb.ai.faceRecognition.repository;

import org.qwb.ai.faceRecognition.entity.FaceStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaceStorageRepository extends JpaRepository<FaceStorage, Long>, JpaSpecificationExecutor<FaceStorage> {
    List<FaceStorage> findByParentPath(String path);

    FaceStorage findByParentPathAndOriginName(String parentPath,String originName);

}
