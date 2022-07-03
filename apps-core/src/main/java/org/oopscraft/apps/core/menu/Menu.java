package org.oopscraft.apps.core.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;
import org.oopscraft.apps.core.data.converter.BooleanYnConverter;

import javax.persistence.*;


@Entity
@Table(name = "menu")
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Menu extends BaseEntity {

	@Id
	@Column(name = "id", length = 64)
	private String id;

	@Column(name = "upper_id", length = 64)
	private String upperId;

	@Column(name = "sort")
	private int sort;

	@Column(name = "name")
	private String name;

	@Column(name = "url")
	private String url;

	@Column(name = "open_new")
	@Convert(converter = BooleanYnConverter.class)
	private boolean openNew;

	@Column(name = "note")
	@Lob
	private String note;

}


