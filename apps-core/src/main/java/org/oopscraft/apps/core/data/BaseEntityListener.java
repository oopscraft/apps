package org.oopscraft.apps.core.data;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Slf4j
public class BaseEntityListener {

	@PrePersist
	public void prePersist(BaseEntity baseEntity) {
		baseEntity.setModifyDateTime(LocalDateTime.now());
		baseEntity.setModifyUserId(getCurrentUserId());
		
	}
	
	@PreUpdate
	public void preUpdate(BaseEntity baseEntity) {
		baseEntity.setModifyDateTime(LocalDateTime.now());
		baseEntity.setModifyUserId(getCurrentUserId());
	}

	@PreRemove
	public void preRemove(BaseEntity baseEntity) {
		log.debug("BaseEntityListener.postLoad", baseEntity);
		if(baseEntity.getSystemData()) {
			throw new RuntimeException("System data cannot be deleted.");
		}
	}

	/*
	 * Return login user id
	 * @return
	 * @throws Exception
	 */
	private static final String getCurrentUserId() {
		// TODO
		return null;
	}

}
