package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.HistorialInventarioEntity;
import com.tfg.inventariado.entity.HistorialInventarioEntityID;

@Repository
public interface HistorialInventarioRepository extends JpaRepository<HistorialInventarioEntity, HistorialInventarioEntityID> {

	List<HistorialInventarioEntity> findByIdOficina(int idOficina);
	List<HistorialInventarioEntity> findByCodArticulo(int codArt);
	
	Page<HistorialInventarioEntity> findAll(Pageable pageable);
	Page<HistorialInventarioEntity> findAll(Specification<HistorialInventarioEntity> spec, Pageable pageable);
	long count(Specification<HistorialInventarioEntity> spec);
	long count();
}
