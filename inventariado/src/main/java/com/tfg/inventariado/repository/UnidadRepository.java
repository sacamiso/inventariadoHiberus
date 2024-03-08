package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.UnidadEntity;

@Repository
public interface UnidadRepository  extends JpaRepository<UnidadEntity, Integer> {

	List<UnidadEntity> findByCodEstado(String cod);
	List<UnidadEntity> findByIdOficina(Integer idOficina);
	List<UnidadEntity> findByCodArticulo(Integer codArticulo);
	List<UnidadEntity> findByIdSalidaIsNullAndCodEstadoNot(String codEstado);
    List<UnidadEntity> findByIdSalidaIsNullAndCodEstadoNotAndIdOficina(String codEstado, Integer idOficina);
    List<UnidadEntity> findByIdSalidaIsNotNullOrCodEstado(String codEstado);
    List<UnidadEntity> findByIdSalidaIsNotNullAndIdOficina(Integer idOficina);
    
    Page<UnidadEntity> findAll(Specification<UnidadEntity> spec, Pageable pageable);
	long count(Specification<UnidadEntity> spec);
}
