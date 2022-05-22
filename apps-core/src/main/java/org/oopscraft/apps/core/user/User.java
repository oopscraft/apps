package org.oopscraft.apps.core.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "apps_user")
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

	public enum Type {
		GENERAL, SYSTEM
	}

	public enum Status {
		ACTIVE, LOCKED, EXPIRED
	}

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "password")
	private String password;

    @Column(name = "name")
    private String name;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Type type = Type.GENERAL;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Status status = Status.ACTIVE;
    
	@Column(name = "email")
	private String email;

	@Column(name = "mobile")
	private String mobile;
    
    @Column(name = "photo")
    @Lob
    private String photo;
    
    @Column(name = "icon")
    @Lob
    private String icon;
    
    @Column(name = "profile")
    @Lob
    private String profile;
    
    @Column(name = "locale")
    private String locale;
    


}
