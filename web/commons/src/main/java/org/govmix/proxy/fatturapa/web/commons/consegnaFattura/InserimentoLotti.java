package org.govmix.proxy.fatturapa.web.commons.consegnaFattura;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.govmix.proxy.fatturapa.orm.Dipartimento;
import org.govmix.proxy.fatturapa.orm.LottoFatture;
import org.govmix.proxy.fatturapa.orm.constants.FormatoTrasmissioneType;
import org.govmix.proxy.fatturapa.orm.constants.StatoConsegnaType;
import org.govmix.proxy.fatturapa.orm.constants.StatoElaborazioneType;
import org.govmix.proxy.fatturapa.orm.constants.StatoInserimentoType;
import org.govmix.proxy.fatturapa.orm.constants.StatoProtocollazioneType;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.LottoBD;
import org.govmix.proxy.fatturapa.web.commons.consegnaFattura.InserimentoLottiException.CODICE;
import org.govmix.proxy.fatturapa.web.commons.consegnaFattura.InserimentoLottoResponse.ESITO;
import org.govmix.proxy.fatturapa.web.commons.dao.DAOFactory;

public class InserimentoLotti {

	private Logger log;
	private Map<String, Dipartimento> dipartimenti;
	
	public InserimentoLotti(Logger log) throws Exception {
		this.log = log;
	}
	
	public InserimentoLottoResponse inserisciLotto(List<InserimentoLottoRequest> requestList) {
		InserimentoLottoResponse inserimentoLottoResponse = new InserimentoLottoResponse();

		Connection connection = null;
		try {
			connection = DAOFactory.getInstance().getConnection();

			connection.setAutoCommit(false);
			LottoBD lottoBD = new LottoBD(log, connection, false);
			ConsegnaFattura consegnaFattura = new ConsegnaFattura(log, false, connection, false);

			for(InserimentoLottoRequest request: requestList) {
				String type = null;
				StatoElaborazioneType stato = null;
				if(request.getNomeFile().toLowerCase().endsWith("xml")) {
					if(!this.getDipartimento(request.getDipartimento()).getFirmaAutomatica()){
						throw new InserimentoLottiException(CODICE.ERRORE_FILE_NON_FIRMATO);
					}
					type= "XML";
					stato = StatoElaborazioneType.NON_FIRMATO;
				} else if(request.getNomeFile().toLowerCase().endsWith("p7m")) {
					if(this.getDipartimento(request.getDipartimento()).getFirmaAutomatica()){
						throw new InserimentoLottiException(CODICE.ERRORE_FILE_FIRMATO);
					}
					type= "P7M";
					stato = StatoElaborazioneType.FIRMA_OK;
				} else {
					throw new InserimentoLottiException(CODICE.ERRORE_FORMATO_FILE);
				}

				LottoFatture lotto = getLotto(request, type);
				lotto.setStatoElaborazioneInUscita(stato);
				
				insertLotto(lotto, lottoBD, consegnaFattura);
			}
			
			connection.commit();
			
			inserimentoLottoResponse.setEsito(ESITO.OK);
			
		} catch(InserimentoLottiException e) {
			if(connection != null) try {connection.rollback();} catch(Exception ex) {}
			
			this.log.error("Errore durante il caricamento dei lotti: " + e.getMessage(), e);
			inserimentoLottoResponse.setEsito(ESITO.KO);
			inserimentoLottoResponse.setEccezione(e);
		} catch(Exception e) {
			if(connection != null) try {connection.rollback();} catch(Exception ex) {}
			
			this.log.error("Errore durante il caricamento dei lotti: " + e.getMessage(), e);
			inserimentoLottoResponse.setEsito(ESITO.KO);
			inserimentoLottoResponse.setEccezione(new InserimentoLottiException(CODICE.ERRORE_GENERICO, e.getMessage()));
		} finally {
			if(connection != null) try {connection.close();} catch(Exception ex) {}
		}

		return inserimentoLottoResponse;
	}

