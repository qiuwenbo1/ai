package org.qwb.ai.faceRecognition.repository;

import org.qwb.ai.faceRecognition.entity.FaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FaceImageRepository extends JpaRepository<FaceImage, Long>, JpaSpecificationExecutor<FaceImage> {
}
