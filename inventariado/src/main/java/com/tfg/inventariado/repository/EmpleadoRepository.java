package com.tfg.inventariado.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.EmpleadoEntity;

@Repository
public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, Integer> {

	List<EmpleadoEntity> findByIdOficina(int idOficina);
	List<EmpleadoEntity> findByCodRol(String codRol);
	List<EmpleadoEntity> findByUsuario(String usuario);
	Optional<EmpleadoEntity> findByDni(String dni);

	Page<EmpleadoEntity> findAll(Specification<EmpleadoEntity> spec,Pageable pageable);
	long count(Specification<EmpleadoEntity> spec);
}
