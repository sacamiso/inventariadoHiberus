package com.tfg.inventariado.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empleado")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmpleadoEntity {

	@Column(name="id_empleado")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idEmpleado;
	
	@Column(name="dni", nullable = false, unique = true)
	private String dni;
	
	@Column(name="nombre", nullable = false)
	private String nombre;
	
	@Column(name="apellidos", nullable = false)
	private String apellidos;
	
	@Column(name="usuario", nullable = false)
	private String usuario;
	
	@Column(name="contrasena", nullable = false)
	private String contraseña;
	
	@Column(name="cod_rol", nullable = false)
	private String codRol;
	
	@Column(name="id_oficina")
	private Integer idOficina;
	
	@ManyToOne
	@JoinColumn(name="cod_rol", referencedColumnName="codigo_rol", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private RolEntity rol;
	
	@ManyToOne
	@JoinColumn(name="id_oficina", referencedColumnName="id_oficina", insertable = false, updatable = false)
	private OficinaEntity oficina;
}
