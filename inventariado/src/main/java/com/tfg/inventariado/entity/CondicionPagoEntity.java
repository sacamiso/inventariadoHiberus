package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "condicion_pago")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CondicionPagoEntity {

	@Column(name="codigo_condicion")
	@Id
	private String codigoCondicion;
	
	@Column(name="descripcion", nullable = false)
	private String descripcion;
}
