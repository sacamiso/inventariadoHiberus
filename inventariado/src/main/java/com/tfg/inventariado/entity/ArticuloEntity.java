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
@Table(name = "articulo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArticuloEntity {

	@Column(name="codigo_articulo")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigoArticulo;
	
	@Column(name="descripcion", nullable = false)
	private String descripcion;
	
	@Column(name="precio_unitario", nullable = false)
	private double precioUnitario;
	
	@Column(name="referencia", nullable = false)
	private String referencia;
	
	@Column(name="cod_categoria", nullable = false)
	private String codCategoria;
	
	@Column(name="cod_subcategoria", nullable = false)
	private String codSubcategoria;
	
	@Column(name="iva", nullable = false)
	private double iva;
	
	@Column(name="fabricante", nullable = false)
	private String fabricante;
	
	@Column(name="modelo")
	private String modelo;
	
}
