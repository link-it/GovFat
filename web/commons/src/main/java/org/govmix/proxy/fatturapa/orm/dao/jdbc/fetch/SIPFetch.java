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
package org.govmix.proxy.fatturapa.orm.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.govmix.proxy.fatturapa.orm.SIP;


/**     
 * SIPFetch
 *
 * @author Giuseppe Papandrea (papandrea@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SIPFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(SIP.model())){
				SIP object = new SIP();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setRegistro", SIP.model().REGISTRO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "registro", SIP.model().REGISTRO.getFieldType()));
				setParameter(object, "setAnno", SIP.model().ANNO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "anno", SIP.model().ANNO.getFieldType()));
				setParameter(object, "setNumero", SIP.model().NUMERO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "numero", SIP.model().NUMERO.getFieldType()));
				setParameter(object, "set_value_statoConsegna", String.class,
					jdbcParameterUtilities.readParameter(rs, "stato_consegna", SIP.model().STATO_CONSEGNA.getFieldType())+"");
				setParameter(object, "setDataUltimaConsegna", SIP.model().DATA_ULTIMA_CONSEGNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ultima_consegna", SIP.model().DATA_ULTIMA_CONSEGNA.getFieldType()));
				setParameter(object, "setRapportoVersamento", SIP.model().RAPPORTO_VERSAMENTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "rapporto_versamento", SIP.model().RAPPORTO_VERSAMENTO.getFieldType()));
				return object;
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by fetch: "+this.getClass().getName());
			}	
					
		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in fetch: "+e.getMessage(),e);
		}
		
	}
	
	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , Map<String,Object> map ) throws ServiceException {
		
		try{

			if(model.equals(SIP.model())){
				SIP object = new SIP();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setRegistro", SIP.model().REGISTRO.getFieldType(),
					this.getObjectFromMap(map,"registro"));
				setParameter(object, "setAnno", SIP.model().ANNO.getFieldType(),
					this.getObjectFromMap(map,"anno"));
				setParameter(object, "setNumero", SIP.model().NUMERO.getFieldType(),
					this.getObjectFromMap(map,"numero"));
				setParameter(object, "set_value_statoConsegna", String.class,
					this.getObjectFromMap(map,"statoConsegna"));
				setParameter(object, "setDataUltimaConsegna", SIP.model().DATA_ULTIMA_CONSEGNA.getFieldType(),
					this.getObjectFromMap(map,"dataUltimaConsegna"));
				setParameter(object, "setRapportoVersamento", SIP.model().RAPPORTO_VERSAMENTO.getFieldType(),
					this.getObjectFromMap(map,"rapportoVersamento"));
				return object;
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by fetch: "+this.getClass().getName());
			}	
					
		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in fetch: "+e.getMessage(),e);
		}
		
	}
	
	
	@Override
	public IKeyGeneratorObject getKeyGeneratorObject( IModel<?> model )  throws ServiceException {
		
		try{

			if(model.equals(SIP.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("sip","id","seq_sip","sip_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
