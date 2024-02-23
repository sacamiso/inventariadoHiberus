package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oficina")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OficinaEntity {

	@Column(name="id_oficina")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idOficina;
	
	@Column(name="codigo_postal")
	private Integer codigoPostal;
	
	@Column(name="direccion", nullable = false)
	private String direccion;
	
	@Column(name="localidad", nullable = false)
	private String localidad;
	
	@Column(name="provincia")
	private String provincia;
	
	@Column(name="pais", nullable = false)
	private String pais;
}
	