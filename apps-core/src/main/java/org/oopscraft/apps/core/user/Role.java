package org.oopscraft.apps.core.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @Id
    @Column(name = "id", length = 32)
    private String id;
    
    @Column(name = "name")
    private String name;

    @Column(name = "icon")
    @Lob
    private String icon;
    
    @Column(name = "note")
    @Lob
    private String note;
    
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "role_authority",
		joinColumns = @JoinColumn(name = "role_id"), 
		foreignKey = @ForeignKey(name = "none"),
		inverseJoinColumns = @JoinColumn(name = "authority_id"),
		inverseForeignKey = @ForeignKey(name = "none")
	)
    @Builder.Default
	List<Authority> authorities = new ArrayList<>();
    
}
