package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.ArticuloEntity;

@Repository
public interface ArticuloRepository extends JpaRepository<ArticuloEntity, Integer> {

	List<ArticuloEntity> findByCodCategoria(String codigoCategoria);
	List<ArticuloEntity> findByCodCategoriaAndCodSubcategoria(String codigoCategoria, String codigoSubcategoria);
}
