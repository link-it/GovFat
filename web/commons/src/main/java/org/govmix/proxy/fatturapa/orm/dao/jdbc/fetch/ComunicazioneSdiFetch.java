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
package org.govmix.proxy.fatturapa.orm.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.govmix.proxy.fatturapa.orm.Metadato;
import org.govmix.proxy.fatturapa.orm.ComunicazioneSdi;


/**     
 * ComunicazioneSdiFetch
 *
 * @author Giuseppe Papandrea (papandrea@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ComunicazioneSdiFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(ComunicazioneSdi.model())){
				ComunicazioneSdi object = new ComunicazioneSdi();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setIdentificativoSdi", ComunicazioneSdi.model().IDENTIFICATIVO_SDI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "identificativo_sdi", ComunicazioneSdi.model().IDENTIFICATIVO_SDI.getFieldType()));
				setParameter(object, "set_value_tipoComunicazione", String.class,
					jdbcParameterUtilities.readParameter(rs, "tipo_comunicazione", ComunicazioneSdi.model().TIPO_COMUNICAZIONE.getFieldType())+"");
				setParameter(object, "setProgressivo", ComunicazioneSdi.model().PROGRESSIVO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "progressivo", ComunicazioneSdi.model().PROGRESSIVO.getFieldType(), org.openspcoop2.generic_project.dao.jdbc.utils.JDBCDefaultForXSDType.FORCE_ZERO_AS_NULL));
				setParameter(object, "setDataRicezione", ComunicazioneSdi.model().DATA_RICEZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ricezione", ComunicazioneSdi.model().DATA_RICEZIONE.getFieldType()));
				setParameter(object, "setNomeFile", ComunicazioneSdi.model().NOME_FILE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_file", ComunicazioneSdi.model().NOME_FILE.getFieldType()));
				setParameter(object, "setContentType", ComunicazioneSdi.model().CONTENT_TYPE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "content_type", ComunicazioneSdi.model().CONTENT_TYPE.getFieldType()));
				setParameter(object, "setRawData", ComunicazioneSdi.model().RAW_DATA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "raw_data", ComunicazioneSdi.model().RAW_DATA.getFieldType()));
				setParameter(object, "set_value_statoConsegna", String.class,
					jdbcParameterUtilities.readParameter(rs, "stato_consegna", ComunicazioneSdi.model().STATO_CONSEGNA.getFieldType())+"");
				setParameter(object, "setDataConsegna", ComunicazioneSdi.model().DATA_CONSEGNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_consegna", ComunicazioneSdi.model().DATA_CONSEGNA.getFieldType()));
				setParameter(object, "setDettaglioConsegna", ComunicazioneSdi.model().DETTAGLIO_CONSEGNA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "dettaglio_consegna", ComunicazioneSdi.model().DETTAGLIO_CONSEGNA.getFieldType()));
				return object;
			}
			if(model.equals(ComunicazioneSdi.model().METADATO)){
				Metadato object = new Metadato();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", ComunicazioneSdi.model().METADATO.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", ComunicazioneSdi.model().METADATO.NOME.getFieldType()));
				setParameter(object, "setValore", ComunicazioneSdi.model().METADATO.VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "valore", ComunicazioneSdi.model().METADATO.VALORE.getFieldType()));
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

			if(model.equals(ComunicazioneSdi.model())){
				ComunicazioneSdi object = new ComunicazioneSdi();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"id"));
				setParameter(object, "setIdentificativoSdi", ComunicazioneSdi.model().IDENTIFICATIVO_SDI.getFieldType(),
					this.getObjectFromMap(map,"identificativoSdi"));
				setParameter(object, "set_value_tipoComunicazione", String.class,
					this.getObjectFromMap(map,"tipoComunicazione"));
				setParameter(object, "setProgressivo", ComunicazioneSdi.model().PROGRESSIVO.getFieldType(),
					this.getObjectFromMap(map,"progressivo"));
				setParameter(object, "setDataRicezione", ComunicazioneSdi.model().DATA_RICEZIONE.getFieldType(),
					this.getObjectFromMap(map,"dataRicezione"));
				setParameter(object, "setNomeFile", ComunicazioneSdi.model().NOME_FILE.getFieldType(),
					this.getObjectFromMap(map,"nomeFile"));
				setParameter(object, "setContentType", ComunicazioneSdi.model().CONTENT_TYPE.getFieldType(),
					this.getObjectFromMap(map,"contentType"));
				setParameter(object, "setRawData", ComunicazioneSdi.model().RAW_DATA.getFieldType(),
					this.getObjectFromMap(map,"rawData"));
				setParameter(object, "set_value_statoConsegna", String.class,
					this.getObjectFromMap(map,"statoConsegna"));
				setParameter(object, "setDataConsegna", ComunicazioneSdi.model().DATA_CONSEGNA.getFieldType(),
					this.getObjectFromMap(map,"dataConsegna"));
				setParameter(object, "setDettaglioConsegna", ComunicazioneSdi.model().DETTAGLIO_CONSEGNA.getFieldType(),
					this.getObjectFromMap(map,"dettaglioConsegna"));
				return object;
			}
			if(model.equals(ComunicazioneSdi.model().METADATO)){
				Metadato object = new Metadato();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"Metadato.id"));
				setParameter(object, "setNome", ComunicazioneSdi.model().METADATO.NOME.getFieldType(),
					this.getObjectFromMap(map,"Metadato.nome"));
				setParameter(object, "setValore", ComunicazioneSdi.model().METADATO.VALORE.getFieldType(),
					this.getObjectFromMap(map,"Metadato.valore"));
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

			if(model.equals(ComunicazioneSdi.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("comunicazioni_sdi","id","seq_comunicazioni_sdi","comunicazioni_sdi_init_seq");
			}
			if(model.equals(ComunicazioneSdi.model().METADATO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("metadati","id","seq_metadati","metadati_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
