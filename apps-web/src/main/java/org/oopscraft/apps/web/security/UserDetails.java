package org.oopscraft.apps.web.security;

import lombok.*;
import org.oopscraft.apps.core.user.Authority;
import org.oopscraft.apps.core.user.User;

import java.util.ArrayList;
import java.util.Collection;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access= AccessLevel.PROTECTED)
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

	private String username;

	private User.Type type = User.Type.GENERAL;

	private boolean enabled = true;

	private boolean accountNonExpired = true;

	private boolean accountNonLocked = true;

	private boolean credentialsNonExpired = true;

	private Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

	/**
	 * valueOf User
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static UserDetails valueOf(User user) {
		UserDetails userDetails = new UserDetails();
		userDetails.setUsername(user.getId());
		userDetails.setType(user.getType());
		userDetails.setEnabled(true);
		userDetails.setAccountNonExpired(user.getStatus() != User.Status.EXPIRED);
		userDetails.setAccountNonLocked(user.getStatus() != User.Status.LOCKED);
		userDetails.setCredentialsNonExpired(true);
		for(Authority authority : user.getAuthorities()) {
			userDetails.addAuthority(GrantedAuthority.valueOf(authority));
		}
		return userDetails;
	}


	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public User.Type getType() {
		return type;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/**
	 * addAuthority
	 * @param authority
	 */
	public void addAuthority(GrantedAuthority authority) {
		authorities.add(authority);
	}

}