	private void insertLotto(LottoFatture lotto, LottoBD lottoBD, ConsegnaFattura consegnaFattura) throws Exception {
		this.log.info("Inserimento del Lotto con identificativo ["+lotto.getIdentificativoSdi()+"]...");
		lottoBD.create(lotto);	
		this.log.info("Inserimento del Lotto con identificativo ["+lotto.getIdentificativoSdi()+"] completato");
		
		
		byte[] lottoXML = ConsegnaFatturaUtils.getLottoXml(lotto, this.log);
		String nomeFile = ConsegnaFatturaUtils.getNomeLottoXml(lotto, this.log);

		
		
		List<byte[]> fattureLst =ConsegnaFatturaUtils.getXmlWithSDIUtils(lottoXML);
		
		for (int i = 0; i < fattureLst.size(); i++) {

			if(fattureLst.get(i) == null) {
				throw new Exception("La fattura ricevuta in ingresso e' null");
			}
			
			ConsegnaFatturaParameters params = ConsegnaFatturaUtils.getParameters(lotto, (i+1), nomeFile, fattureLst.get(i));
			consegnaFattura.consegnaFattura(params);
		}

	}

	public InserimentoLottoResponse inserisciLottoSoloConservazione(List<InserimentoLottoSoloConservazioneRequest> requestList) {
		InserimentoLottoResponse inserimentoLottoResponse = new InserimentoLottoResponse();
		
		Connection connection = null;
		
		try {
			connection = DAOFactory.getInstance().getConnection();
			connection.setAutoCommit(false);

			LottoBD lottoBD = new LottoBD(log, connection, false);
			ConsegnaFattura consegnaFattura = new ConsegnaFattura(log, false, connection, false);

			for(InserimentoLottoSoloConservazioneRequest request: requestList) {
				String type = null;
				if(request.getNomeFile().toLowerCase().endsWith("xml")) {
					throw new InserimentoLottiException(CODICE.ERRORE_FILE_NON_FIRMATO_CONSERVAZIONE);
				} else if(request.getNomeFile().toLowerCase().endsWith("p7m")) {
					type= "P7M";
				} else {
					throw new InserimentoLottiException(CODICE.ERRORE_FORMATO_FILE);
				}

				LottoFatture lotto = getLotto(request, type);
				lotto.setStatoElaborazioneInUscita(StatoElaborazioneType.SPEDIZIONE_NON_ATTIVA);
				lotto.setProtocollo(request.getNumeroProtocollo() + "/" + request.getAnnoProtocollo() + "/" + request.getRegistroProtocollo());
				
				insertLotto(lotto, lottoBD, consegnaFattura);
			}
			connection.commit();
			inserimentoLottoResponse.setEsito(ESITO.OK);
		} catch(InserimentoLottiException e) {
			if(connection != null) try {connection.rollback();} catch(Exception ex) {}

			this.log.error("Errore durante il caricamento dei lotti: " + e.getMessage(), e);
			inserimentoLottoResponse.setEsito(ESITO.KO);
			inserimentoLottoResponse.setEccezione(e);
		} catch(Exception e) {
			if(connection != null) try {connection.rollback();} catch(Exception ex) {}
			
			this.log.error("Errore durante il caricamento dei lotti: " + e.getMessage(), e);
			inserimentoLottoResponse.setEsito(ESITO.KO);
			inserimentoLottoResponse.setEccezione(new InserimentoLottiException(CODICE.ERRORE_GENERICO, e.getMessage()));
		} finally {
			if(connection != null) try {connection.close();} catch(Exception ex) {}
		}
		
		return inserimentoLottoResponse;
	}
	
