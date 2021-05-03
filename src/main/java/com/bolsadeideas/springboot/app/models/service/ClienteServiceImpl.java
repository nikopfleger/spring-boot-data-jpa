package com.bolsadeideas.springboot.app.models.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IClienteDao;
import com.bolsadeideas.springboot.app.models.entity.Cliente;

//SERVICE ESTA BASADO EN EL PATRON DE DISEÑO BUSINESS SERVICE FACADE
@Service
public class ClienteServiceImpl implements IClienteService {
	
	//LA IDEA ES TENER VARIOS DAO E IMPLEMENTAR TODOS ACA COMO UNICO PUNTO DE ACCESO
	//Y MUEVO TODOS LOS TRANSACTIONAL DEL CONTROLADOR A LA CLASE SERVICE
	
	@Autowired
	private IClienteDao clienteDao;

	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAll() {
		
		return clienteDao.findAll();
	}
	
	@Override
	public Cliente findOne(Long id) {
		return clienteDao.findOne(id);
	}


	@Override
	@Transactional
	public void save(Cliente cliente) {
		clienteDao.save(cliente);
		
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDao.delete(id);
		
	}

}
