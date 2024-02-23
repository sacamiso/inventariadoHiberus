package com.tfg.inventariado.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salida")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SalidaEntity {

	@Column(name="id_salida")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idSalida;
	
	@Column(name="numero_unidades", nullable = false)
	private Integer numUnidades;
	
	@Column(name="coste_total", nullable = false)
	private double costeTotal;
	
	@Column(name="coste_unitario", nullable = false)
	private double costeUnitario;
	
	@Column(name = "fecha_salida")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaSalida;
	
	@Column(name="id_oficina", nullable = false)
	private Integer idOficina;
	
	@Column(name="cod_articulo", nullable = false)
	private Integer codArticulo;
}
