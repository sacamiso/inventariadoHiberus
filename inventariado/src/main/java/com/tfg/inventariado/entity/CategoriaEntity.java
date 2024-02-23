package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categoria")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoriaEntity {

	@Column(name="codigo_categoria")
	@Id
	private String codigoCategoria;
	
	@Column(name="nombre", nullable = false)
	private String nombre;
}
