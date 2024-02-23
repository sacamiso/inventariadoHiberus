package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.InventarioEntity;
import com.tfg.inventariado.entity.InventarioEntityID;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity, InventarioEntityID> {
	List<InventarioEntity> findByIdOficina(int idOficina);
	List<InventarioEntity> findByCodArticulo(int codArt);
	
	Page<InventarioEntity> findAll(Pageable pageable);
	long count();
}
