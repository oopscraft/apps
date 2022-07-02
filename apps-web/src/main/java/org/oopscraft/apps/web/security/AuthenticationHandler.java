package org.oopscraft.apps.web.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.oopscraft.apps.core.message.MessageSource;
import org.oopscraft.apps.web.WebConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler, LogoutSuccessHandler, AuthenticationEntryPoint {


	public static String ACCESS_TOKEN_HEADER_NAME = "X-Security-Token";

	public static int ACCESS_TOKEN_VALID_MINUTES = 60;

	public final WebConfig webConfig;

	public final JwtTokenEncoder jwtTokenEncoder;

	private final MessageSource messageSource;

	private final LocaleResolver localeResolver;

	RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException {
		try {
			UserDetails userDetails = (UserDetails)authentication.getPrincipal();
			issueAccessToken(userDetails, response);
			String referer = request.getHeader("referer");
			if(StringUtils.isNotBlank(referer)) {
				redirectStrategy.sendRedirect(request, response, referer);
			}else{
				response.setStatus(HttpServletResponse.SC_OK);
			}
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		if(exception instanceof AuthenticationException) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			String messageId = String.format("admin.security.%s", exception.getClass().getSimpleName());
			Locale locale = localeResolver.resolveLocale(request);
			String message = messageSource.getMessage(messageId, null, locale);
			throw new AuthenticationFailureException(message);
		}else{
			throw exception;
		}
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		expireAccessToken(request, response);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * commence
	 * @param request
	 * @param response
	 * @param authException
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		boolean onlyMessage = false;

		// case of custom header
		if("XMLHttpRequest".equals(request.getHeader("X-Requested-with"))){
			onlyMessage = true;
		}

		// case of request content type
		if("application/json".equals(request.getHeader("Content-Type"))){
			onlyMessage = true;
		}

		// process
		if(onlyMessage) {
			throw new AuthenticationFailureException(authException.getMessage());
		}else{
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "/login");
			response.getWriter().flush();
		}
	}

	/**
	 * parseAccessToken
	 * @param request
	 * @return
	 */
	public String parseAccessToken(HttpServletRequest request) {
		String accessToken = null;

		// checks request header
		accessToken = request.getHeader(ACCESS_TOKEN_HEADER_NAME);

		// checks cookie
		if(accessToken == null || accessToken.trim().length() < 1) {
			if(request.getCookies() != null) {
				for(Cookie cookie : request.getCookies()) {
					if(ACCESS_TOKEN_HEADER_NAME.equals(cookie.getName())) {
						accessToken = cookie.getValue();
						break;
					}
				}
			}
		}

		// return
		return accessToken;
	}

	/**
	 * issueAccessToken
	 * @param userDetails
	 * @param response
	 */
	public void issueAccessToken(UserDetails userDetails, HttpServletResponse response) {
		String securityToken = jwtTokenEncoder.encode(userDetails, webConfig.getSecureKey(), ACCESS_TOKEN_VALID_MINUTES);
		response.setHeader(ACCESS_TOKEN_HEADER_NAME, securityToken);
		Cookie securityTokenCookie = new Cookie(ACCESS_TOKEN_HEADER_NAME, securityToken);
		securityTokenCookie.setPath("/");
		securityTokenCookie.setHttpOnly(true);
		securityTokenCookie.setMaxAge(ACCESS_TOKEN_VALID_MINUTES * 60);
		response.addCookie(securityTokenCookie);
	}

	/**
	 * expireAccessToken
	 * @param request
	 * @param response
	 */
	public void expireAccessToken(HttpServletRequest request, HttpServletResponse response) {
		if(request.getCookies() != null) {
			for(Cookie cookie : request.getCookies()) {
				if(ACCESS_TOKEN_HEADER_NAME.equals(cookie.getName())) {
					cookie.setPath("/");
					cookie.setValue("");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
					break;
				}
			}
		}
	}

}
