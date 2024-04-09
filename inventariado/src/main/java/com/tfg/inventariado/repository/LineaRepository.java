package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.LineaEntity;
import com.tfg.inventariado.entity.id.LineaEntityID;

@Repository
public interface LineaRepository extends JpaRepository<LineaEntity, LineaEntityID>{

	List<LineaEntity> findByNumeroPedido(int numeroPedido);
}
