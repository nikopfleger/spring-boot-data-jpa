package com.bolsadeideas.springboot.app.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/listar")
	public String listar(@RequestParam(name="page", defaultValue="0") int page, Model model) {
		
		Pageable pageRequest = PageRequest.of(page,4); 
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		
		model.addAttribute("page",pageRender);				
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);

		return "listar";
	}

	@GetMapping("/form")
	public String crear(Model model) {
		Cliente cliente = new Cliente();
		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", "Formulario de Cliente");
		return "form";
	}

	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con éxito!" : "Cliente creado con éxito!";
		
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
	}

	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash,
			SessionStatus status) {

		Cliente cliente = null;

		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "El id del cliente no existe!");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El id del cliente no puede ser cero!");
			return "redirect:/listar";
		}

		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", "Editar Cliente");

		return "form";
	}

	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			clienteService.delete(id);
			flash.addAttribute("success", "Cliente eliminado con éxito!");
		}

		return "redirect:/listar";
	}
}
