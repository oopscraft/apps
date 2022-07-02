package org.oopscraft.apps.core.code;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "code")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Code extends BaseEntity {
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "name")
	@NotNull
	private String name;
	
	@Column(name = "note")
	@Lob
	private String note;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = CodeItem_.CODE_ID, cascade = CascadeType.ALL, orphanRemoval= true)
	@OrderBy(CodeItem_.SORT)
	@Builder.Default
	List<CodeItem> items = new ArrayList<>();


}
