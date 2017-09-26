package org.govmix.proxy.fatturapa.web.timers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.apache.log4j.Logger;
import org.apache.soap.encoding.soapenc.Base64;
import org.govmix.proxy.fatturapa.orm.TracciaSDI;
import org.govmix.proxy.fatturapa.orm.constants.StatoProtocollazioneType;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.TracciaSdIBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.filter.TracciaSdIFilter;
import org.govmix.proxy.fatturapa.web.commons.utils.CostantiProtocollazione;
import org.govmix.proxy.fatturapa.web.commons.utils.Endpoint;
import org.govmix.proxy.fatturapa.web.commons.utils.EndpointSelector;

public class ProtocollazioneNotifiche implements IWorkFlow<TracciaSDI> {

	private Logger log;
	private int limit;
	private TracciaSdIBD tracciaSdiBD;
	private Date limitDate;
	private EndpointSelector endpointSelector;

	@Override
	public void init(Logger log, Connection connection, int limit) throws Exception {
		this.log = log;
		this.limit = limit;
		this.limitDate = new Date();
		this.tracciaSdiBD = new TracciaSdIBD(log, connection, false);
		this.endpointSelector = new EndpointSelector(log, connection, false);
	}

	@Override
	public long count() throws Exception {
		return this.tracciaSdiBD.count(this.newFilter());
	}

	private TracciaSdIFilter newFilter() {
		TracciaSdIFilter filter = this.tracciaSdiBD.newFilter();
		filter.setDataProssimaProtocollazioneMax(this.limitDate);
		filter.setStatoProtocollazione(StatoProtocollazioneType.NON_PROTOCOLLATA);
		filter.setDaProtocollare(true);
		filter.setOffset(0);
		filter.setLimit(this.limit);
		return filter;
	}

	@Override
	public List<TracciaSDI> getNextLista() throws Exception {
		return this.tracciaSdiBD.findAll(this.newFilter());
	}

	@Override
	public void process(TracciaSDI tracciaSDI) throws Exception {
		StatoProtocollazioneType nextStatoOK = StatoProtocollazioneType.PROTOCOLLATA;
		StatoProtocollazioneType nextStatoKO = StatoProtocollazioneType.ERRORE_PROTOCOLLAZIONE;
		this.log.debug("Elaboro la traccia con id ["+tracciaSDI.getId()+"]");
		
		Endpoint endpoint = endpointSelector.findEndpoint(tracciaSDI);
		
		URL urlOriginale = endpoint.getEndpoint().toURL();
		
		this.log.debug("Spedisco la traccia ["+tracciaSDI.getId()+"] all'endpoint ["+urlOriginale.toString()+"]");
		
		URL url = new URL(urlOriginale.toString() + "/protocollazioneRicevute");

		URLConnection conn = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) conn;
		String errore = null;
		boolean esitoPositivo = false;
		String response = null;
		try{
			httpConn.setRequestProperty(CostantiProtocollazione.IDENTIFICATIVO_SDI_HEADER_PARAM, ""+tracciaSDI.getIdentificativoSdi());
			
			if(tracciaSDI.getNumeroFattura() != null)
				httpConn.setRequestProperty(CostantiProtocollazione.NUMERO_HEADER_PARAM, tracciaSDI.getNumeroFattura());

			httpConn.setRequestProperty(CostantiProtocollazione.NOME_FILE_HEADER_PARAM, ""+tracciaSDI.getNomeFile());
			httpConn.setRequestProperty(CostantiProtocollazione.DESTINATARIO_HEADER_PARAM, tracciaSDI.getLottoFatture().getCodiceDestinatario());
			
			httpConn.setRequestProperty("Content-Type", tracciaSDI.getContentType());
			

			if(endpoint.getUsername() != null && endpoint.getPassword()!= null) {
				String auth = endpoint.getUsername() + ":" + endpoint.getPassword(); 
				String authentication = "Basic " + Base64.encode(auth.getBytes());

				httpConn.setRequestProperty("Authorization", authentication);
			}

			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			
			httpConn.setRequestMethod("POST");								

			httpConn.getOutputStream().write(tracciaSDI.getRawData());
			httpConn.getOutputStream().flush();
			httpConn.getOutputStream().close();
			
			esitoPositivo = httpConn.getResponseCode() < 299;
			
			response = IOUtils.readStringFromStream(httpConn.getInputStream());
			
			if(esitoPositivo) {
				this.tracciaSdiBD.updateStatoProtocollazione(tracciaSDI, nextStatoOK);
				this.log.debug("Elaboro la traccia ["+tracciaSDI.getId()+"], stato ["+tracciaSDI.getStatoProtocollazione()+"] -> ["+nextStatoOK+"]");
			} else {
				this.tracciaSdiBD.updateStatoProtocollazione(tracciaSDI, nextStatoKO);
				this.log.debug("Elaboro la traccia ["+tracciaSDI.getId()+"], stato ["+tracciaSDI.getStatoProtocollazione()+"] -> ["+nextStatoKO+"]");
			}
		} catch(Exception e) {
			this.log.error("Errore durante la protocollazione della traccia: " + e.getMessage(), e);
			this.tracciaSdiBD.updateStatoProtocollazione(tracciaSDI, nextStatoKO);
			this.log.debug("Elaboro la traccia ["+tracciaSDI.getId()+"], stato ["+tracciaSDI.getStatoProtocollazione()+"] -> ["+nextStatoKO+"]");
		}
	}

}
