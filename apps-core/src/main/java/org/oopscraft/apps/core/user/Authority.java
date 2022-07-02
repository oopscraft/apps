package org.oopscraft.apps.core.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "authority")
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Authority extends BaseEntity {

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

}
