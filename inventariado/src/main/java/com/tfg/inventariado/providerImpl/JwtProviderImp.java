package com.tfg.inventariado.providerImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.entity.EmpleadoEntity;
import com.tfg.inventariado.provider.JwtProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtProviderImp implements JwtProvider{
	
	@Value("${security.jwt.duration}")
	private long tokenDuration;
	
	@Value("${security.jwt.secret}")
	private String secret;

	@Override
	public String generateToken(EmpleadoEntity usuario, Map<String, Object> extraClaims) {
		LocalDateTime issuedAt = LocalDateTime.now();
		LocalDateTime expiration = issuedAt.plusMinutes(tokenDuration);
		return Jwts
			.builder()
			.claims(extraClaims)
			.subject(usuario.getUsuario())
			.issuedAt(Timestamp.valueOf(issuedAt))
			.expiration(Timestamp.valueOf(expiration))
			.header()
			.and()
			.signWith(generateSecretKey())
			.compact();		
	}
	
	private SecretKey generateSecretKey() {
		byte[] secretBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(secretBytes);
	}

	@Override
	public String extraerUsuario(String jwt) {
		return extraerClaims(jwt).getSubject();
	}

	private Claims extraerClaims(String jwt) {
		return Jwts.parser()
			.verifyWith(generateSecretKey())
			.build()
			.parseSignedClaims(jwt).getPayload();
	}
	
	@Override
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(generateSecretKey()).build().parseSignedClaims(token);
		    return true;
		} catch (MalformedJwtException ex) {
		    log.error("Token JWT invalido");
		} catch (ExpiredJwtException ex) {
		    log.error("Token JWT expirado " + ex.getClaims().getSubject());
		} catch (UnsupportedJwtException ex) {
		    log.error("Token JWT no soportado");
		} catch (IllegalArgumentException ex) {
		    log.error("Token JWT vacio");
		} catch (SignatureException e) {
		    log.error("Firma JWT invalida");
		}
		return false;
	}

}
