package com.tfg.inventariado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tfg.inventariado.entity.RolEntity;

@Repository
public interface RolRepository extends JpaRepository<RolEntity, String> {

}
