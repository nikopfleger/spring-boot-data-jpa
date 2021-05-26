package com.bolsadeideas.springboot.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.view.xml.ClienteList;

//ES CONTROLLER + RESPONSEBODY, POR LO QUE TODOS SUS METODOS SON DE TIPO REST
@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;

	@GetMapping("/listar")
	public ClienteList listar() {
				
		return new ClienteList(clienteService.findAll());
	
	}
	
}
