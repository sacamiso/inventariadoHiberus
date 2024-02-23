package com.tfg.inventariado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.EstadoEntity;

@Repository
public interface EstadoRepository extends JpaRepository<EstadoEntity, String> {

}
