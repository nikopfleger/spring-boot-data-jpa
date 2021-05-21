package com.bolsadeideas.springboot.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
	@NotEmpty
	private String nombre;

	@NotEmpty
	private String apellido;

	@NotEmpty
	@Email
	private String email;

	@NotNull
	@Column(name = "create_at")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date createAt;

	private String foto;
	
	//DELETE Y PERSIST EN CASCADA
	//mappedBy crea automatico la foreign key
	@OneToMany(mappedBy="cliente" , fetch = FetchType.LAZY, cascade= CascadeType.ALL, orphanRemoval=true) 
	@JsonManagedReference
	private List<Factura> factura;

	// SE LLAMA ANTES DE INSERTAR CON PERSIST
//	@PrePersist
//	public void prePersist() {
//		createAt = new Date();
//	}
	
	public Cliente() {
		factura = new ArrayList<Factura>();
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

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<Factura> getFactura() {
		return factura;
	}

	public void setFactura(List<Factura> factura) {
		this.factura = factura;
	}
	
	public void addFactura(Factura factura) {
		factura.add(factura);
	}

	@Override
	public String toString() {
		return nombre + " " + apellido;
	}


	
}
