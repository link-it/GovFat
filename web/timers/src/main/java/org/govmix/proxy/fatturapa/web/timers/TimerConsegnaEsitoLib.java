/*
 * ProxyFatturaPA - Gestione del formato Fattura Elettronica 
 * http://www.gov4j.it/fatturapa
 * 
 * Copyright (c) 2014-2019 Link.it srl (http://link.it). 
 * Copyright (c) 2014-2019 Provincia Autonoma di Bolzano (http://www.provincia.bz.it/). 
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.govmix.proxy.fatturapa.orm.IdTracciaSdi;
import org.govmix.proxy.fatturapa.orm.NotificaEsitoCommittente;
import org.govmix.proxy.fatturapa.orm.TracciaSDI;
import org.govmix.proxy.fatturapa.orm.constants.EsitoType;
import org.govmix.proxy.fatturapa.orm.constants.ScartoType;
import org.govmix.proxy.fatturapa.orm.constants.StatoConsegnaType;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.FatturaPassivaBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.NotificaEsitoCommittenteBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.TracciaSdIBD;
import org.govmix.proxy.fatturapa.web.commons.dao.DAOFactory;
import org.govmix.proxy.fatturapa.web.commons.notificaesitocommittente.InvioNotifica;
import org.govmix.proxy.fatturapa.web.commons.notificaesitocommittente.NotificaECRequest;
import org.govmix.proxy.fatturapa.web.commons.notificaesitocommittente.NotificaECResponse;
import org.govmix.proxy.fatturapa.web.commons.policies.IPolicyRispedizione;
import org.govmix.proxy.fatturapa.web.commons.policies.PolicyRispedizioneFactory;
import org.govmix.proxy.fatturapa.web.commons.sonde.Sonda;
import org.govmix.proxy.fatturapa.web.timers.utils.BatchProperties;

import it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType;

public class TimerConsegnaEsitoLib extends AbstractTimerLib {
	public TimerConsegnaEsitoLib(int limit, Logger log, boolean logQuery) throws Exception{
		super(limit, log, logQuery);
	}

	@Override
	public void execute() throws Exception {

		Connection connection = null;
		try {
			connection = DAOFactory.getInstance().getConnection();
			NotificaEsitoCommittenteBD notificaEsitoCommittenteBD = new NotificaEsitoCommittenteBD(log, connection, false);
			TracciaSdIBD tracciaBD = new TracciaSdIBD(log, connection, false);
			FatturaPassivaBD fatturaElettronicaBD = new FatturaPassivaBD(log, connection, false);

			Date limitDate = new Date(System.currentTimeMillis());

			this.log.info("Cerco NotificheEsitoCommittente da consegnare");
			long countNotifiche = notificaEsitoCommittenteBD.countNotifiche(limitDate);
			this.log.info("Trovate ["+countNotifiche+"] NotificheEsitoCommittente da consegnare");
			long countNotificheElaborate = 0; 

			if(countNotifiche > 0) {
				connection.setAutoCommit(false);

				this.log.info("Gestisco ["+countNotifiche+"] NotificheEsitoCommittente da consegnare, ["+this.limit+"] alla volta");
				List<NotificaEsitoCommittente> lstNotifiche = notificaEsitoCommittenteBD.findAllNotifiche(limitDate, 0, this.limit);

				BatchProperties properties = BatchProperties.getInstance();
				try {
					while(countNotificheElaborate < countNotifiche) {
						for(NotificaEsitoCommittente notifica : lstNotifiche) {

							try{
								InvioNotifica invioNotifica = new InvioNotifica(properties.getRicezioneEsitoURL(), properties.getRicezioneEsitoUsername(), properties.getRicezioneEsitoPassword());

								
								NotificaECRequest request = new NotificaECRequest();
								request.setNotifica(notifica);
								NotificaECResponse invioNotificaResponse = invioNotifica.invia(request);
								int esitoChiamata = invioNotificaResponse.getEsitoChiamata();

								if(esitoChiamata > 299) {
									IPolicyRispedizione policyRispedizione = PolicyRispedizioneFactory.getInstance().getPolicyRispedizione(notifica);
									long offset = policyRispedizione.getOffsetRispedizione();
									Date nextDate = new Date(System.currentTimeMillis() + offset);
									this.log.info("Risposta dallo SdI con codice ["+esitoChiamata+"]. ritentero' l'invio in data ["+nextDate+"]");
									notifica.setDataProssimaConsegnaSdi(nextDate);
									notifica.setDataUltimaConsegnaSdi(new Date());

									int tentativiConsegnaSdi = notifica.getTentativiConsegnaSdi();
									notifica.setTentativiConsegnaSdi(tentativiConsegnaSdi  + 1);
									notifica.setStatoConsegnaSdi(policyRispedizione.isRispedizioneAbilitata() ? StatoConsegnaType.IN_RICONSEGNA: StatoConsegnaType.ERRORE_CONSEGNA);
									notificaEsitoCommittenteBD.update(notifica);
									countNotificheElaborate++;
									continue;
									
								} else {
									
									String codiceDestinatario = fatturaElettronicaBD.get(notifica.getIdFattura()).getCodiceDestinatario();
									
									this.log.info("Risposta dallo SdI con codice ["+esitoChiamata+"].");
									notifica.setDataInvioSdi(new Date());
									
									TracciaSDI tracciaNotifica = invioNotificaResponse.getTracciaNotifica();
									tracciaNotifica.setCodiceDipartimento(codiceDestinatario);
									tracciaBD.insert(tracciaNotifica);
									IdTracciaSdi idTraccia = new IdTracciaSdi();
									idTraccia.setIdTraccia(tracciaNotifica.getId());
									notifica.setIdTracciaNotifica(idTraccia);

									EsitoType esitoFattura = null;
									if(esitoChiamata == 200) {

										ScartoEsitoCommittenteType scarto = invioNotificaResponse.getScarto();
										ScartoType scartoT;
										switch(scarto.getScarto()) {
										case EN00: scartoT = ScartoType.EN00;
										break;
										case EN01: scartoT = ScartoType.EN01;
										break;
										default: scartoT = ScartoType.EN00;
										break;

										}

										// Gestisco i vari casi in cui la notifica ottiene uno scarto ma questo non deve essere registrato.
										// Attualmente i casi di questo tipo sono due:
										if(ScartoType.EN01.equals(scartoT)) { 
											this.log.info("Risposta dallo SdI con Scarto Note["+scarto.getNote()+"]. Numero tentativi di consegna["+notifica.getTentativiConsegnaSdi()+"].");
											if(scarto.getNote() != null && scarto.getNote().contains(properties.getMsgNotificaGiaPervenuta())) {
												// 1) Notifica gia' pervenuta al sistema di interscambio: significa che la notifica e' arrivata allo SdI
												// ma il proxy non e' riuscito a riceverne comunicazione.
												this.log.info("Risposta dallo SdI con Scarto Note["+scarto.getNote()+"]. Considero il precedente tentativo di invio come andato a buon fine. Inserisco l'esito");

												switch(notifica.getEsito()) {
												case EC01: esitoFattura = EsitoType.INVIATA_ACCETTATO;
												break;
												case EC02: esitoFattura = EsitoType.INVIATA_RIFIUTATO;
												break;
												default: esitoFattura = EsitoType.INVIATA_ACCETTATO;
												break;
												}
//												} else {
//													this.log.info("Inserisco lo scarto nonostante il messaggio di Scarto Note ["+scarto.getNote()+"] in quanto non avevo ancora effettuato un tentativo di consegna");
//													notifica.setScarto(scartoT);
//													notifica.setScartoNote(scarto.getNote());
//													notifica.setScartoXml(invioNotifica.getScartoXML());
//
//													switch(esito) {
//													case EC01: esitoFattura = EsitoType.SCARTATA_ACCETTATO;
//													break;
//													case EC02: esitoFattura = EsitoType.SCARTATA_RIFIUTATO;
//													break;
//													default: esitoFattura = EsitoType.SCARTATA_ACCETTATO;
//													break;
//
//													}
//												}
											} else if(scarto.getNote() != null && scarto.getNote().contains(properties.getMsgAvvenutaRicezioneNonNotificata())) {
												// 2) Notifica arrivata allo SdI prima che il committente ricevesse comunicazione dell'avvenuta ricezione della fattura: 
												// si ritenta la rispedizione dopo 24 ore per dare il tempo allo SdI di inviare la comunicazione al committente
												Date domaniStessaOra = new Date(System.currentTimeMillis() + 1000*60*60*24);
												this.log.info("Risposta dallo SdI con Scarto Note["+scarto.getNote()+"]. ritentero' l'invio in data ["+domaniStessaOra+"]");
												notifica.setDataProssimaConsegnaSdi(domaniStessaOra);
												notifica.setDataUltimaConsegnaSdi(new Date());
//												int tentativiConsegnaSdi = notifica.getTentativiConsegnaSdi();
//												notifica.setTentativiConsegnaSdi(tentativiConsegnaSdi  + 1);
												notifica.setStatoConsegnaSdi(StatoConsegnaType.IN_RICONSEGNA);
												notificaEsitoCommittenteBD.update(notifica);
												countNotificheElaborate++;
												continue;

											} else {
												
												notifica.setScarto(scartoT);
												notifica.setScartoNote(scarto.getNote());
												
												TracciaSDI tracciaScarto = invioNotificaResponse.getTracciaScarto();
												tracciaScarto.setCodiceDipartimento(codiceDestinatario);
												tracciaBD.insert(tracciaScarto);
												IdTracciaSdi idTracciaScarto = new IdTracciaSdi();
												idTracciaScarto.setIdTraccia(invioNotificaResponse.getTracciaScarto().getId());
												notifica.setIdTracciaScarto(idTracciaScarto);


												switch(notifica.getEsito()) {
												case EC01: esitoFattura = EsitoType.SCARTATA_ACCETTATO;
												break;
												case EC02: esitoFattura = EsitoType.SCARTATA_RIFIUTATO;
												break;
												default: esitoFattura = EsitoType.SCARTATA_ACCETTATO;
												break;

												}
											}
											

										} else {

											notifica.setScarto(scartoT);
											notifica.setScartoNote(scarto.getNote());
											TracciaSDI tracciaScarto = invioNotificaResponse.getTracciaScarto();
											tracciaScarto.setCodiceDipartimento(codiceDestinatario);
											tracciaBD.insert(tracciaScarto);
											IdTracciaSdi idTracciaScarto = new IdTracciaSdi();
											idTracciaScarto.setIdTraccia(invioNotificaResponse.getTracciaScarto().getId());
											notifica.setIdTracciaScarto(idTracciaScarto);

											switch(notifica.getEsito()) {
											case EC01: esitoFattura = EsitoType.SCARTATA_ACCETTATO;
											break;
											case EC02: esitoFattura = EsitoType.SCARTATA_RIFIUTATO;
											break;
											default: esitoFattura = EsitoType.SCARTATA_ACCETTATO;
											break;

											}
										}

										this.log.info("Esito della fattura ["+esitoFattura.toString()+"]");

									} else if (esitoChiamata == 202) {
										this.log.info("Risposta dallo SdI con codice ["+esitoChiamata+"]. Aggiorno l'esito della fattura.");

										switch(notifica.getEsito()) {
										case EC01: esitoFattura = EsitoType.INVIATA_ACCETTATO;
										break;
										case EC02: esitoFattura = EsitoType.INVIATA_RIFIUTATO;
										break;
										default: esitoFattura = EsitoType.INVIATA_ACCETTATO;
										break;

										}

										this.log.info("Esito della fattura ["+esitoFattura.toString()+"]");

									}
									if(esitoFattura!=null)
										fatturaElettronicaBD.updateEsito(notifica.getIdFattura(), esitoFattura);
								}
								notifica.setStatoConsegnaSdi(StatoConsegnaType.CONSEGNATA);
								notifica.setDataInvioSdi(new Date());
								notificaEsitoCommittenteBD.update(notifica);
								
							} catch(Exception e) {
								this.log.error("Errore durante l'invio della notifica relativa alla fattura ["+notifica.getIdFattura().toJson()+"]:"+e.getMessage(), e);
								IPolicyRispedizione policyRispedizione = PolicyRispedizioneFactory.getInstance().getPolicyRispedizione(notifica);
								long offset = policyRispedizione.getOffsetRispedizione();
								Date nextDate = new Date(System.currentTimeMillis() + offset);
								notifica.setDataUltimaConsegnaSdi(new Date());
								notifica.setDataProssimaConsegnaSdi(nextDate);
								int tentativiConsegnaSdi = notifica.getTentativiConsegnaSdi();
								notifica.setTentativiConsegnaSdi(tentativiConsegnaSdi  + 1);
								notifica.setStatoConsegnaSdi(policyRispedizione.isRispedizioneAbilitata() ? StatoConsegnaType.IN_RICONSEGNA: StatoConsegnaType.ERRORE_CONSEGNA);
								notificaEsitoCommittenteBD.update(notifica);
							}
							
							connection.commit();
							countNotificheElaborate++;
						}
						this.log.info("Gestite ["+countNotificheElaborate+"\\"+countNotifiche+"] NotificheEsitoCommittente da consegnare");

						lstNotifiche = notificaEsitoCommittenteBD.findAllNotifiche(limitDate, 0, this.limit);
					}
					Sonda.getInstance().registraChiamataServizioOK(this.getTimerName());
				} catch(Exception e) {
					log.error("Errore durante la consegnaEsito:"+e.getMessage(), e);
				}

				this.log.info("Gestite ["+countNotificheElaborate+"\\"+countNotifiche+"] NotificheEsitoCommittente da consegnare. Fine");
//				connection.setAutoCommit(true);
			}

		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}

	}
}
