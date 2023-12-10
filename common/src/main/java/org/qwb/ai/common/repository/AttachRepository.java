package org.qwb.ai.common.repository;

import org.qwb.ai.common.entity.Attach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface AttachRepository extends JpaRepository<Attach, Long>, JpaSpecificationExecutor<Attach> {

    Attach findByEtag(String etag);
}
