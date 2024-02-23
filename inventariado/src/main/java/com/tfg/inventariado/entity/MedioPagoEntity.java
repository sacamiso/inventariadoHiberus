package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medio_pago")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MedioPagoEntity {

	@Column(name="codigo_medio")
	@Id
	private String codigoMedio;
	
	@Column(name="descripcion", nullable = false)
	private String descripcion;
}
