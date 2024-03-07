package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.StockSeguridadEntity;
import com.tfg.inventariado.entity.StockSeguridadEntityID;

@Repository
public interface StockSeguridadRepository extends JpaRepository<StockSeguridadEntity, StockSeguridadEntityID> {

	List<StockSeguridadEntity> findByIdOficina(int idOficina);
	List<StockSeguridadEntity> findByCodCategoriaAndCodSubcategoria(String codigoCategoria, String codigoSubcategoria);
	
	Page<StockSeguridadEntity> findAll(Pageable pageable);
	long count();
	void deleteByIdOficina(int idOficina);
}
