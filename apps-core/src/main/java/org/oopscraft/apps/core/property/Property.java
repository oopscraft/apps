package org.oopscraft.apps.core.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "apps_property")
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Property extends BaseEntity {

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
	String value;
    
}
