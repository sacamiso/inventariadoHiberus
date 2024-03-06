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
@Table(name = "stock_seguridad")
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(StockSeguridadEntityID.class) 
public class StockSeguridadEntity {

	@Column(name="cod_subcategoria")
	@Id
	private String codSubcategoria;
	
	@Column(name="cod_categoria")
	@Id
	private String codCategoria;
	
	@Column(name="id_oficina")
	@Id
	private Integer idOficina;
	
	@Column(name="cantidad", nullable = false)
	private Integer cantidad;
	
	@Column(name="plazo_entrega_medio", nullable = false)
	private Integer plazoEntregaMedio;
	
	@ManyToOne
	@JoinColumn(name="id_oficina", referencedColumnName="id_oficina", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private OficinaEntity oficina;
}
