package org.oopscraft.apps.core.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.converter.BooleanYnConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseEntity {

	@Column(name = "system_data_yn", length = 1)
	@Convert(converter= BooleanYnConverter.class)
	private Boolean systemData;

	@Column(name = "modify_date_time")
	private LocalDateTime modifyDateTime;
	
	@Column(name = "modify_user_id", length = 32)
	private String modifyUserId;

}
