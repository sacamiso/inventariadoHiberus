package com.tfg.inventariado.configuracion;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.entity.EmpleadoEntity;
import com.tfg.inventariado.repository.EmpleadoRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service()
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomUserDetailsService implements UserDetailsService{

	private @NonNull EmpleadoRepository usuarioRepository;

	@Override
    public UserDetails loadUserByUsername(String username) {
    	Optional<EmpleadoEntity> usuario;
	    usuario = usuarioRepository.findByUsuario(username);
    	
        if (!usuario.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return usuario.get();
    }
}
