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
package org.govmix.proxy.fatturapa.orm.model;

import org.govmix.proxy.fatturapa.orm.IdTrasmissioneEsito;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IdTrasmissioneEsito 
 *
 * @author Giuseppe Papandrea (papandrea@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdTrasmissioneEsitoModel extends AbstractModel<IdTrasmissioneEsito> {

	public IdTrasmissioneEsitoModel(){
	
		super();
	
		this.ID_TRASMISSIONE_ESITO = new Field("idTrasmissioneEsito",long.class,"id-trasmissione-esito",IdTrasmissioneEsito.class);
	
	}
	
	public IdTrasmissioneEsitoModel(IField father){
	
		super(father);
	
		this.ID_TRASMISSIONE_ESITO = new ComplexField(father,"idTrasmissioneEsito",long.class,"id-trasmissione-esito",IdTrasmissioneEsito.class);
	
	}
	
	

	public IField ID_TRASMISSIONE_ESITO = null;
	 

	@Override
	public Class<IdTrasmissioneEsito> getModeledClass(){
		return IdTrasmissioneEsito.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}