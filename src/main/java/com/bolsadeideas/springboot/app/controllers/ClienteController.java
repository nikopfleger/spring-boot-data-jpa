package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	private String rootPath;

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;

	//OTRA FORMA
	//@PreAuthorize("hasRole('')")
	@Secured("ROLE_USER")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		Cliente cliente = clienteService.fetchByIdWithFactura(id);
		//Cliente cliente = clienteService.findOne(id);

		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}

		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", "Detalle cliente: " + cliente.getNombre());

		return "ver";
	}

	@GetMapping({"/listar", "/", ""})
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Authentication authentication, HttpServletRequest request) {
		
		if (authentication != null) {
			logger.info("Hola usuario autenticado, tu username es: " + authentication.getName());
			
		}
		

		
		/*
		if (hasRole("ROLE_ADMIN",  authentication)) {
			logger.info("Hola ".concat(authentication.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Hola ".concat(authentication.getName()).concat(" no tienes acceso!"));
		}  LO MISMMO DE ABAJO */
		
		/*-
		//WRAPPER
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
		
		if(securityContext.isUserInRole("ADMIN")) {
			logger.info("Hola ".concat(authentication.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Hola ".concat(authentication.getName()).concat(" no tienes acceso!"));
		} LO MISMO DE ABAJO */
		
		if(request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(authentication.getName()).concat(" tienes acceso!"));
		} else {
			try {
			logger.info("Hola ".concat(authentication.getName()).concat(" no tienes acceso!"));
			} catch (NullPointerException e) {
				//e.printStackTrace();
				logger.info("Hola, no tienes usuario, por lo tanto no tienes acceso");
			}
		}
		
		Pageable pageRequest = PageRequest.of(page, 4);

		Page<Cliente> clientes = clienteService.findAll(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);

		model.addAttribute("page", pageRender);
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);

		return "listar";
	}

	@Secured("ROLE_ADMIN") //CON LLAVES VALIDO VARIOS
	@GetMapping("/form")
	public String crear(Model model) {
		Cliente cliente = new Cliente();
		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", "Formulario de Cliente");
		return "form";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}

		if (!foto.isEmpty()) {

			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {

				uploadFileService.delete(cliente.getFoto());

			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");

			cliente.setFoto(uniqueFilename);

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

	@Secured("ROLE_ADMIN")
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);

			clienteService.delete(id);
			flash.addAttribute("success", "Cliente eliminado con éxito!");

			if (uploadFileService.delete(cliente.getFoto())) {
				flash.addAttribute("info", "Foto " + cliente.getFoto() + " eliminada con exito!");
			}

		}

		return "redirect:/listar";
	}
	
	//TODA CLASE ROL TIENE QUE IMPLEMENTAR GrantedAuthority
	//SPRING SECURITY LOS MANEJA CON SIMPLEGRANTEDAUTHORITY, PERO SI QUIERO CREAR UN ROL DEBO INCLUIRLO
	private boolean hasRole(String role, Authentication auth) {
		
		if (auth!= null) {
			logger.info("Hola usuario autenticado, tu username es: " + auth.getName());
			
		}
		//CUALQUIER ROL TIENE QUE IMPLEMENTAR LA INTERFAZ
		Collection<?  extends GrantedAuthority> authorities = auth.getAuthorities();
		
		//SI TENGO EL ROL
		return authorities.contains(new SimpleGrantedAuthority(role));
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
}
