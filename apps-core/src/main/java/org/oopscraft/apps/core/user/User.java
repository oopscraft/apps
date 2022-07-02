package org.oopscraft.apps.core.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
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
    @Column(name = "id", length=32)
    private String id;

    @Column(name = "password", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

    @Column(name = "name", nullable = false)
    private String name;

	@Column(name = "type", length = 16, nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Type type = Type.GENERAL;

	@Column(name = "status", length = 16, nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Status status = Status.ACTIVE;
    
	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "mobile", unique = true)
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
    
    @Column(name = "locale", length = 16)
    private String locale;
    
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "user_role",
		joinColumns = @JoinColumn(name = "user_id"), 
		foreignKey = @ForeignKey(name = "none"),
		inverseJoinColumns = @JoinColumn(name = "role_id"), 
		inverseForeignKey = @ForeignKey(name = "none")
	)
	@Builder.Default
	List<Role> roles = new ArrayList<>();
	
	/**
	 * getAuthorities
	 * @return list of authorities
	 */
	public List<Authority> getAuthorities() {
		List<Authority> authorities = new ArrayList<>();
		for(Role role : getRoles()) {
			authorities.addAll(role.getAuthorities());
		}
		return authorities;
	}

}
