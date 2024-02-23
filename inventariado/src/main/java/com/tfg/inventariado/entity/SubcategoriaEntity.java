package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subcategoria")
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(SubcategoriaEntityID.class)
public class SubcategoriaEntity {

	@Column(name="codigo_subcategoria")
	@Id
	private String codigoSubcategoria;
	
	@Column(name="codigo_categoria")
	@Id
	private String codigoCategoria;
	
	@Column(name="nombre", nullable = false)
	private String nombre;
	
	@ManyToOne
	@JoinColumn(name="codigo_categoria", referencedColumnName="codigo_categoria", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private CategoriaEntity categoria;
}
