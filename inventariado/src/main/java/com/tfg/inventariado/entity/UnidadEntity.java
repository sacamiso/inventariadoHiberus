package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unidad")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UnidadEntity {

	@Column(name="codigo_interno")
	@Id
	private Integer codigoInterno;
	
	@Column(name="cod_estado", nullable = false)
	private String codEstado;
	
	@Column(name="num_pedido")
	private Integer numeroPedido;
	
	@Column(name="id_salida")
	private Integer idSalida;
	
	@Column(name="id_oficina", nullable = false)
	private Integer idOficina;
	
	@Column(name="cod_articulo", nullable = false)
	private Integer codArticulo;
}
