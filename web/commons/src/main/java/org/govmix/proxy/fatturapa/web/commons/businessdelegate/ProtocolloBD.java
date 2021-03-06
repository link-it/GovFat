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
package org.govmix.proxy.fatturapa.web.commons.businessdelegate;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.govmix.proxy.fatturapa.orm.IdProtocollo;
import org.govmix.proxy.fatturapa.orm.Protocollo;
import org.govmix.proxy.fatturapa.orm.dao.IProtocolloService;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

public class ProtocolloBD extends BaseBD {


	private IProtocolloService service;

	public ProtocolloBD() throws Exception {
		this(Logger.getLogger(ProtocolloBD.class));
	}

	public ProtocolloBD(Logger log) throws Exception {
		super(log);
		this.service = this.serviceManager.getProtocolloService();
	}

	public ProtocolloBD(Logger log, Connection connection, boolean autoCommit) throws Exception {
		super(log, connection, autoCommit);
		this.service = this.serviceManager.getProtocolloService();
	}

	public Protocollo get(IdProtocollo id) throws Exception {
		try {
			return this.service.get(id);
		} catch (ServiceException e) {
			this.log.error("Errore durante la get: " + e.getMessage(), e);
			throw new Exception(e);
		} catch (NotFoundException e) {
			this.log.error("Errore durante la get: " + e.getMessage(), e);
			throw new Exception(e);
		} catch (MultipleResultException e) {
			this.log.error("Errore durante la get: " + e.getMessage(), e);
			throw new Exception(e);
		} catch (NotImplementedException e) {
			this.log.error("Errore durante la get: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

}
