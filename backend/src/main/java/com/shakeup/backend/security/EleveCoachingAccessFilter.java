package com.shakeup.backend.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.service.CoachingAccessService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class EleveCoachingAccessFilter extends OncePerRequestFilter {

	private final CoachingAccessService coachingAccess;

	public EleveCoachingAccessFilter(CoachingAccessService coachingAccess) {
		this.coachingAccess = coachingAccess;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		if (!path.startsWith("/api/v1/eleve")) {
			return true;
		}
		return "GET".equalsIgnoreCase(request.getMethod()) && path.equals("/api/v1/eleve/me");
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain)
			throws ServletException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof UserAccount user)) {
			filterChain.doFilter(request, response);
			return;
		}
		if (user.getRole() != Role.ELEVE || coachingAccess.isCoachingActive(user)) {
			filterChain.doFilter(request, response);
			return;
		}
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter()
				.write("{\"code\":\"COACHING_ENDED\",\"message\":\"Suivi en pause — contacte ton coach pour réactiver.\"}");
	}
}
