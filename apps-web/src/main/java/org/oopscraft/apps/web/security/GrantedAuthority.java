package org.oopscraft.apps.web.security;

import lombok.Data;
import org.oopscraft.apps.core.user.Authority;

@Data
public class GrantedAuthority implements org.springframework.security.core.GrantedAuthority {

	private String authority;
	
	/**
	 * value Of Authority
	 * @param authority
	 * @return
	 */
	public static GrantedAuthority valueOf(Authority authority) {
		GrantedAuthority grantedAuthority = new GrantedAuthority();
		grantedAuthority.setAuthority(authority.getId());
		return grantedAuthority;
	}
	
	@Override
	public String getAuthority() {
		return authority;
	}

}
