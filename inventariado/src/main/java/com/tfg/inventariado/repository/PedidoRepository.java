package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.PedidoEntity;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Integer>{
	List<PedidoEntity> findByIdProveedor(int idProveedor);
	List<PedidoEntity> findByIdOficina(int idOficina);
}
