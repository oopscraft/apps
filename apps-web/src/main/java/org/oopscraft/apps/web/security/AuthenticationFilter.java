package org.oopscraft.apps.web.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.core.user.User;
import org.oopscraft.apps.core.user.UserService;
import org.oopscraft.apps.web.WebConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends  OncePerRequestFilter {

	private final WebConfig webConfig;

	private final AuthenticationHandler authenticationHandler;

	private final AuthenticationProvider authenticationProvider;

	private final JwtTokenEncoder jwtTokenEncoder;

	private final UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		// checks and extends security token
		String accessToken = authenticationHandler.parseAccessToken(request);

		// try authentication
		if(accessToken != null) {
			try {
				UserDetails userDetails = jwtTokenEncoder.decode(accessToken, webConfig.getSecretKey());
				if (userDetails != null) {

					// if system account, checks real time authentication
					if(userDetails.getType() == User.Type.SYSTEM){
						User user = authenticationProvider.findUser(userDetails.getUsername());
						userDetails = UserDetails.valueOf(user);
						authenticationProvider.checkUserStatus(userDetails);
					}

					// adds default authorities from config
					for(String authority : webConfig.getDefaultAuthorities()){
						GrantedAuthority grantAuthority = new GrantedAuthority();
						grantAuthority.setAuthority(authority);
						userDetails.addAuthority(grantAuthority);
					}

					// authenticates
					Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					SecurityContext securityContext = SecurityContextHolder.getContext();
					securityContext.setAuthentication(authentication);

					// keep token alive
					authenticationHandler.issueAccessToken(userDetails, response);
				}
			} catch (Exception ignore) {
				log.warn(ignore.getMessage());
			}
		}

		filterChain.doFilter(request,response);
	}

}
