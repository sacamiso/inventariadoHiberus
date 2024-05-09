package com.tfg.inventariado.configuracion;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tfg.inventariado.entity.EmpleadoEntity;
import com.tfg.inventariado.provider.JwtProvider;
import com.tfg.inventariado.repository.EmpleadoRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private @NonNull EmpleadoRepository usuarioRepository;

	private @NonNull JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		String jwt = authHeader.split(" ")[1];
		
		if (jwt == null || !jwtProvider.validateToken(jwt)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			filterChain.doFilter(request, response);	
			return;
		}
		
		String username = jwtProvider.extraerUsuario(jwt);
		
		Optional<EmpleadoEntity> usuario = usuarioRepository.findByUsuario(username);
		if (!usuario.isPresent()) {
			filterChain.doFilter(request, response);
			return;
		}
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(usuario.get(), null, usuario.get().getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authToken);
		
		//Aprovechamos para loggear información de las peticiones que no sean OPTIONS, de swagger o de api-docs o masterdata
		if (!request.getMethod().equals("OPTIONS") && !request.getRequestURI().contains("swagger") && !request.getRequestURI().contains("api-docs") && !request.getRequestURI().contains("masterdata")) {
			log.info("[" + request.getMethod() + "] " + request.getRequestURI() + " - Usuario: " + username);
		}
		filterChain.doFilter(request, response);			
	}

}
