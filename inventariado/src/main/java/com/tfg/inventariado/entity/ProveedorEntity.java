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
@Table(name = "proveedor")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProveedorEntity {

	@Column(name="id_proveedor")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProveedor;
	
	@Column(name="cif", nullable = false, unique = true)
	private String cif;
	
	@Column(name="razon_social", nullable = false)
	private String razonSocial;
	
	@Column(name="direccion", nullable = false)
	private String direccion;
	
	@Column(name="codigo_postal")
	private Integer codigoPostal;
	
	@Column(name="localidad", nullable = false)
	private String localidad;
	
	@Column(name="telefono", nullable = false)
	private String telefono;
	
	@Column(name="email", nullable = false)
	private String email;
	
}
