package com.bolsadeideas.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.models.entity.Producto;

public interface IProductoDao extends CrudRepository<Producto, Long> {
	
	//?1 lo reemplaza por term
	@Query("select p from Producto p where p.nombre like %?1%")
	public List<Producto> findByNombre(String term);
	
	//ES LO MISMO DE ARRIBA, COMo mantengo un estandar en el nombre del metodo va a hacer lo de arriba automaticamente, en la documentacion de spring ir a query creation
	public List<Producto> findByNombreLikeIgnoreCase(String term);
}
