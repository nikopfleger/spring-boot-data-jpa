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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "factura")
@Entity
public class Factura implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3778805715180389301L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String descripcion;
	
	private String observacion;
	
	@Temporal(TemporalType.DATE)
	@Column(name="create_at")
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private Date createAt;
	
	//SOLO HAGO LA CONSULTA CUANDO LA LLAMO
	@ManyToOne(fetch=FetchType.LAZY)
	private Cliente cliente;
	
	
	//Join column si la relacion es solo de un solo sentido, por ejemplo no voy a pedir de que factura son los items pero si los items de cada factura
	//Si en la relación puedo pedir de ambos lados no necesito el joincolumn
	@OneToMany(fetch= FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "factura_id")
	private List<ItemFactura> items;
	
	
	
	public Factura() {
		this.items = new ArrayList<ItemFactura>();
	}

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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	//CUANDO SERIALIZA NO LLAMA A ESTE METODO
	@XmlTransient
	@JsonBackReference
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void add(Factura factura) {
		// TODO Auto-generated method stub
		
	}

	public List<ItemFactura> getItems() {
		return items;
	}

	public void setItems(List<ItemFactura> items) {
		this.items = items;
	}
	
	public void addItemFactura(ItemFactura item) {
		items.add(item);
	}
	
	public Double getTotal() {
		Double total = 0.0;
		
		
		for (ItemFactura item : this.items) {
			total += item.calcularImporte();
		}
		return total;
	}

}
