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
package org.govmix.proxy.pcc.fatture.utils;


public class PccProperties extends AbstractProperties {

	private static PccProperties props;
	
	public static PccProperties getInstance() {
		if(props == null) {
			props = new PccProperties("/pcc.properties");
		}
		
		return props;
		
	}

	private String urlWadlEndpointEnte;
	private String urlWadlEndpointPdd;
	private String urlWsdlFattureWS;
	private String urlWsdlTracceWS;
	private int maxTentativiRispedizione;
	private int fattoreRispedizione;

	private String urlPCC;
	private String usernamePCC;
	private String passwordPCC;
	private String versioneApplicativa;
	private String idEgovRichiestaHeader;
	
	private String sistemaRichiedenteCruscotto;
	private String utenteRichiedenteCruscotto;

	private String sistemaRichiedenteGestionale;
	private String utenteRichiedenteGestionale;

	private Integer intervalloRispedizioneDefault;
	private Integer intervalloSpedizioneEsito;


	private PccProperties(String path) throws RuntimeException {
		super(path);
		try {
			this.versioneApplicativa = this.getProperty("org.govmix.proxy.pcc.versioneApplicativa", true);
			this.urlPCC = this.getProperty("org.govmix.proxy.pcc.url", true);
			this.urlWadlEndpointEnte = this.getProperty("org.govmix.proxy.wadl.endpoint.ente.url", true);
			this.urlWadlEndpointPdd = this.getProperty("org.govmix.proxy.wadl.endpoint.pdd.url", true);
			this.urlWsdlFattureWS= this.getProperty("org.govmix.proxy.wsdl.fattureWS.url", true);
			this.urlWsdlTracceWS= this.getProperty("org.govmix.proxy.wsdl.tracceWS.url", true);
			this.usernamePCC = this.getProperty("org.govmix.proxy.pcc.username", true);
			this.passwordPCC = this.getProperty("org.govmix.proxy.pcc.password", true);
			this.idEgovRichiestaHeader = this.getProperty("org.govmix.proxy.pcc.idEgovRichiestaHeader", true);
			this.sistemaRichiedenteCruscotto = this.getProperty("org.govmix.proxy.pcc.sistemaRichiedenteCruscotto", true);
			this.utenteRichiedenteCruscotto = this.getProperty("org.govmix.proxy.pcc.utenteRichiedenteCruscotto", true);
			this.sistemaRichiedenteGestionale = this.getProperty("org.govmix.proxy.pcc.sistemaRichiedenteGestionale", true);
			this.utenteRichiedenteGestionale = this.getProperty("org.govmix.proxy.pcc.utenteRichiedenteGestionale", true);

		
			this.maxTentativiRispedizione = Integer.parseInt(this.getProperty("org.govmix.proxy.fatturapa.web.api.pdd.consegnaFattura.maxTentativiRispedizione", true));
			this.fattoreRispedizione = Integer.parseInt(this.getProperty("org.govmix.proxy.fatturapa.web.api.pdd.consegnaFattura.fattoreRispedizione", true));
		
			this.intervalloRispedizioneDefault = Integer.parseInt(this.getProperty("org.govmix.proxy.pcc.intervalloRispedizioneDefault", true));
			this.intervalloSpedizioneEsito = Integer.parseInt(this.getProperty("org.govmix.proxy.pcc.intervalloSpedizioneEsito", true));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getUrlPCC() {
		return urlPCC;
	}
	public String getUsernamePCC() {
		return usernamePCC;
	}
	public String getPasswordPCC() {
		return passwordPCC;
	}
	public String getVersioneApplicativa() {
		return versioneApplicativa;
	}
	public String getIdEgovRichiestaHeader() {
		return idEgovRichiestaHeader;
	}
	public String getSistemaRichiedenteCruscotto() {
		return sistemaRichiedenteCruscotto;
	}
	public String getUtenteRichiedenteCruscotto() {
		return utenteRichiedenteCruscotto;
	}
	public String getSistemaRichiedenteGestionale() {
		return sistemaRichiedenteGestionale;
	}
	public String getUtenteRichiedenteGestionale() {
		return utenteRichiedenteGestionale;
	}

	public String getUrlWadlEndpointEnte() {
		return urlWadlEndpointEnte;
	}

	public String getUrlWadlEndpointPdd() {
		return urlWadlEndpointPdd;
	}

	public String getUrlWsdlFattureWS() {
		return urlWsdlFattureWS;
	}

	public String getUrlWsdlTracceWS() {
		return urlWsdlTracceWS;
	}
	public int getMaxTentativiRispedizione() {
		return maxTentativiRispedizione;
	}
	public Integer getIntervalloRispedizioneDefault() {
		return intervalloRispedizioneDefault;
	}
	public int getFattoreRispedizione() {
		return fattoreRispedizione;
	}

	public Integer getIntervalloSpedizioneEsito() {
		return intervalloSpedizioneEsito;
	}

}