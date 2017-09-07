/*
 * ProxyFatturaPA - Gestione del formato Fattura Elettronica 
 * http://www.gov4j.it/fatturapa
 * 
 * Copyright (c) 2014-2017 Link.it srl (http://link.it). 
 * Copyright (c) 2014-2017 Provincia Autonoma di Bolzano (http://www.provincia.bz.it/). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.govmix.proxy.fatturapa.web.console.bean;

import javax.faces.context.FacesContext;

import org.govmix.proxy.fatturapa.orm.TracciaSDI;
import org.govmix.proxy.fatturapa.orm.constants.TipoComunicazioneType;
import org.govmix.proxy.fatturapa.web.commons.exporter.AbstractSingleFileExporter;
import org.govmix.proxy.fatturapa.web.console.exporter.FattureExporter;
import org.openspcoop2.generic_project.web.view.IViewBean;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.impl.jsf1.bean.BaseBean;
import org.openspcoop2.generic_project.web.output.Button;
import org.openspcoop2.generic_project.web.output.DateTime;
import org.openspcoop2.generic_project.web.output.Text;

public class TracciaSDIBean extends BaseBean<TracciaSDI, Long> implements IViewBean<TracciaSDI, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Text identificativoSdi = null;
	private Text tipoComunicazione = null;
	private Text nomeFile = null;
	private DateTime data = null;
	private Button xml = null;
	private Button pdf = null;
	
	public TracciaSDIBean() {
		try{
			this.init();
		}catch(Exception e){

		}
	}
	
	public void init() throws FactoryException{
		this.identificativoSdi = this.getFactory().getOutputFieldFactory().createText("nomeAttachment","allegatoFattura.nomeAttachment");
		this.tipoComunicazione =   this.getFactory().getOutputFieldFactory().createText("algoritmoCompressione","allegatoFattura.algoritmoCompressione");
		this.nomeFile =  this.getFactory().getOutputFieldFactory().createText("formatoAttachment","allegatoFattura.formatoAttachment");
		this.data = this.getFactory().getOutputFieldFactory().createDateTime("data","notificaEsitoCommittente.data","dd/M/yyyy");
		this.xml = this.getFactory().getOutputFieldFactory().createButton("xml","notificaEsitoCommittente.label.xml",null,"/images/fatturapa/icons/xml.png","notificaEsitoCommittente.label.xml.iconTitle","notificaEsitoCommittente.label.xml.iconTitle");
		this.pdf = this.getFactory().getOutputFieldFactory().createButton("pdf","notificaEsitoCommittente.label.pdf",null,"/images/fatturapa/icons/pdf.png","notificaEsitoCommittente.label.pdf.iconTitle","notificaEsitoCommittente.label.pdf.iconTitle");
		
		this.setField(this.identificativoSdi);
		this.setField(this.tipoComunicazione);
		this.setField(this.nomeFile);
		this.setField(this.data);
		this.setField(this.xml);
		this.setField(this.pdf);
	}
	
	@Override
	public void setDTO(TracciaSDI dto) {
		super.setDTO(dto);
		
		this.identificativoSdi.setValue(this.getDTO().getIdentificativoSdi() + "");
		TipoComunicazioneType tipoComunicazione2 = this.getDTO().getTipoComunicazione();
		if(tipoComunicazione2 != null){
			String valueTC = tipoComunicazione2.getValue();
			this.tipoComunicazione.setValue("comunicazione.tipoComunicazione."+valueTC);
		}
		this.nomeFile.setValue(this.getDTO().getNomeFile());
		
		this.prepareUrls();
	}
	
	
	@Override
	public Long getId() {
		return this.getDTO() != null ? this.getDTO().getId() : -1L;
	}
	
	private void prepareUrls(){
		FacesContext context = FacesContext.getCurrentInstance();

		String url = context.getExternalContext().getRequestContextPath() 
				+ "/"+FattureExporter.FATTURE_EXPORTER+"?"
				+FattureExporter.PARAMETRO_IDS+"=" + this.getDTO().getId()
				+ "&"+FattureExporter.PARAMETRO_FORMATO+"="+ AbstractSingleFileExporter.FORMATO_XML
				+ "&"+FattureExporter.PARAMETRO_ACTION+"="+ FattureExporter.PARAMETRO_ACTION_COMUNICAZIONE;

		this.xml.setHref(this.getDTO().getRawData() != null ?  url : null);

		url = context.getExternalContext().getRequestContextPath() 
				+ "/"+FattureExporter.FATTURE_EXPORTER+"?"
				+FattureExporter.PARAMETRO_IDS+"=" + this.getDTO().getId()
				+ "&"+FattureExporter.PARAMETRO_FORMATO+"="+ AbstractSingleFileExporter.FORMATO_PDF
				+ "&"+FattureExporter.PARAMETRO_ACTION+"="+ FattureExporter.PARAMETRO_ACTION_COMUNICAZIONE;

		this.pdf.setHref( this.getDTO().getRawData() != null ? url : null);
	}

	public Text getIdentificativoSdi() {
		return identificativoSdi;
	}

	public void setIdentificativoSdi(Text identificativoSdi) {
		this.identificativoSdi = identificativoSdi;
	}

	public Text getTipoComunicazione() {
		return tipoComunicazione;
	}

	public void setTipoComunicazione(Text tipoComunicazione) {
		this.tipoComunicazione = tipoComunicazione;
	}

	public Text getNomeFile() {
		return nomeFile;
	}

	public void setNomeFile(Text nomeFile) {
		this.nomeFile = nomeFile;
	}

	public DateTime getData() {
		return data;
	}

	public void setData(DateTime data) {
		this.data = data;
	}

	public Button getXml() {
		return xml;
	}

	public void setXml(Button xml) {
		this.xml = xml;
	}

	public Button getPdf() {
		return pdf;
	}

	public void setPdf(Button pdf) {
		this.pdf = pdf;
	}
	
	
}
