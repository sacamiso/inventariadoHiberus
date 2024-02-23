package com.tfg.inventariado.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asignacion")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AsignacionEntity {

	@Column(name="id_asignacion")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idAsignacion;
	
	@Column(name = "fecha_inicio", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

	@Column(name = "fecha_fin")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
	
	@Column(name="id_empleado", nullable = false)
	private Integer idEmpleado;
	
	@Column(name="cod_unidad", nullable = false)
	private Integer codUnidad;
	
	@ManyToOne
	@JoinColumn(name="id_empleado", referencedColumnName="id_empleado", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private EmpleadoEntity empleado;
	
	@ManyToOne
	@JoinColumn(name="cod_unidad", referencedColumnName="codigo_interno", insertable = false, updatable = false)
	private UnidadEntity unidad;
}
