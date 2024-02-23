package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.StockSeguridadEntity;
import com.tfg.inventariado.entity.StockSeguridadEntityID;

@Repository
public interface StockSeguridadRepository extends JpaRepository<StockSeguridadEntity, StockSeguridadEntityID> {

	List<StockSeguridadEntity> findByIdOficina(int idOficina);
	List<StockSeguridadEntity> findByCodCategoriaAndCodSubcategoria(String codigoCategoria, String codigoSubcategoria);
}
