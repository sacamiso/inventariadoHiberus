package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rol")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RolEntity {

	@Column(name="codigo_rol")
	@Id
	private String codigoRol;
	
	@Column(name="nombre", nullable = false)
	private String nombre;
}
