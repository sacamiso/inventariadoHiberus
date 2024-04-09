package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.StockSeguridadEntity;
import com.tfg.inventariado.entity.id.StockSeguridadEntityID;

@Repository
public interface StockSeguridadRepository extends JpaRepository<StockSeguridadEntity, StockSeguridadEntityID> {

	List<StockSeguridadEntity> findByIdOficina(int idOficina);
	List<StockSeguridadEntity> findByCodCategoriaAndCodSubcategoria(String codigoCategoria, String codigoSubcategoria);
	
	Page<StockSeguridadEntity> findAll(Pageable pageable);
	long count();
	void deleteByIdOficina(int idOficina);
	
	Page<StockSeguridadEntity> findAll(Specification<StockSeguridadEntity> spec,Pageable pageable);
	long count(Specification<StockSeguridadEntity> spec);

	@Query("SELECT s FROM StockSeguridadEntity s ORDER BY s.idOficina, s.codCategoria, s.codSubcategoria")
	List<StockSeguridadEntity> findAllOrdered();
}
