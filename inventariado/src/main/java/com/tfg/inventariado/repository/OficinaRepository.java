package com.tfg.inventariado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.OficinaEntity;

@Repository
public interface OficinaRepository extends JpaRepository<OficinaEntity, Integer>{

}
