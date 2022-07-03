package org.oopscraft.apps.core.message;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "apps_message")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message extends BaseEntity {

	@Id
	@Column(name = "id", length = 64)
	private String id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "note")
	@Lob
	private String note;

	@Column(name = "value")
	@Lob
	private String value;

}
