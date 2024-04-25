package com.tfg.inventariado.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.PedidoVWEntity;

@Repository
public interface PedidoVWRepository extends JpaRepository<PedidoVWEntity, Integer>{

	Page<PedidoVWEntity> findAll(Specification<PedidoVWEntity> spec,Pageable pageable);
	long count(Specification<PedidoVWEntity> spec);
	List<PedidoVWEntity> findAll(Specification<PedidoVWEntity> spec, Sort sort);

}
