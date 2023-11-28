package org.qwb.ai.faceRecognition.repository;

import org.qwb.ai.faceRecognition.entity.PersonImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PersonImageRepository extends JpaRepository<PersonImage, Long>, JpaSpecificationExecutor<PersonImage> {
    Long countByPerson(Long id);

    @Transactional
    void deleteByPerson(Long id);

    List<PersonImage> findByPerson(Long toLong);
}
