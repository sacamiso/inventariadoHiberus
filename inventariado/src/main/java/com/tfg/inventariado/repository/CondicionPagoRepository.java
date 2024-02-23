package com.tfg.inventariado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.CondicionPagoEntity;

@Repository
public interface CondicionPagoRepository extends JpaRepository<CondicionPagoEntity, String> {

}
