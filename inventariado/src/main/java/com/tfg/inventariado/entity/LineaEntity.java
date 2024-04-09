package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.tfg.inventariado.entity.id.LineaEntityID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linea")
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(LineaEntityID.class) 
public class LineaEntity {

	@Column(name="numero_pedido")
	@Id
	private Integer numeroPedido;
	
	@Column(name="numero_linea")
	@Id
	private Integer numeroLinea;
	
	@Column(name="codigo_articulo", nullable = false)
	private Integer codigoArticulo;
	
	@Column(name="numero_unidades", nullable = false)
	private Integer numeroUnidades;
	
	@Column(name="precio_linea", nullable = false)
	private double precioLinea;
	
	@Column(name="descuento", nullable = false)
	private double descuento;
	
	@ManyToOne
	@JoinColumn(name="codigo_articulo", referencedColumnName="codigo_articulo", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private ArticuloEntity articulo;
}
