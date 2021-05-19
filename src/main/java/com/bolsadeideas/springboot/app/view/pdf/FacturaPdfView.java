package com.bolsadeideas.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

//EXPORTAR EL HTML EN UN PDF
@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {
	
	@Autowired
    private MessageSource messageSource;
	
	@Autowired
    private LocaleResolver localeResolver;

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Factura factura = (Factura) model.get("factura");
		
		Locale locale = localeResolver.resolveLocale(request);
		
		
		//OTRA FORMA MAS FACIL DE LOCALE QUE CON EL RESOLVER y EL MESSAGESOURCE
		MessageSourceAccessor mensajes = getMessageSourceAccessor();
		
		PdfPTable datosCliente = new PdfPTable(1);
		datosCliente.setSpacingAfter(20);
		
		PdfPCell cell = null;
		
		
		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.cliente", null, locale)));
		cell.setBackgroundColor(new Color(184,218,255));
		cell.setPadding(8f);
		datosCliente.addCell(cell);
		
		
		datosCliente.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		datosCliente.addCell(factura.getCliente().getEmail());
		
		PdfPTable datosFactura = new PdfPTable(1);
		datosFactura.setSpacingAfter(20);
		
		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.factura", null, locale)));
		cell.setBackgroundColor(new Color(195,230,203));
		cell.setPadding(8f);
		
		datosFactura.addCell(cell);
		datosFactura.addCell(mensajes.getMessage("text.cliente.factura.folio") + ": " + factura.getId());
		datosFactura.addCell(mensajes.getMessage("text.cliente.factura.descripcion") + ": " + factura.getDescripcion());
		datosFactura.addCell(mensajes.getMessage("text.cliente.factura.fecha") + ": " + factura.getCreateAt());
		
		PdfPTable itemsFactura = new PdfPTable(4);
		
		itemsFactura.setWidths(new float[] {3.5f, 1, 1, 1});
		itemsFactura.addCell(mensajes.getMessage("text.factura.form.item.nombre"));
		itemsFactura.addCell(mensajes.getMessage("text.factura.form.item.precio"));		
		itemsFactura.addCell(mensajes.getMessage("text.factura.form.item.cantidad"));
		itemsFactura.addCell(mensajes.getMessage("text.factura.form.item.total"));
		
		for(ItemFactura item: factura.getItems()) {
			itemsFactura.addCell(item.getProducto().getNombre());
			itemsFactura.addCell(item.getProducto().getPrecio().toString());
			
			cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			
			itemsFactura.addCell(cell);
			itemsFactura.addCell(item.calcularImporte().toString());
		}
		
		//CALCULAR GRAN TOTAL		
		cell = new PdfPCell(new Phrase(mensajes.getMessage("text.factura.form.total")));
		cell.setColspan(3);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		itemsFactura.addCell(cell);
		itemsFactura.addCell(factura.getTotal().toString());
		
		//GUARDO LAS TABLAS AL DOCUMENTO
		document.add(datosCliente);
		document.add(datosFactura);
		document.add(itemsFactura);
		
		
		
		
	}

}
