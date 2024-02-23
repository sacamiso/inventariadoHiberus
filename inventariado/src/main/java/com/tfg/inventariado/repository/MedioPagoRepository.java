package com.tfg.inventariado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.MedioPagoEntity;

@Repository
public interface MedioPagoRepository extends JpaRepository<MedioPagoEntity, String>{

}
