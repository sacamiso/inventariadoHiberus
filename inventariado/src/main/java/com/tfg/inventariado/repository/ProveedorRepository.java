package com.tfg.inventariado.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.ProveedorEntity;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Integer> {

	Optional<ProveedorEntity> findByCif(String cif);
	
	Page<ProveedorEntity> findAll(Specification<ProveedorEntity> spec,Pageable pageable);
	long count(Specification<ProveedorEntity> spec);
}
