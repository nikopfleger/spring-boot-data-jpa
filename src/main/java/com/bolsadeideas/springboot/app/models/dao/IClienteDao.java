package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

//CRUD REPOSITORY ES LO NORMAL; SI QUIERO PAGINAR USO PAGINGANDSORTINGREPOSITORY

public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long> {
	// A DIFERENCIA DEL EAGER ESTO LO HACE EN UN SOLO QUERY EL EAGER TRAERIA TODO DE
	// UNA PERO EN VARIOS QUERIES
	@Query("select c from Cliente c left join fetch c.factura f where c.id = ?1")
	public Cliente fetchByIdWithFactura(Long id);
}
