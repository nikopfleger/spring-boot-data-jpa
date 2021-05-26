package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;
import com.bolsadeideas.springboot.app.view.xml.ClienteList;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	private String rootPath;

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadFileService;
	
	//PARA INYECTAR EL IDIOMA	
	@Autowired
	private MessageSource messageSource;

	//OTRA FORMA
	//@PreAuthorize("hasRole('')")
	@Secured("ROLE_USER")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash, Locale locale) {

		Cliente cliente = clienteService.fetchByIdWithFactura(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
			return "redirect:/listar";
		}

		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.detalle.titulo", null, locale).concat(": ").concat(cliente.getNombre()));
		return "ver";
	}
	
	//RESPONSEBODY SIGNIFICA QUE ES DE TIPO REST Y SE VA A ALMACENAR EN EL CUERPO DE LA RESPUESTA
	//RESPONDE EN FORMATO JSON
	//SI QUIERO FORMATO XML TAMBIEN NECESITO LA CLASE WRAPPER DE CLIENTELIST
	@GetMapping("/listar-rest")
	public @ResponseBody ClienteList listarRest() {
		
		return new ClienteList(clienteService.findAll());
	
	}

	@GetMapping({"/listar", "/", ""})
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		if (authentication != null) {
			logger.info("Hola usuario autenticado, tu username es: " + authentication.getName());
			
		}
		
		
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
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		model.addAttribute("clientes", clientes);

		return "listar";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/form")
	public String crear(Model model, Locale locale) {

		Cliente cliente = new Cliente();
		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo.crear", null, locale));
		return "form";
	}
	
	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash,
			SessionStatus status, Locale locale) {

		Cliente cliente = null;

		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.id.error", null, locale));
			return "redirect:/listar";
		}
		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo.editar", null, locale));
		return "form";
	}

	@Secured("ROLE_ADMIN")
	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status, Locale locale) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo", null, locale));
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

			flash.addFlashAttribute("info", messageSource.getMessage("text.cliente.flash.foto.subir.success", null, locale) + "'" + uniqueFilename + "'");

			cliente.setFoto(uniqueFilename);

		}

		String mensajeFlash = (cliente.getId() != null) ? messageSource.getMessage("text.cliente.flash.editar.success", null, locale) : messageSource.getMessage("text.cliente.flash.crear.success", null, locale);

		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
	}

	
	@Secured("ROLE_ADMIN")
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {

		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);

			clienteService.delete(id);
			flash.addFlashAttribute("success", messageSource.getMessage("text.cliente.flash.eliminar.success", null, locale));

			if (uploadFileService.delete(cliente.getFoto())) {
				String mensajeFotoEliminar = String.format(messageSource.getMessage("text.cliente.flash.foto.eliminar.success", null, locale), cliente.getFoto());
				flash.addFlashAttribute("info", mensajeFotoEliminar);
			}

		}
		return "redirect:/listar";
	}
	
	//TODA CLASE ROL TIENE QUE IMPLEMENTAR GrantedAuthority
	//SPRING SECURITY LOS MANEJA CON SIMPLEGRANTEDAUTHORITY, PERO SI QUIERO CREAR UN ROL DEBO INCLUIRLO
	@SuppressWarnings("unused")
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
