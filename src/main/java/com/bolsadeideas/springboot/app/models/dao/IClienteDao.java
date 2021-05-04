package com.bolsadeideas.springboot.app.models.dao;


import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

//CRUD REPOSITORY ES LO NORMAL; SI QUIERO PAGINAR USO PAGINGANDSORTINGREPOSITORY

public interface IClienteDao extends PagingAndSortingRepository<Cliente,Long> {


}
