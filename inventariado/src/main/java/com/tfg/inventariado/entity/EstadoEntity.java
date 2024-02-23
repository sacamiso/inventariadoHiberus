package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EstadoEntity {
	
	@Column(name="codigo_estado")
	@Id
	private String codigoEstado;
	
	@Column(name="nombre", nullable = false)
	private String nombre;
}
