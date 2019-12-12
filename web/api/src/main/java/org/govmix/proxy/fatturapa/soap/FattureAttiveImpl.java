/**
 * 
 */
package org.govmix.proxy.fatturapa.soap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;
import org.govmix.fatturapa.AuthorizationFault_Exception;
import org.govmix.fatturapa.FattureAttive;
import org.govmix.fatturapa.GenericFault_Exception;
import org.govmix.fatturapa.IdentificativoInternoTipo;
import org.govmix.fatturapa.IdentificativoSDITipo;
import org.govmix.fatturapa.IdentificativoUOTipo;
import org.govmix.fatturapa.InviaFatturaRichiestaTipo;
import org.govmix.fatturapa.InviaFatturaRispostaTipo;
import org.govmix.fatturapa.NotificheTipo;
import org.govmix.fatturapa.ProtocolloTipo;
import org.govmix.fatturapa.RiceviNotificheRichiestaTipo;
import org.govmix.fatturapa.RiceviNotificheRispostaTipo;
import org.govmix.proxy.fatturapa.orm.Dipartimento;
import org.govmix.proxy.fatturapa.orm.FatturaElettronica;
import org.govmix.proxy.fatturapa.orm.IdFattura;
import org.govmix.proxy.fatturapa.orm.TracciaSDI;
import org.govmix.proxy.fatturapa.orm.constants.StatoElaborazioneType;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.DipartimentoBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.FatturaAttivaBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.TracciaSdIBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.filter.TracciaSdIFilter;
import org.govmix.proxy.fatturapa.web.commons.consegnaFattura.InserimentoLotti;
import org.govmix.proxy.fatturapa.web.commons.consegnaFattura.InserimentoLottoRequest;
import org.govmix.proxy.fatturapa.web.commons.consegnaFattura.InserimentoLottoResponse;
import org.govmix.proxy.fatturapa.web.commons.fatturaattiva.EsitoInvioFattura.ESITO;
import org.govmix.proxy.fatturapa.web.commons.utils.CommonsProperties;
import org.govmix.proxy.fatturapa.web.commons.utils.LoggerManager;
import org.openspcoop2.generic_project.exception.NotFoundException;

public class FattureAttiveImpl implements FattureAttive {

	@Resource 
	private WebServiceContext wsContext;
	private String getPrincipal() throws Exception {
		List<String> principals = ((Map<Object, List<String>>)wsContext.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS)).get("PRINCIPAL_PROXY");
		
