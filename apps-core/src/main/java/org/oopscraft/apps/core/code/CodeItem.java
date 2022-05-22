package org.oopscraft.apps.core.code;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "apps_code_item")
@IdClass(CodeItem.Pk.class)
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeItem extends BaseEntity {

	@Data
	public static class Pk implements Serializable {
		private String codeId;
		private String id;
	}

	@Id
	@Column(name = "code_id")
	private String codeId;
	
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "sort")
	private int sort;
	
	@Column(name = "name")
	private String name;
	
}
