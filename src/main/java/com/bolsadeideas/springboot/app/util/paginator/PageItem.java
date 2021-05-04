package com.bolsadeideas.springboot.app.util.paginator;

public class PageItem {

	private Integer numero;
	private boolean actual;
	
	public PageItem(Integer numero, boolean actual) {
		super();
		this.numero = numero;
		this.actual = actual;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public boolean isActual() {
		return actual;
	}

	public void setActual(boolean actual) {
		this.actual = actual;
	}
	
	
	
}
