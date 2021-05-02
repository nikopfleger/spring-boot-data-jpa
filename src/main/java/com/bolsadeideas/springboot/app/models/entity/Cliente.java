package com.bolsadeideas.springboot.app.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//SI LA TABLA SE LLAMA IGUAL NO USAMOS @Table(name="")

@Table(name = "cliente")
@Entity
public class Cliente implements Serializable {

	private static final long serialVersionUID = 4327690299451102763L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// COLUMN SI TIENE EL MISMO NOMBRE NO ES NECESARIO
	// con , hay mas opciones
	@Column(name = "nombre")
	private String nombre;
	private String apellido;
	private String email;

	@Column(name = "create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	//SE LLAMA ANTES DE INSERTAR CON PERSIST
	@PrePersist
	public void prePersist() {
		createAt = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