		if(principals != null && principals.size() > 0) {
			
			return principals.get(0);
		} else {
			throw new Exception("Principal utente non trovato");
		}

	}

	private List<StatoElaborazioneType> fatturaInviataSdi;
	private Logger log;
	private boolean modalitaPushRichiesta;
	private boolean firmaLottoEsteroNecessaria;
	

	public FattureAttiveImpl(boolean modalitaPushRichiesta, boolean firmaLottoEsteroNecessaria) throws Exception {
		this.log = LoggerManager.getEndpointFattureAttiveLogger();
		this.modalitaPushRichiesta = modalitaPushRichiesta;
		this.firmaLottoEsteroNecessaria = firmaLottoEsteroNecessaria;
		this.fatturaInviataSdi = new ArrayList<StatoElaborazioneType>();
		
		this.fatturaInviataSdi.add(StatoElaborazioneType.RICEVUTA_DALLO_SDI);
		this.fatturaInviataSdi.add(StatoElaborazioneType.RICEVUTO_SCARTO_SDI);
		this.fatturaInviataSdi.add(StatoElaborazioneType.RICEVUTA_DAL_DESTINATARIO);
		this.fatturaInviataSdi.add(StatoElaborazioneType.IMPOSSIBILITA_DI_RECAPITO);
		this.fatturaInviataSdi.add(StatoElaborazioneType.MANCATA_CONSEGNA);
		this.fatturaInviataSdi.add(StatoElaborazioneType.RICEVUTA_DECORRENZA_TERMINI);
		this.fatturaInviataSdi.add(StatoElaborazioneType.RICEVUTO_ESITO_CEDENTE_PRESTATORE_ACCETTAZIONE);
		this.fatturaInviataSdi.add(StatoElaborazioneType.RICEVUTO_ESITO_CEDENTE_PRESTATORE_RIFIUTO);
		this.log.info("Init fattureAttive Service completato. Info versione: " + CommonsProperties.getInstance(this.log).getInfoVersione());
	}

	@Override
	public InviaFatturaRispostaTipo inviaFattura(InviaFatturaRichiestaTipo inviaFatturaRichiestaTipo)
			throws GenericFault_Exception, AuthorizationFault_Exception {

		this.log.info("Invoke inviaFattura...");

		try {
			InserimentoLotti inserimento = new InserimentoLotti(this.log, this.modalitaPushRichiesta, this.firmaLottoEsteroNecessaria);

			inserimento.setDipartimenti(new DipartimentoBD(this.log).getListaDipartimentiUtente(getPrincipal()));
			List<InserimentoLottoRequest> requestList = new ArrayList<InserimentoLottoRequest>();
			InserimentoLottoRequest request = new InserimentoLottoRequest();

			request.setDipartimento(inviaFatturaRichiestaTipo.getCodiceUO());
			request.setNomeFile(inviaFatturaRichiestaTipo.getNomeFileFattura());
			request.setXml(getBytes(inviaFatturaRichiestaTipo.getFileFattura()));
			if(inviaFatturaRichiestaTipo.getInfoDocumentale()!=null) {
				request.setIdDocumentale(process(inviaFatturaRichiestaTipo.getInfoDocumentale().getIdDocumentale()));
				request.setProtocollo(inviaFatturaRichiestaTipo.getInfoDocumentale().getDataProtocollo() + "/" + inviaFatturaRichiestaTipo.getInfoDocumentale().getNumeroProtocollo());
			}
			
			
			requestList.add(request);
			InserimentoLottoResponse inserisciLotto = inserimento.inserisciLotto(requestList);

			if(ESITO.OK.toString().equals(inserisciLotto.getEsito().toString())) {
				this.log.info("inviaFattura completata con successo");
				InviaFatturaRispostaTipo inviaFatturaRispostaTipo = new InviaFatturaRispostaTipo();
				IdentificativoInternoTipo value = new IdentificativoInternoTipo();
				value.setId(inserisciLotto.getLstIdentificativoEfatt().get(0).getIdentificativoSdi()+"");
				inviaFatturaRispostaTipo.setIdentificativoInterno(value);
				return inviaFatturaRispostaTipo;
			} else {
				throw inserisciLotto.getEccezione();
			}
		} catch(Exception e) {
			this.log.error("inviaFattura completata con errore: "  + e.getMessage(), e);
			throw new GenericFault_Exception(e.getMessage(), e);
		}
	}

	private String process(String idDocumentale) {
		return "<ns2:idProtocollo xmlns:ns2=\"http://webservice.fascicoloinformatico.gvcc.net/\"><uuid>"+idDocumentale+"</uuid></ns2:idProtocollo>";
	}

	@Override
	public RiceviNotificheRispostaTipo riceviNotifiche(RiceviNotificheRichiestaTipo richiesta)
			throws GenericFault_Exception, AuthorizationFault_Exception {
		this.log.info("Invoke riceviNotifiche...");

		try {
			RiceviNotificheRispostaTipo risposta = new RiceviNotificheRispostaTipo();
			
			FatturaAttivaBD fatturaBD = new FatturaAttivaBD(this.log);
			
			String principal = getPrincipal();
			List<Dipartimento> listaDipartimentiUtente = new DipartimentoBD(this.log).getListaDipartimentiUtente(principal);
			
			List<String> listaCodDipartimentiUtente = new ArrayList<String>();
			for(Dipartimento d: listaDipartimentiUtente) {
				listaCodDipartimentiUtente.add(d.getCodice());
			}
			FatturaElettronica fattura = null;
			try {
				
				if(richiesta.getIdentificativoSDI()!=null) {
					IdFattura id = new IdFattura(true);
					id.setIdentificativoSdi(richiesta.getIdentificativoSDI().getIdSDI().toString());
					id.setPosizione(richiesta.getIdentificativoSDI().getPosizione().intValue());
					fattura = fatturaBD.get(id);
				} else if(richiesta.getIdentificativoUO()!=null) {
					fattura = fatturaBD.findByCodDipartimentoNumeroData(richiesta.getIdentificativoUO().getCodiceUO(),richiesta.getIdentificativoUO().getNumeroFattura(),richiesta.getIdentificativoUO().getDataFattura());
				} else if(richiesta.getIdentificativoInterno()!=null) {
					fattura = fatturaBD.findByMessageId(richiesta.getIdentificativoInterno().getId());
				} else {
					throw new Exception("Impossibile identificare la fattura");					
				}
				
				IdentificativoUOTipo idUO = new IdentificativoUOTipo();
				idUO.setCodiceUO(fattura.getCodiceDestinatario());
				idUO.setDataFattura(fattura.getData());
				idUO.setNumeroFattura(fattura.getNumero());
				risposta.setIdentificativoUO(idUO);
				
				IdentificativoInternoTipo idInterno = new IdentificativoInternoTipo();
				idInterno.setId(fattura.getMessageId());
				risposta.setIdentificativoInterno(idInterno);
				
				if(this.fatturaInviataSdi.contains(fattura.getLottoFatture().getStatoElaborazioneInUscita())) {
					IdentificativoSDITipo idSdi = new IdentificativoSDITipo();
					idSdi.setIdSDI(BigDecimal.valueOf(fattura.getLottoFatture().getIdentificativoSdi()));
					idSdi.setPosizione(BigInteger.valueOf(fattura.getPosizione()));
					risposta.setIdentificativoSDI(idSdi);
				}

			} catch(NotFoundException e) {
				throw new Exception("Fattura non trovata");
			}

			if(!listaCodDipartimentiUtente.contains(fattura.getCodiceDestinatario())) {
				throw new AuthorizationFault_Exception("L'utente ["+principal+"] non e' abilitato a visualizzare le notifiche per questa fattura");
			}

			if(fattura.getProtocollo()!=null) {
				String[] split = fattura.getProtocollo().split("/");
				if(split.length == 3) {
					ProtocolloTipo protocollo = new ProtocolloTipo();
					protocollo.setNumero(Integer.parseInt(split[0].trim()));
					protocollo.setAnno(Integer.parseInt(split[1].trim()));
					protocollo.setRegistro(split[2].trim());
					risposta.setProtocollo(protocollo);
				}
			}
			TracciaSdIBD tracciaBD = new TracciaSdIBD(this.log);
			
			TracciaSdIFilter filter = tracciaBD.newFilter();
			filter.setIdentificativoSdi(fattura.getIdentificativoSdi());
			List<TracciaSDI> tracce = tracciaBD.findAll(filter);
			
			if(tracce != null && !tracce.isEmpty()) {
				NotificheTipo notifiche = new NotificheTipo();
				boolean add = false;
				for(TracciaSDI traccia: tracce) {
					switch(traccia.getTipoComunicazione()) {
					case AT:
						notifiche.setAttestazioneTrasmissioneFattura(getRaw(traccia));
						add = true;
						break;
					case DT_ATT:
						notifiche.setNotificaDecorrenzaTermini(getRaw(traccia));
						add = true;
						break;
					case DT_PASS: //Solo fatturazione passiva
						break;
					case EC: //Solo fatturazione passiva
						break;
					case FAT_IN: //Solo fatturazione passiva
						break;
					case FAT_OUT: //traccia della fattura
						break;
					case MC:
						notifiche.setNotificaMancataConsegna(getRaw(traccia));
						add = true;
						break;
					case MT: //Metadati non gestiti
						break;
					case NE:
						if(traccia.getPosizione()==null || traccia.getPosizione().equals(fattura.getPosizione())) { // la posizione e' opzionale
							notifiche.setNotificaEsito(getRaw(traccia));
							add = true;
						}
						break;
					case NS:
						notifiche.setNotificaScarto(getRaw(traccia));
						add = true;
						break;
					case RC:
						notifiche.setRicevutaConsegna(getRaw(traccia));
						add = true;
						break;
					case SE: //Solo fatturazione passiva
						break;
					default:
						break;}
				}
				if(add)
					risposta.setNotifiche(notifiche);
			}
			
			this.log.info("riceviNotifiche completata con successo");
			return risposta;
		} catch(AuthorizationFault_Exception e) {
			this.log.error("riceviNotifiche completata con errore di autorizzazione: "  + e.getMessage(), e);
			throw e;
		} catch(Exception e) {
			this.log.error("riceviNotifiche completata con errore: "  + e.getMessage(), e);
			throw new GenericFault_Exception(e.getMessage(), e);
		}
	}
	
	private byte[] getBytes(byte[] bytes) {
		return bytes;
	}

//	private byte[] getBytes(DataHandler dataHandler) throws IOException {
//		ByteArrayOutputStream baos = null;
//		try {
//			baos = new ByteArrayOutputStream();
//			FileSystemUtilities.copy(dataHandler.getInputStream(), baos);
//			
//			return baos.toByteArray();
//		} finally {
//			if(baos != null) {
//				try {
//					baos.flush();
//					baos.close();
//				} catch(Exception e) {}
//			}
//		}
//
//	}

	private static byte[] getRaw(TracciaSDI traccia) {
		return traccia.getRawData();
	}

//	private static DataHandler getRaw(TracciaSDI traccia) throws IOException {
//		InputStreamDataSource isds = new InputStreamDataSource(traccia.getNomeFile(), traccia.getContentType(), traccia.getRawData());
//		return new DataHandler(isds);
//	}

}