	private LottoFatture getLotto(InserimentoLottoRequest req, String type) throws Exception {
		
		
		ConsegnaFatturaParameters params = null;
		Integer identificativo = generaIdentificativo();
		String messageId = identificativo + "";

		try {

			params = ConsegnaFatturaUtils.getParameters(identificativo, req.getNomeFile(),
							type, null,
							messageId,
							false,
							req.getXml());
			
			params.validate(true);
		} catch(Exception e) {
			throw new InserimentoLottiException(CODICE.PARAMETRI_NON_VALIDI);
		}

		LottoFatture lotto = new LottoFatture();

		lotto.setFormatoArchivioInvioFattura(params.getFormatoArchivioInvioFattura());
		lotto.setCedentePrestatoreCodice(params.getCedentePrestatore().getIdCodice());
		lotto.setCedentePrestatorePaese(params.getCedentePrestatore().getIdPaese());
		lotto.setCedentePrestatoreCodiceFiscale(params.getCedentePrestatore().getCodiceFiscale());
		lotto.setCedentePrestatoreCognome(params.getCedentePrestatore().getCognome());
		lotto.setCedentePrestatoreNome(params.getCedentePrestatore().getNome());
		lotto.setCedentePrestatoreDenominazione(params.getCedentePrestatore().getDenominazione());

		lotto.setCessionarioCommittenteCodice(params.getCessionarioCommittente().getIdCodice());
		lotto.setCessionarioCommittentePaese(params.getCessionarioCommittente().getIdPaese());
		lotto.setCessionarioCommittenteCodiceFiscale(params.getCessionarioCommittente().getCodiceFiscale());
		lotto.setCessionarioCommittenteCognome(params.getCessionarioCommittente().getCognome());
		lotto.setCessionarioCommittenteNome(params.getCessionarioCommittente().getNome());
		lotto.setCessionarioCommittenteDenominazione(params.getCessionarioCommittente().getDenominazione());

		if(params.getTerzoIntermediarioOSoggettoEmittente() != null) {
			lotto.setTerzoIntermediarioOSoggettoEmittenteCodice(params.getTerzoIntermediarioOSoggettoEmittente().getIdCodice());
			lotto.setTerzoIntermediarioOSoggettoEmittentePaese(params.getTerzoIntermediarioOSoggettoEmittente().getIdPaese());
			lotto.setTerzoIntermediarioOSoggettoEmittenteCodiceFiscale(params.getTerzoIntermediarioOSoggettoEmittente().getCodiceFiscale());
			lotto.setTerzoIntermediarioOSoggettoEmittenteCognome(params.getTerzoIntermediarioOSoggettoEmittente().getCognome());
			lotto.setTerzoIntermediarioOSoggettoEmittenteNome(params.getTerzoIntermediarioOSoggettoEmittente().getNome());
			lotto.setTerzoIntermediarioOSoggettoEmittenteDenominazione(params.getTerzoIntermediarioOSoggettoEmittente().getDenominazione());
		}

		lotto.setIdentificativoSdi(params.getIdentificativoSdI());

		lotto.setCodiceDestinatario(req.getDipartimento());
		lotto.setFormatoTrasmissione(FormatoTrasmissioneType.valueOf(params.getFormatoFatturaPA()));

		lotto.setNomeFile(params.getNomeFile());
		lotto.setMessageId(params.getMessageId());

		lotto.setXml(params.getXml());
		lotto.setFatturazioneAttiva(true);
		
		lotto.setDataRicezione(new Date());
		lotto.setStatoConsegna(StatoConsegnaType.NON_CONSEGNATA);
		lotto.setStatoProtocollazione(StatoProtocollazioneType.NON_PROTOCOLLATA);
		lotto.setStatoInserimento(StatoInserimentoType.NON_INSERITO);

		return lotto;
	}

	private static String currentMinute = "";
	private static int counter = 0;
	private static int padding = 3;
	
	private static synchronized Integer generaIdentificativo() {
		SimpleDateFormat sdf = new SimpleDateFormat("dHHmm");
		String format = sdf.format(new Date());
		if(!currentMinute.equals(format)) {
			counter = 0;
			currentMinute = format;
		}
		
		String paddedCounter = "";
		
		for(int i=(counter + "").length(); i<padding; i++)
			paddedCounter += "0";
		
		paddedCounter += counter;
		counter++;
		return Integer.parseInt(format + "" + paddedCounter);
		
	}

	private Dipartimento getDipartimento(String codice) {
		if(this.dipartimenti != null && this.dipartimenti.containsKey(codice)) {
			return this.dipartimenti.get(codice);
		}
		return null;
	}
	
	public void setDipartimenti(List<Dipartimento> dipartimenti) {
		this.dipartimenti = new HashMap<String, Dipartimento>();
		if(dipartimenti != null) {
			for(Dipartimento dipartimento: dipartimenti) {
				this.dipartimenti.put(dipartimento.getCodice(), dipartimento);
			}
		}
	}
}
