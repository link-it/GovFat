/*
 * ProxyFatturaPA - Gestione del formato Fattura Elettronica 
 * http://www.gov4j.it/fatturapa
 * 
 * Copyright (c) 2014-2018 Link.it srl (http://link.it). 
 * Copyright (c) 2014-2018 Provincia Autonoma di Bolzano (http://www.provincia.bz.it/). 
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
package org.govmix.proxy.fatturapa.web.console.anagrafica.bean;

import java.net.URI;

import org.govmix.proxy.fatturapa.orm.Protocollo;
import org.openspcoop2.generic_project.web.bean.IBean;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.impl.jsf1.bean.BaseBean;
import org.openspcoop2.generic_project.web.output.OutputGroup;
import org.openspcoop2.generic_project.web.output.Text;

public class ProtocolloBean extends BaseBean<Protocollo, Long> implements IBean<Protocollo, Long> {

	
	private Text nome = null;
	private Text descrizione = null;
	private Text endpoint = null;


	// Gruppo Informazioni Dati Genareli
	private OutputGroup fieldsDatiGenerali = null;

	public ProtocolloBean(){
		try{
			this.init();
		}catch(Exception e){

		}
	}

	private void init() throws FactoryException{
		this.nome = this.getWebGenericProjectFactory().getOutputFieldFactory().createText("nome","protocollo.nome");
		this.descrizione = this.getWebGenericProjectFactory().getOutputFieldFactory().createText("descrizione","protocollo.descrizione");
		this.endpoint = this.getWebGenericProjectFactory().getOutputFieldFactory().createText("endpoint","protocollo.endpoint");
		
		this.setField(this.nome);
		this.setField(this.descrizione);
		this.setField(this.endpoint);

		this.fieldsDatiGenerali = this.getWebGenericProjectFactory().getOutputFieldFactory().createOutputGroup("datiGenerali",2);
		this.fieldsDatiGenerali.setStyleClass(org.govmix.proxy.fatturapa.web.console.costanti.Costanti.CSS_CLASS_DATI_TRASMISSIONE_TABLE); 
		this.fieldsDatiGenerali.setColumnClasses(org.govmix.proxy.fatturapa.web.console.costanti.Costanti.CSS_CLASS_DATI_DETTAGLIO_DUE_COLONNE);

		this.fieldsDatiGenerali.addField(this.nome);
		this.fieldsDatiGenerali.addField(this.descrizione);
		this.fieldsDatiGenerali.addField(this.endpoint);
	}

	@Override
	public Long getId() {
		return this.dto != null ? this.dto.getId() : -1L;
	}

	@Override
	public void setDTO(Protocollo dto) {
		super.setDTO(dto);

		this.nome.setValue(this.getDTO().getNome());
		this.descrizione.setValue(this.getDTO().getDescrizione());
		URI endpoint2 = this.getDTO().getEndpoint();
		if(endpoint2 != null)
			this.endpoint.setValue(endpoint2.toString());

	}

	public Text getNome() {
		return this.nome;
	}

	public void setNome(Text nome) {
		this.nome = nome;
	}

	public Text getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(Text descrizione) {
		this.descrizione = descrizione;
	}

	public Text getEndpoint() {
		return this.endpoint;
	}

	public void setEndpoint(Text endpoint) {
		this.endpoint = endpoint;
	}

	public OutputGroup getFieldsDatiGenerali() {
		return this.fieldsDatiGenerali;
	}

	public void setFieldsDatiGenerali(OutputGroup fieldsDatiGenerali) {
		this.fieldsDatiGenerali = fieldsDatiGenerali;
	}
}
