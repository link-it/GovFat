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
package org.govmix.proxy.fatturapa.web.timers;

import org.govmix.proxy.fatturapa.web.commons.utils.LoggerManager;

/**
 * Thread per la gestione del Threshold
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 9747 $, $Date: 2014-03-10 11:47:43 +0100 (Mon, 10 Mar 2014) $
 */
public class TimerConsegnaEsitoThread extends AbstractTimerThread {

	@Override
	public String getIdModulo() {
		return TimerConsegnaEsito.ID_MODULO;
	}

	@Override
	public AbstractTimerLib getTimerLib() throws Exception {
		return new TimerConsegnaEsitoLib(this.limit,LoggerManager.getBatchConsegnaEsitoLogger(),this.logQuery);
	}
}
