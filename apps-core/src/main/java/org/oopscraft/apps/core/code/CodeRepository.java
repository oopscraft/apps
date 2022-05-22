package org.oopscraft.apps.core.code;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository 
public interface CodeRepository extends JpaRepository<Code,String>, JpaSpecificationExecutor<Code> {

}
