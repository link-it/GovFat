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
package org.govmix.proxy.fatturapa.orm.dao;

import java.util.List;

import org.govmix.proxy.fatturapa.orm.IdUtente;
import org.govmix.proxy.fatturapa.orm.Utente;
import org.openspcoop2.generic_project.dao.IServiceSearchWithId;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;


/** 
* Service can be used for research objects on the backend of type org.govmix.proxy.fatturapa.orm.Utente  
 *
 * @author Giuseppe Papandrea (papandrea@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IUtenteServiceSearch extends IServiceSearchWithId<Utente, IdUtente> {
    public List<Utente> findAll(IPaginatedExpression expression, boolean caricaDipartimenti) throws ServiceException,NotImplementedException;
    public List<Utente> findAll(IPaginatedExpression expression, boolean caricaDipartimenti, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingBehaviour) throws ServiceException,NotImplementedException;
}
