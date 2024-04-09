package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.SubcategoriaEntity;
import com.tfg.inventariado.entity.id.SubcategoriaEntityID;

@Repository
public interface SubcategoriaRepository extends JpaRepository<SubcategoriaEntity, SubcategoriaEntityID>{
	
	List<SubcategoriaEntity> findByCodigoCategoria(String codigoCategoria);
}
