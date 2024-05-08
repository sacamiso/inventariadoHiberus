package com.tfg.inventariado.provider;

import java.util.Map;

import com.tfg.inventariado.entity.EmpleadoEntity;

public interface JwtProvider {

	String generateToken(EmpleadoEntity usuario, Map<String, Object> a);
	String extraerUsuario(String jwt);
	boolean validateToken(String token);
}
