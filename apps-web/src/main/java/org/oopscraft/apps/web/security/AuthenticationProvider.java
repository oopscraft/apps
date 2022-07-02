package org.oopscraft.apps.web.security;

import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.user.User;
import org.oopscraft.apps.core.user.UserService;
import org.oopscraft.apps.core.user.UserService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

	private final UserService userService;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

	@Override
	@Transactional
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String id = authentication.getName();
		String password = (String) authentication.getCredentials();

		// find user
		User user = findUser(id);

		// checks password validation
		checkPasswordMatch(password, user);

		// checks status
		UserDetails userDetails = UserDetails.valueOf(user);
		checkUserStatus(userDetails);

		// returns authentication token
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	/**
	 * findUser
	 * @param id
	 * @return
	 */
	public User findUser(String id) {
		User user = null;
		try {
			user = userService.getUser(id);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
		if(user == null) {
			throw new UsernameNotFoundException(null);
		}
		return user;
	}


	/**
	 * checkPasswordMatch
	 * @param password
	 * @param user
	 */
	public void checkPasswordMatch(String password, User user) {
		if(passwordEncoder.matches(password, user.getPassword()) == false) {
			throw new BadCredentialsException(null);
		}
	}

	/**
	 * checkUserStatus
	 * @param userDetails
	 */
	public void checkUserStatus(UserDetails userDetails) {
		if(userDetails.isAccountNonExpired() == false) {
			throw new AccountExpiredException(null);
		}
		if(userDetails.isAccountNonLocked() == false) {
			throw new LockedException(null);
		}
		if(userDetails.isEnabled() == false){
			throw new DisabledException(null);
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}