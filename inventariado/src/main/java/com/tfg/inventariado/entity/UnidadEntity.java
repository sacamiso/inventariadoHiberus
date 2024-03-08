package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	
	@ManyToOne
	@JoinColumn(name="cod_estado", referencedColumnName="codigo_estado", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private EstadoEntity estado;
	
	@Column(name="num_pedido")
	private Integer numeroPedido;
	
	@ManyToOne
	@JoinColumn(name="num_pedido", referencedColumnName="numero_pedido", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private PedidoEntity pedido;
	
	@Column(name="id_salida")
	private Integer idSalida;
	
	@ManyToOne
	@JoinColumn(name="id_salida", referencedColumnName="id_salida", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private SalidaEntity salida;
	
	@Column(name="id_oficina", nullable = false)
	private Integer idOficina;
	
	@ManyToOne
	@JoinColumn(name="id_oficina", referencedColumnName="id_oficina", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private OficinaEntity oficina;
	
	@Column(name="cod_articulo", nullable = false)
	private Integer codArticulo;
	
	@ManyToOne
	@JoinColumn(name="cod_articulo", referencedColumnName="codigo_articulo", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private ArticuloEntity articulo;
}
