package com.tfg.inventariado.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.OficinaEntity;

@Repository
public interface OficinaRepository extends JpaRepository<OficinaEntity, Integer>{
	Page<OficinaEntity> findAll(Specification<OficinaEntity> spec,Pageable pageable);
	long count(Specification<OficinaEntity> spec);
}
