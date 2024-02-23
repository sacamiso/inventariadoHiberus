package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.SalidaEntity;

@Repository
public interface SalidaRepository extends JpaRepository<SalidaEntity, Integer> {

	List<SalidaEntity> findByIdOficina(int idOficina);
	List<SalidaEntity> findByCodArticulo(int codArt);
	
}
