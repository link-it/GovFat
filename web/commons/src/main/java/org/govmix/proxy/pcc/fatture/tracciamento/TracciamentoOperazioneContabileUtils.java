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
package org.govmix.proxy.pcc.fatture.tracciamento;


import it.tesoro.fatture.DettaglioMovimentoTipo;
import it.tesoro.fatture.PianoComunicazioneScadenzaTipo;
import it.tesoro.fatture.QueryDatiFatturaRispostaTipo;
import it.tesoro.fatture.RipartizioneAttualeTipo;
import it.tesoro.fatture.StatoContabileFatturaTipo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.govmix.pcc.fatture.ComunicaScadenzaTipo;
import org.govmix.pcc.fatture.ComunicazioneScadenzaTipo;
import org.govmix.pcc.fatture.ContabilizzazioneTipo;
import org.govmix.pcc.fatture.NaturaSpesaContabiliTipo;
import org.govmix.pcc.fatture.NaturaSpesaTipo;
import org.govmix.pcc.fatture.OperazioneContabilizzazioneTipo;
import org.govmix.pcc.fatture.OperazioneTipo;
import org.govmix.pcc.fatture.PagamentoStornoTipo;
import org.govmix.pcc.fatture.PagamentoTipo;
import org.govmix.pcc.fatture.StatoDebitoTipo;
import org.govmix.pcc.fatture.TipoOperazioneTipo;
import org.govmix.proxy.fatturapa.orm.IdFattura;
import org.govmix.proxy.fatturapa.orm.PccContabilizzazione;
import org.govmix.proxy.fatturapa.orm.PccPagamento;
import org.govmix.proxy.fatturapa.orm.PccScadenza;
import org.govmix.proxy.fatturapa.orm.constants.CausaleType;
import org.govmix.proxy.fatturapa.orm.constants.NaturaSpesaType;
import org.govmix.proxy.fatturapa.orm.constants.StatoDebitoType;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.FilterSortWrapper;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.PccContabilizzazioneBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.PccOperazioneContabileBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.PccPagamentoBD;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.PccScadenzaBD;
import org.govmix.proxy.fatturapa.web.commons.utils.TransformUtils;
import org.govmix.proxy.pcc.fatture.authorization.AuthorizationBeanResponse;
import org.govmix.proxy.pcc.fatture.utils.PccProperties;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.SortOrder;

public class TracciamentoOperazioneContabileUtils {

	private PccOperazioneContabileBD pccOperazioneContabileBD;
	private PccPagamentoBD pccPagamentoBD;
	private PccContabilizzazioneBD pccContabilizzazioneBD;
	private PccScadenzaBD scadenzaBD;
	private List<TipoOperazioneTipo> operazioniTracciabili;
	private Logger log;
	
	// riallineamento
	private Map<String, RiallineamentoBean> liq;
	private Map<String, RiallineamentoBean> sosp;
	private Map<String, RiallineamentoBean> noliq;
	private Map<String, RiallineamentoBean> pagamento;
	
	public TracciamentoOperazioneContabileUtils(Logger log) throws Exception {
		this.log = log;
		this.pccOperazioneContabileBD = new PccOperazioneContabileBD(log);
		this.pccPagamentoBD = new PccPagamentoBD(log);
		this.scadenzaBD = new PccScadenzaBD(log);
		this.pccContabilizzazioneBD = new PccContabilizzazioneBD(log);
		this.operazioniTracciabili = new ArrayList<TipoOperazioneTipo>();
		this.operazioniTracciabili.add(TipoOperazioneTipo.CO);
		this.operazioniTracciabili.add(TipoOperazioneTipo.CP);
		this.operazioniTracciabili.add(TipoOperazioneTipo.SP);
		this.operazioniTracciabili.add(TipoOperazioneTipo.CS);
		this.operazioniTracciabili.add(TipoOperazioneTipo.CCS);
		this.operazioniTracciabili.add(TipoOperazioneTipo.SC);
		
		this.liq = new HashMap<String, RiallineamentoBean>();
		this.liq.put("DebitiLiquidatiCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		this.liq.put("DebitiInAttesaDiLiquidazioneCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		this.liq.put("DebitiLiquidabiliNonCommercialiCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		
		this.liq.put("DebitiLiquidatiCA", new RiallineamentoBean(null, NaturaSpesaType.CA));
		this.liq.put("DebitiInAttesaDiLiquidazioneCA", new RiallineamentoBean(null, NaturaSpesaType.CA));
		this.liq.put("DebitiLiquidabiliNonCommercialiCA", new RiallineamentoBean(null, NaturaSpesaType.CA));
		
		this.liq.put("DebitiInAttesaDiLiquidazioneNA", new RiallineamentoBean(null, NaturaSpesaType.NA));
		
		
		this.sosp = new HashMap<String, RiallineamentoBean>();
		this.sosp.put("DebitiACaricoDiTerziCA", new RiallineamentoBean(CausaleType.PAGTERZI, NaturaSpesaType.CA));
		this.sosp.put("DebitiContestatiCA", new RiallineamentoBean(null, NaturaSpesaType.CA));
		this.sosp.put("DebitiInAttesaDiAccettazioneCA", new RiallineamentoBean(null, NaturaSpesaType.CA));
		this.sosp.put("DebitiInContenziosoCA", new RiallineamentoBean(CausaleType.CONT, NaturaSpesaType.CA));

		this.sosp.put("DebitiACaricoDiTerziCO", new RiallineamentoBean(CausaleType.PAGTERZI, NaturaSpesaType.CO));
		this.sosp.put("DebitiContestatiCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		this.sosp.put("DebitiInAttesaDiAccettazioneCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		this.sosp.put("DebitiInContenziosoCO", new RiallineamentoBean(CausaleType.CONT, NaturaSpesaType.CO));
		
		this.sosp.put("DebitiACaricoDiTerziNA", new RiallineamentoBean(CausaleType.PAGTERZI, NaturaSpesaType.NA));
		this.sosp.put("DebitiContestatiNA", new RiallineamentoBean(null, NaturaSpesaType.NA));
		this.sosp.put("DebitiInAttesaDiAccettazioneNA", new RiallineamentoBean(null, NaturaSpesaType.NA));
		this.sosp.put("DebitiInContenziosoNA", new RiallineamentoBean(CausaleType.CONT, NaturaSpesaType.NA));
		
		
		this.noliq = new HashMap<String, RiallineamentoBean>();
		this.noliq.put("DebitiNonRiconosciuti", new RiallineamentoBean(null, NaturaSpesaType.NA)); // CONT / ATTNC
		this.noliq.put("DebitiNonCommerciali", new RiallineamentoBean(CausaleType.IVARC, NaturaSpesaType.NA));
		this.noliq.put("DebitiPerInteressiMoratori", new RiallineamentoBean(null, NaturaSpesaType.NA));

		
		this.pagamento = new HashMap<String, RiallineamentoBean>();

		this.pagamento.put("PagamentiAgenziaEntrateCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		this.pagamento.put("PagamentiAgenziaRiscossioneCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		this.pagamento.put("PagamentiBancheCessionarieCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		this.pagamento.put("PagamentiFornitoriCO", new RiallineamentoBean(null, NaturaSpesaType.CO));
		
		this.pagamento.put("PagamentiAgenziaEntrateCA", new RiallineamentoBean(null, NaturaSpesaType.CA));
		this.pagamento.put("PagamentiAgenziaRiscossioneCA", new RiallineamentoBean(null, NaturaSpesaType.CA));
		this.pagamento.put("PagamentiBancheCessionarieCA", new RiallineamentoBean(null, NaturaSpesaType.CA));
		this.pagamento.put("PagamentiFornitoriCA", new RiallineamentoBean(null, NaturaSpesaType.CA));

	}

	public void tracciaOperazioneContabile(OperazioneTipo operaz, String sistemaRichiedente, String utenteRichiedente, IdFattura idFattura) throws Exception {

		if(this.operazioniTracciabili.contains(operaz.getTipoOperazione())) {

			switch(operaz.getTipoOperazione()) {
			case CCS: tracciaCancellazioneComunicazioniScadenza(operaz, idFattura);
			break;
			case CO: tracciaContabilizzazioneFattura(operaz, sistemaRichiedente, utenteRichiedente, idFattura);
			break;
			case CP: tracciaPagamento(operaz, idFattura);
			break;
			case CS: tracciaComunicazioneScadenza(operaz, sistemaRichiedente, utenteRichiedente, idFattura);
			break;
			case RC: //NOP
			break;
			case RF: //NOP
				break;
			case SC: tracciaStornoContabilizzazione(operaz, idFattura);
			break;
			case SP: tracciaStornoPagamento(operaz, idFattura);
			break;
			default:
				break;
			}
		}
	}

	private List<PccPagamento> tracciaStornoPagamento(OperazioneTipo operaz, IdFattura idFattura)  throws Exception {
		
		List<PccPagamento> lstPagamento = getStorno(operaz);
		for(PccPagamento pccPagamento: lstPagamento) { 
			pccPagamento.setIdFattura(idFattura);
			this.pccOperazioneContabileBD.create(pccPagamento);
		}
		return lstPagamento;
	}

	public double getImportoByIdFattura(IdFattura idFattura, String naturaSpesa) throws Exception {
		List<PccPagamento> pagamentiByIdFattura = this.pccPagamentoBD.getPagamentiByIdFattura(idFattura);
		double importo =0;
		for(PccPagamento pagamento: pagamentiByIdFattura) {
			if(pagamento.getNaturaSpesa().getValue().equals(naturaSpesa)) {
				importo += pagamento.getImportoPagato();
			}
		}
		
		return importo;
		
	}
	
	private void tracciaStornoContabilizzazione(OperazioneTipo operaz, IdFattura idFattura)  throws Exception {
		this.pccOperazioneContabileBD.deleteContabilizzazioni(idFattura);
	}

	private List<PccScadenza> tracciaComunicazioneScadenza(OperazioneTipo operaz, String sistemaRichiedente, String utenteRichiedente, IdFattura idFattura) throws Exception {

		this.pccOperazioneContabileBD.deleteScadenze(idFattura, false);

		List<PccScadenza> scadenzaLst = new ArrayList<PccScadenza>();

		for(ComunicazioneScadenzaTipo comunicazioneScadenza: operaz.getStrutturaDatiOperazione().getComunicazioneScadenza()) {
			PccScadenza scadenza = new PccScadenza();
			
			scadenza.setDataRichiesta(new Date());
			if(comunicazioneScadenza.getDataScadenza() != null)
				scadenza.setDataScadenza(comunicazioneScadenza.getDataScadenza());
			
			scadenza.setIdFattura(idFattura);
			
			if(comunicazioneScadenza.getImporto() != null) {
				scadenza.setImportoInScadenza(comunicazioneScadenza.getImporto().doubleValue());
				scadenza.setImportoIniziale(comunicazioneScadenza.getImporto().doubleValue());
			}
			
			scadenza.setPagatoRicontabilizzato(0d);
			scadenza.setSistemaRichiedente(sistemaRichiedente);
			scadenza.setUtenteRichiedente(utenteRichiedente);
			this.pccOperazioneContabileBD.create(scadenza);
			scadenzaLst.add(scadenza);
		}
		return scadenzaLst;
	}

	private List<PccPagamento> tracciaPagamento(OperazioneTipo operaz, IdFattura idFattura)  throws Exception {
		
		List<PccPagamento> lstPagamento = getPagamento(operaz);
		for(PccPagamento pccPagamento: lstPagamento) { 
			pccPagamento.setIdFattura(idFattura);
			this.pccOperazioneContabileBD.create(pccPagamento);
		}
		return lstPagamento;
	}

	private List<PccPagamento> getStorno(OperazioneTipo operaz) {

		List<PccPagamento> pccPagamentoLst = new ArrayList<PccPagamento>();

		for(PagamentoStornoTipo pagamentoInput: operaz.getStrutturaDatiOperazione().getPagamentoStorno()) {
			PccPagamento pccPagamento = new PccPagamento();
			double pagamentoStorno = pagamentoInput.getImportoStorno().doubleValue();
			
			if(pagamentoStorno > 0) {
				pagamentoStorno *= -1;
			}
			
			pccPagamento.setDataRichiesta(new Date());
			pccPagamento.setImportoPagato(pagamentoStorno);
			pccPagamento.setNaturaSpesa(NaturaSpesaType.toEnumConstant(pagamentoInput.getNaturaSpesa().value()));
			pccPagamento.setIdFiscaleIvaBeneficiario(pagamentoInput.getIdFiscaleIVABeneficiario());
			
			pccPagamentoLst.add(pccPagamento);
		}
		
		return pccPagamentoLst;
	}

	private List<PccPagamento> getPagamento(OperazioneTipo operaz) {
		
		List<PccPagamento> pccPagamentoLst = new ArrayList<PccPagamento>();

		for(PagamentoTipo pagamentoInput: operaz.getStrutturaDatiOperazione().getPagamento()) {
			
			PccPagamento pccPagamento = new PccPagamento();
			pccPagamento.setDataRichiesta(new Date());
			pccPagamento.setImportoPagato(pagamentoInput.getImportoPagato().doubleValue());
			pccPagamento.setNaturaSpesa(NaturaSpesaType.toEnumConstant(pagamentoInput.getNaturaSpesa().value()));
			pccPagamento.setCapitoliSpesa(pagamentoInput.getCapitoliSpesa());
			pccPagamento.setEstremiImpegno(pagamentoInput.getEstremiImpegno());
			
			if(pagamentoInput.getMandatoPagamento() != null) {
				pccPagamento.setNumeroMandato(pagamentoInput.getMandatoPagamento().getNumero());
				pccPagamento.setDataMandato(pagamentoInput.getMandatoPagamento().getDataMandatoPagamento());
			}
			
			pccPagamento.setIdFiscaleIvaBeneficiario(pagamentoInput.getIdFiscaleIVABeneficiario());
			pccPagamento.setCodiceCig(pagamentoInput.getCodiceCIG());
			pccPagamento.setCodiceCup(pagamentoInput.getCodiceCUP());
			
			pccPagamento.setDescrizione(pagamentoInput.getDescrizione());
			
			pccPagamentoLst.add(pccPagamento);
		}
		
		return pccPagamentoLst;
	}

	private void tracciaContabilizzazioneFattura(OperazioneTipo operaz, String sistemaRichiedente, String utenteRichiedente, IdFattura idFattura)  throws Exception {
		
		if(PccProperties.getInstance().getSistemaRichiedenteCruscotto().equals(sistemaRichiedente)) {
			this.pccContabilizzazioneBD.deleteByIdFatturaSistemaRichiedente(idFattura, sistemaRichiedente);			
		}
		
		for(ContabilizzazioneTipo contabilizzazione: operaz.getStrutturaDatiOperazione().getListaContabilizzazione()) {
			
			if(contabilizzazione.getImportoMovimento().doubleValue() != 0) {
				PccContabilizzazione pccContabilizzazione = new PccContabilizzazione();
				pccContabilizzazione.setDataRichiesta(new Date());
				
				if(TransformUtils.isDescrizioneRaw(contabilizzazione.getDescrizione())) {
					TransformUtils.populateContabilizzazione(pccContabilizzazione, contabilizzazione.getDescrizione());
				} else {
					pccContabilizzazione.setIdImporto(contabilizzazione.getIdentificativoMovimento());
					pccContabilizzazione.setDescrizione(contabilizzazione.getDescrizione());
					pccContabilizzazione.setSistemaRichiedente(sistemaRichiedente);
					pccContabilizzazione.setUtenteRichiedente(utenteRichiedente);
				}

				pccContabilizzazione.setImportoMovimento(contabilizzazione.getImportoMovimento().doubleValue());
				pccContabilizzazione.setNaturaSpesa(NaturaSpesaType.toEnumConstant(contabilizzazione.getNaturaSpesa().toString()));
				pccContabilizzazione.setCapitoliSpesa(contabilizzazione.getCapitoliSpesa());
				pccContabilizzazione.setStatoDebito(StatoDebitoType.toEnumConstant(contabilizzazione.getOperazione().getStatoDebito().value()));
				
				if(contabilizzazione.getOperazione().getCausale() != null)
					pccContabilizzazione.setCausale(CausaleType.toEnumConstant(contabilizzazione.getOperazione().getCausale()));
				
				pccContabilizzazione.setEstremiImpegno(contabilizzazione.getEstremiImpegno());
				pccContabilizzazione.setCodiceCig(contabilizzazione.getCodiceCIG());
				pccContabilizzazione.setCodiceCup(contabilizzazione.getCodiceCUP());
				pccContabilizzazione.setIdFattura(idFattura);
				this.pccOperazioneContabileBD.create(pccContabilizzazione);
			} else {
				this.pccContabilizzazioneBD.deleteByIdFatturaIdMovimento(idFattura, contabilizzazione.getIdentificativoMovimento());
			}
		}
	}

	private void tracciaCancellazioneComunicazioniScadenza(OperazioneTipo operaz, IdFattura idFattura)  throws Exception {
		this.pccOperazioneContabileBD.deleteScadenze(idFattura, false);
	}

	public List<ContabilizzazioneTipo> getContabilizzazioniByIdFatturaEDiversoSistemaRichiedente(IdFattura idFattura, String sistemaRichiedente) throws Exception {
		List<PccContabilizzazione> pccContabilizzazioneList = this.pccContabilizzazioneBD.getContabilizzazioniByIdFatturaEDiversoSistemaRichiedente(idFattura, sistemaRichiedente);
		List<ContabilizzazioneTipo> lst = new ArrayList<ContabilizzazioneTipo>();
		for(PccContabilizzazione contabilizzazione: pccContabilizzazioneList) {
			lst.add(toPcc(contabilizzazione));
		}
		return lst;
	}

	public List<ComunicazioneScadenzaTipo> getScadenzeByIdFattura(IdFattura idFattura) throws ServiceException {
		List<PccScadenza> pccScadenzaList = this.scadenzaBD.getScadenzeByIdFattura(idFattura);
		List<ComunicazioneScadenzaTipo> lst = new ArrayList<ComunicazioneScadenzaTipo>();
		for(PccScadenza scadenza: pccScadenzaList) {
			lst.add(toPcc(scadenza));
		}
		return lst;
	}


	private ContabilizzazioneTipo toPcc(PccContabilizzazione contabilizzazione) {
		ContabilizzazioneTipo cont = new ContabilizzazioneTipo();
		cont.setCapitoliSpesa(contabilizzazione.getCapitoliSpesa());
		cont.setCodiceCIG(contabilizzazione.getCodiceCig());
		cont.setCodiceCUP(contabilizzazione.getCodiceCup());
		cont.setDescrizione(contabilizzazione.getDescrizione());
		cont.setEstremiImpegno(contabilizzazione.getEstremiImpegno());
		cont.setIdentificativoMovimento(contabilizzazione.getIdImporto());
		cont.setImportoMovimento(new BigDecimal(contabilizzazione.getImportoMovimento()));
		NaturaSpesaContabiliTipo nat = null;
		switch(contabilizzazione.getNaturaSpesa()) {
		case CA: nat = NaturaSpesaContabiliTipo.CA;
			break;
		case CO: nat = NaturaSpesaContabiliTipo.CO;
			break;
		case NA: nat = NaturaSpesaContabiliTipo.NA;
			break;
		default:
			break;
		
		}
		cont.setNaturaSpesa(nat);
		OperazioneContabilizzazioneTipo op = new OperazioneContabilizzazioneTipo();
		
		if(contabilizzazione.getCausale() != null)
			op.setCausale(contabilizzazione.getCausale().getValue());
		
		op.setStatoDebito(StatoDebitoTipo.fromValue(contabilizzazione.getStatoDebito().getValue()));
		
		cont.setOperazione(op);
		
		return cont;
	}

	private ComunicazioneScadenzaTipo toPcc(PccScadenza scadenza) {
		ComunicazioneScadenzaTipo comunicazioneScadenza= new ComunicazioneScadenzaTipo();
		comunicazioneScadenza.setComunicaScadenza(ComunicaScadenzaTipo.SI);
		if(scadenza.getDataScadenza() != null)
			comunicazioneScadenza.setDataScadenza(scadenza.getDataScadenza());
		
		if(scadenza.getImportoInScadenza() != null)
			comunicazioneScadenza.setImporto(new BigDecimal(scadenza.getImportoInScadenza()));
		
		return comunicazioneScadenza;
	}

	public void riallineaDatiFattura(AuthorizationBeanResponse beanResponse, StatoContabileFatturaTipo statoContabileFatturaTipo) throws Exception {
		
		this.pccContabilizzazioneBD.deleteContabilizzazioni(beanResponse.getIdLogicoFattura());

		List<PccContabilizzazione> lstContabilizzazioni = getListaContabilizzazioni(statoContabileFatturaTipo.getDatiDocumento().getRipartizioneAttuale(), beanResponse);
		
		for(PccContabilizzazione pccContabilizzazione: lstContabilizzazioni) {
			this.pccOperazioneContabileBD.create(pccContabilizzazione);	
		}
		
		double pagato = statoContabileFatturaTipo.getDatiDocumento().getRipartizioneAttuale().getImportoPagato().doubleValue();

		this.pccPagamentoBD.deletePagamenti(beanResponse.getIdLogicoFattura());

		if(pagato > 0) {
			PccPagamento pagamento = new PccPagamento();
			pagamento.setIdFattura(beanResponse.getIdLogicoFattura());
			pagamento.setCodiceCig("NA");
			pagamento.setCodiceCup("NA");
			pagamento.setImportoPagato(pagato);
			pagamento.setIdFiscaleIvaBeneficiario(statoContabileFatturaTipo.getDatiDocumento().getIdFiscaleIVA());
			pagamento.setDataRichiesta(new Date());
			pagamento.setNaturaSpesa(NaturaSpesaType.NA);
	
			this.pccPagamentoBD.create(pagamento);
		}
		
	}

	private List<PccContabilizzazione> getListaContabilizzazioni(RipartizioneAttualeTipo ripartizioneAttualeTipo, AuthorizationBeanResponse beanResponse) {
		List<PccContabilizzazione> lst = new ArrayList<PccContabilizzazione>();
		
		String pccSistemaRichiedente = beanResponse.getSistemaRichiedente();
		String pccUtenteRichiedente = beanResponse.getUtenteRichiedente();
		int i = 0;
		if(ripartizioneAttualeTipo.getImportoNonLiquidato().doubleValue() > 0) {
			PccContabilizzazione pccContabilizzazione = new PccContabilizzazione();
			pccContabilizzazione.setIdImporto(pccSistemaRichiedente +"_" +(i++));
			pccContabilizzazione.setSistemaRichiedente(pccSistemaRichiedente);
			pccContabilizzazione.setUtenteRichiedente(pccUtenteRichiedente);
			pccContabilizzazione.setNaturaSpesa(NaturaSpesaType.NA);	

			pccContabilizzazione.setImportoMovimento(ripartizioneAttualeTipo.getImportoNonLiquidato().doubleValue());

			pccContabilizzazione.setStatoDebito(StatoDebitoType.NOLIQ);
			pccContabilizzazione.setCodiceCig("NA");
			pccContabilizzazione.setCodiceCup("NA");
			pccContabilizzazione.setDataRichiesta(new Date());

			pccContabilizzazione.setIdFattura(beanResponse.getIdLogicoFattura());
			lst.add(pccContabilizzazione);
		}
		
		if(ripartizioneAttualeTipo.getImportoSospeso().doubleValue() > 0) {
			PccContabilizzazione pccContabilizzazione = new PccContabilizzazione();
			pccContabilizzazione.setIdImporto(pccSistemaRichiedente +"_" +(i++));
			pccContabilizzazione.setSistemaRichiedente(pccSistemaRichiedente);
			pccContabilizzazione.setUtenteRichiedente(pccUtenteRichiedente);
			pccContabilizzazione.setNaturaSpesa(NaturaSpesaType.NA);	

			pccContabilizzazione.setImportoMovimento(ripartizioneAttualeTipo.getImportoSospeso().doubleValue());

			pccContabilizzazione.setStatoDebito(StatoDebitoType.SOSP);
			pccContabilizzazione.setCodiceCig("NA");
			pccContabilizzazione.setCodiceCup("NA");
			pccContabilizzazione.setDataRichiesta(new Date());
			pccContabilizzazione.setIdFattura(beanResponse.getIdLogicoFattura());
			lst.add(pccContabilizzazione);
		}
		
		double liquidato = ripartizioneAttualeTipo.getImportoLiquidato().doubleValue();
		
		if(liquidato > 0) {
			PccContabilizzazione pccContabilizzazione = new PccContabilizzazione();
			pccContabilizzazione.setIdImporto(pccSistemaRichiedente +"_" +(i++));
			pccContabilizzazione.setSistemaRichiedente(pccSistemaRichiedente);
			pccContabilizzazione.setUtenteRichiedente(pccUtenteRichiedente);
			pccContabilizzazione.setNaturaSpesa(NaturaSpesaType.NA);	

			pccContabilizzazione.setImportoMovimento(liquidato);

			pccContabilizzazione.setStatoDebito(StatoDebitoType.LIQ);
			pccContabilizzazione.setCodiceCig("NA");
			pccContabilizzazione.setCodiceCup("NA");
			pccContabilizzazione.setDataRichiesta(new Date());
			
			pccContabilizzazione.setIdFattura(beanResponse.getIdLogicoFattura());
			lst.add(pccContabilizzazione);
		}
		
		return lst;
	}

	public boolean existScadenze(IdFattura idFattura) throws Exception {
		return this.pccOperazioneContabileBD.existScadenze(idFattura);
	}

	public List<PccScadenza> getScadenze(IdFattura idFattura) throws Exception {
		return this.scadenzaBD.getScadenzeByIdFattura(idFattura);
	}

	public boolean existContabilizzazioni(IdFattura idFattura) throws Exception {
		return this.pccContabilizzazioneBD.exists(idFattura);
	}

	public Date getDataScadenza(IdFattura idFattura, double importoTotaleDocumento) throws Exception {
		List<PccScadenza> scadenzeLst = this.scadenzaBD.getScadenzeByIdFattura(idFattura, null, null, null );
		
		PccScadenza primaScadenza = null;
		
		for(int i = 0; i < scadenzeLst.size(); i++) {
			PccScadenza scadenza = scadenzeLst.get(i);

			if(scadenza.getDataScadenza() == null) {
				return new Date(0);
			} else if(primaScadenza == null || (scadenza.getImportoInScadenza() > 0 && primaScadenza.getDataScadenza().after(scadenza.getDataScadenza()))) {
				primaScadenza = scadenza;
			}
		}
		
		if(primaScadenza != null)
			return primaScadenza.getDataScadenza();
		
		return null;
		
		
	}

	public boolean isDaPagare(IdFattura idFattura) throws Exception {
		List<PccScadenza> lst = this.scadenzaBD.getScadenzeByIdFattura(idFattura);
		for(PccScadenza scadenza: lst) {
			if(scadenza.getImportoIniziale() == null || scadenza.getImportoIniziale() > 0)
				return true;
		}
		return false;
	}

	public void riallineaStatoContabileFattura(IdFattura idFattura, QueryDatiFatturaRispostaTipo datiFattura) throws Exception {
		
		this.log.debug("Riallineamento stato contabile per id fattura ["+idFattura.toJson()+"]");

		if(datiFattura.getDatiRisposta().getDettaglioFattura() != null) {
			List<PccContabilizzazione> lstLiq = new ArrayList<PccContabilizzazione>();
			List<PccContabilizzazione> lstSosp = new ArrayList<PccContabilizzazione>();
			List<PccContabilizzazione> lstNoliq = new ArrayList<PccContabilizzazione>();
			List<PccPagamento> lstPag = new ArrayList<PccPagamento>();
			

			if(datiFattura.getDatiRisposta().getDettaglioFattura().getDatiDocumento().getListaDettaglioMovimento() != null && datiFattura.getDatiRisposta().getDettaglioFattura().getDatiDocumento().getListaDettaglioMovimento().getMovimento() != null) {
				for(int i = datiFattura.getDatiRisposta().getDettaglioFattura().getDatiDocumento().getListaDettaglioMovimento().getMovimento().size()-1; i >= 0;i--) {
					
					DettaglioMovimentoTipo movimento = datiFattura.getDatiRisposta().getDettaglioFattura().getDatiDocumento().getListaDettaglioMovimento().getMovimento().get(i);
					String statoDebito = movimento.getStatoDebito().trim();
					String causale = movimento.getCausale().trim();
					
					this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"]");
					if(this.liq.containsKey(causale) && "Contabilizzazione".equals(statoDebito)) {
						this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"], aggiungo contabilizzazioni LIQ");
						lstLiq.addAll(getContabilizzazione(movimento, this.liq.get(causale)));
					} else if(this.sosp.containsKey(causale) && "Contabilizzazione".equals(statoDebito)) {
						this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"], aggiungo contabilizzazioni SOSP");
						lstSosp.addAll(getContabilizzazione(movimento, this.sosp.get(causale)));
					} else if(this.noliq.containsKey(causale) && "Contabilizzazione".equals(statoDebito)) {
						this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"], aggiungo contabilizzazioni NOLIQ");
						lstNoliq.addAll(getContabilizzazione(movimento, this.noliq.get(causale)));
					} else if(this.pagamento.containsKey(causale) && "Pagamento".equals(statoDebito)) {
						this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"], aggiungo Pagamento");
						lstPag.addAll(getPagamento(movimento, this.pagamento.get(causale), false));
					} else if(this.pagamento.containsKey(causale) && "StornoPagamento".equals(statoDebito)) {
						this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"], aggiungo PAG negativo");
						lstPag.addAll(getPagamento(movimento, this.pagamento.get(causale), true));
					} else if(this.liq.containsKey(causale) && "StornoContabilizzazione".equals(statoDebito)) {
						this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"], azzero LIQ");
						lstLiq.clear();
					} else if(this.sosp.containsKey(causale) && "StornoContabilizzazione".equals(statoDebito)) {
						this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"], azzero SOSP");
						lstSosp.clear();
					} else if(this.noliq.containsKey(causale) && "StornoContabilizzazione".equals(statoDebito)) {
						this.log.debug("Trovato statoDebito ["+statoDebito+"] e causale ["+causale+"], azzero NOLIQ");
						lstNoliq.clear();
					} else {
						this.log.debug("StatoDebito  ["+statoDebito+"] e causale ["+causale+"] corrisponde a NOP");
					}
					
				}
			}
			
			this.pccContabilizzazioneBD.deleteContabilizzazioni(idFattura);
			
			if(lstLiq.size() > 0) {
				this.log.debug("Inserisco ["+lstLiq.size()+"] LIQ");
				for(PccContabilizzazione cont: lstLiq) {
					cont.setIdFattura(idFattura);
					cont.setStatoDebito(StatoDebitoType.LIQ);
					if(cont.getSistemaRichiedente() == null) {
						TransformUtils.populateContabilizzazioneDefault(cont, false);
					}
					this.pccContabilizzazioneBD.create(cont);
				}
			} else {
				this.log.debug("Non inserisco LIQ");
			}

			if(lstSosp.size() > 0) {
				this.log.debug("Inserisco ["+lstSosp.size()+"] SOSP");
				for(PccContabilizzazione cont: lstSosp) {
					cont.setIdFattura(idFattura);
					cont.setStatoDebito(StatoDebitoType.SOSP);
					if(cont.getSistemaRichiedente() == null) {
						TransformUtils.populateContabilizzazioneDefault(cont, false);
					}
					this.pccContabilizzazioneBD.create(cont);
				}
			} else {
				this.log.debug("Non inserisco SOSP");
			}

			if(lstNoliq.size() > 0) {
				this.log.debug("Inserisco ["+lstNoliq.size()+"] NOLIQ");
				for(PccContabilizzazione cont: lstNoliq) {
					cont.setIdFattura(idFattura);
					cont.setStatoDebito(StatoDebitoType.NOLIQ);
					if(cont.getSistemaRichiedente() == null) {
						TransformUtils.populateContabilizzazioneDefault(cont, false);
					}
					this.pccContabilizzazioneBD.create(cont);
				}
			} else {
				this.log.debug("Non inserisco NOLIQ");
			}

			this.pccPagamentoBD.deletePagamenti(idFattura);

			if(lstPag.size() > 0) {
				this.log.debug("Inserisco ["+lstPag.size()+"] PAG");
				for(PccPagamento pag: lstPag) {
					pag.setIdFattura(idFattura);
					pag.setIdFiscaleIvaBeneficiario(datiFattura.getDatiRisposta().getDettaglioFattura().getDatiDocumento().getIdFiscaleIVA());
					this.pccOperazioneContabileBD.create(pag);
				}
			} else {
				this.log.debug("Non inserisco PAG");
			}
			
			this.scadenzaBD.deleteScadenze(idFattura, true);

			if(datiFattura.getDatiRisposta().getDettaglioFattura().getListaComunicazioneScadenza() != null && datiFattura.getDatiRisposta().getDettaglioFattura().getListaComunicazioneScadenza().getComunicazioneScadenza() != null) {
				List<PianoComunicazioneScadenzaTipo> comunicazioneScadenza = datiFattura.getDatiRisposta().getDettaglioFattura().getListaComunicazioneScadenza().getComunicazioneScadenza();
				if(comunicazioneScadenza.size() > 0) {
					this.log.debug("Inserisco ["+comunicazioneScadenza.size()+"] SCAD");
					for(PianoComunicazioneScadenzaTipo scadenza: comunicazioneScadenza) {
						PccScadenza pccScadenza = new PccScadenza();
						pccScadenza.setDataScadenza(scadenza.getDataScadenza());
						pccScadenza.setImportoInScadenza(scadenza.getImportoInScadenza().doubleValue());
						pccScadenza.setImportoIniziale(scadenza.getImportoIniziale().doubleValue());
						pccScadenza.setPagatoRicontabilizzato(scadenza.getPagatoRicontabilizzato().doubleValue());
						pccScadenza.setIdFattura(idFattura);
						pccScadenza.setDataRichiesta(new Date());
						
						pccScadenza.setSistemaRichiedente(PccProperties.getInstance().getSistemaRichiedenteGestionale());
						pccScadenza.setUtenteRichiedente(PccProperties.getInstance().getUtenteRichiedenteGestionale());
						this.scadenzaBD.create(pccScadenza);
					}
				} else {
					this.log.debug("Non inserisco SCAD");
				}
			}
			this.log.debug("Riallineamento stato contabile per id fattura ["+idFattura.toJson()+"] completato con successo");
		} else {
			this.log.debug("Riallineamento stato contabile per id fattura ["+idFattura.toJson()+"] non possibile");
		}
			
	}
	
	private PccContabilizzazione getContabilizzazione(DettaglioMovimentoTipo movimento, NaturaSpesaType naturaSpesa, CausaleType causale, double importo) throws Exception {
		PccContabilizzazione pccContabilizzazione = new PccContabilizzazione();
		pccContabilizzazione.setDataRichiesta(movimento.getDataMovimento());
		pccContabilizzazione.setImportoMovimento(Math.abs(importo));
		pccContabilizzazione.setNaturaSpesa(naturaSpesa);
		pccContabilizzazione.setCapitoliSpesa(movimento.getCapitoloPgConto());
		
		if(causale != null)
			pccContabilizzazione.setCausale(causale);
		
		try {
			TransformUtils.populateContabilizzazione(pccContabilizzazione, movimento.getDescrizioneContabilizzazione());
		} catch(Exception e) {
			this.log.info("Descrizione ["+movimento.getDescrizioneContabilizzazione()+"] non nel giusto formato, inserisco valori di default");
		}
		pccContabilizzazione.setEstremiImpegno(movimento.getEstremiImpegno());
		
		if(movimento.getCodiceCIG() != null) {
			pccContabilizzazione.setCodiceCig(movimento.getCodiceCIG());
		} else {
			pccContabilizzazione.setCodiceCig("NA");
		}
		
		if(movimento.getCodiceCUP() != null) {
			pccContabilizzazione.setCodiceCup(movimento.getCodiceCUP());
		} else {
			pccContabilizzazione.setCodiceCup("NA");
		}

		return pccContabilizzazione;
	}
	
	private List<PccContabilizzazione> getContabilizzazione(DettaglioMovimentoTipo movimento, RiallineamentoBean riallineamento) throws Exception {
		List<PccContabilizzazione> lstContabilizzazione = new ArrayList<PccContabilizzazione>();
		
		switch(riallineamento.getNaturaSpesa()) {
		case CA: lstContabilizzazione.add(getContabilizzazione(movimento, NaturaSpesaType.CA, riallineamento.getCausale(), movimento.getImporto().doubleValue()));
			break;
		case CO: lstContabilizzazione.add(getContabilizzazione(movimento, NaturaSpesaType.CO, riallineamento.getCausale(), movimento.getImporto().doubleValue()));
			break;
		case NA: lstContabilizzazione.add(getContabilizzazione(movimento, NaturaSpesaType.NA, riallineamento.getCausale(), movimento.getImporto().doubleValue()));
			break;
		default:
			break;
		}
		
		return lstContabilizzazione;
	}

	private PccPagamento getPagamento(DettaglioMovimentoTipo movimento, NaturaSpesaType naturaSpesa, double importo, boolean storno) throws Exception {
		PccPagamento pccPagamento = new PccPagamento();
		pccPagamento.setDataRichiesta(movimento.getDataMovimento());
		pccPagamento.setDescrizione(movimento.getDescrizioneContabilizzazione());
		pccPagamento.setNaturaSpesa(naturaSpesa);
		pccPagamento.setCapitoliSpesa(movimento.getCapitoloPgConto());
		
		pccPagamento.setEstremiImpegno(movimento.getEstremiImpegno());
		
		if(movimento.getCodiceCIG() != null) {
			pccPagamento.setCodiceCig(movimento.getCodiceCIG());
		} else {
			pccPagamento.setCodiceCig("NA");
		}
		
		if(movimento.getCodiceCUP() != null) {
			pccPagamento.setCodiceCup(movimento.getCodiceCUP());
		} else {
			pccPagamento.setCodiceCup("NA");
		}

		if(storno) {
			pccPagamento.setImportoPagato(-Math.abs(importo));
		} else {
			pccPagamento.setImportoPagato(Math.abs(importo));
		}

		return pccPagamento;
	}

	private List<PccPagamento> getPagamento(DettaglioMovimentoTipo movimento, RiallineamentoBean riallineamento, boolean storno) throws Exception {
		List<PccPagamento> lstPagamento = new ArrayList<PccPagamento>();
		
		switch(riallineamento.getNaturaSpesa()) {
		case CA: lstPagamento.add(getPagamento(movimento, NaturaSpesaType.CA, movimento.getImporto().doubleValue(), storno));
			break;
		case CO: lstPagamento.add(getPagamento(movimento, NaturaSpesaType.CO, movimento.getImporto().doubleValue(), storno));
			break;
		case NA: lstPagamento.add(getPagamento(movimento, NaturaSpesaType.NA, movimento.getImporto().doubleValue(), storno));
			break;
		default:
			break;
		}

		return lstPagamento;
	}
	
	public void aggiornaContabilizzazioniEScadenzeDopoPagamento(IdFattura idFattura, PagamentoTipo pagamento) throws Exception {
		double rimanenza = pagamento.getImportoPagato().doubleValue();
		
		List<List<PccContabilizzazione>> listaDiListeContabilizzazioni = getListaDiListeContabilizzazioni(idFattura, pagamento);
		
		List<PccContabilizzazione> contabilizzazioniDaCancellare = new ArrayList<PccContabilizzazione>();
		
		for(int i = 0; i< listaDiListeContabilizzazioni.size(); i++) { //l'ordine e' importante
			List<PccContabilizzazione> listaCont = listaDiListeContabilizzazioni.get(i);
			if(rimanenza > 0) {
				for(PccContabilizzazione contabilizzazione: listaCont) {
					if(rimanenza > 0) {
						if(contabilizzazione.getImportoMovimento() > rimanenza) {
							
							contabilizzazione.setImportoMovimento(contabilizzazione.getImportoMovimento() - rimanenza);
							this.pccContabilizzazioneBD.update(contabilizzazione);
							rimanenza = 0;
							
						} else {
							rimanenza -= contabilizzazione.getImportoMovimento();
							contabilizzazioniDaCancellare.add(contabilizzazione);
						}
					} else {
						break;
					}				
				}
			} else {
				break;
			}
		}
		
		for(PccContabilizzazione delete: contabilizzazioniDaCancellare) {
			this.pccContabilizzazioneBD.delete(delete);
		}
		
		//scadenze
		
		List<FilterSortWrapper> list = new ArrayList<FilterSortWrapper>();
		FilterSortWrapper wr = new FilterSortWrapper();
		wr.setField(PccScadenza.model().DATA_SCADENZA);
		wr.setSortOrder(SortOrder.ASC);
		list.add(wr);
		List<PccScadenza> scadenzeLst = this.scadenzaBD.getScadenzeByIdFattura(idFattura, null, null, list );

		PccScadenza ultimaScadenza = null;
		double importo = pagamento.getImportoPagato().doubleValue();
		for(int i =0; i < scadenzeLst.size(); i++) {
			PccScadenza scad = scadenzeLst.get(i);
			if(scad.getImportoInScadenza() != null && scad.getImportoInScadenza().doubleValue() > 0) {
				ultimaScadenza = scad;
				if(importo > 0) {
					Double importoScad = scad.getImportoInScadenza();
					Double pagatoRicontabilizzato = scad.getPagatoRicontabilizzato();
					if(importo > importoScad) {
						scad.setImportoInScadenza(0d);
						scad.setPagatoRicontabilizzato(pagatoRicontabilizzato  + importoScad);
						this.scadenzaBD.update(scad);
					} else {
						scad.setImportoInScadenza(importoScad - importo);
						scad.setPagatoRicontabilizzato(pagatoRicontabilizzato + importo);
						this.scadenzaBD.update(scad);
					}
					importo -= importoScad;
				} else {
					break;
				}
			}
		}
		
		if(importo > 0 && ultimaScadenza != null) {
			ultimaScadenza.setPagatoRicontabilizzato(ultimaScadenza.getPagatoRicontabilizzato().doubleValue() + importo);
		}
	}

	public void aggiornaContabilizzazioniDopoStornoPagamento(IdFattura idFattura, PagamentoStornoTipo storno, String sistemaRichiedente, String utenteRichiedente) throws Exception {
		
		PccContabilizzazione contabilizzazione = new PccContabilizzazione();
		NaturaSpesaType naturaSpesa = null;
		switch(storno.getNaturaSpesa()) {
		case CA:naturaSpesa = NaturaSpesaType.CA;
			break;
		case CO:naturaSpesa = NaturaSpesaType.CO;
			break;
		default:
			break;
		
		}
		
		contabilizzazione.setDataRichiesta(new Date());
		contabilizzazione.setNaturaSpesa(naturaSpesa);
		contabilizzazione.setCodiceCig("NA");
		contabilizzazione.setCodiceCup("NA");
		contabilizzazione.setStatoDebito(StatoDebitoType.LIQ);
		contabilizzazione.setImportoMovimento(storno.getImportoStorno().doubleValue());
		contabilizzazione.setIdFattura(idFattura);
		contabilizzazione.setSistemaRichiedente(sistemaRichiedente);
		contabilizzazione.setUtenteRichiedente(utenteRichiedente);
		contabilizzazione.setIdImporto(UUID.randomUUID().toString().substring(0, 3));
		
		this.pccContabilizzazioneBD.create(contabilizzazione);

		//scadenze
		
		List<FilterSortWrapper> list = new ArrayList<FilterSortWrapper>();
		FilterSortWrapper wr = new FilterSortWrapper();
		wr.setField(PccScadenza.model().DATA_SCADENZA);
		wr.setSortOrder(SortOrder.DESC);
		list.add(wr);
		List<PccScadenza> scadenzeLst = this.scadenzaBD.getScadenzeByIdFattura(idFattura, null, null, list );

		double importo = storno.getImportoStorno().doubleValue();
		for(int i =0; i < scadenzeLst.size(); i++) {
			PccScadenza scad = scadenzeLst.get(i);
			if(scad.getPagatoRicontabilizzato() != null && scad.getPagatoRicontabilizzato().doubleValue() > 0) {
				if(importo > 0) {
					Double importoScad = scad.getImportoInScadenza();
					Double pagatoRicontabilizzato = scad.getPagatoRicontabilizzato();
					if(importo > pagatoRicontabilizzato) {
						scad.setImportoInScadenza(importoScad + pagatoRicontabilizzato);
						scad.setPagatoRicontabilizzato(0d);
						this.scadenzaBD.update(scad);
					} else {
						scad.setImportoInScadenza(importoScad + importo);
						scad.setPagatoRicontabilizzato(pagatoRicontabilizzato - importo);
						this.scadenzaBD.update(scad);
					}
					importo -= pagatoRicontabilizzato;
				} else {
					break;
				}
			}
		}

	}
	
	private List<List<PccContabilizzazione>> getListaDiListeContabilizzazioni(IdFattura idFattura, PagamentoTipo pagamento) throws ServiceException {
		
		String cig = pagamento.getCodiceCIG();
		String cup = pagamento.getCodiceCUP();
		NaturaSpesaTipo naturaSpesa = pagamento.getNaturaSpesa();

		ArrayList<List<PccContabilizzazione>> listaDiListe = new ArrayList<List<PccContabilizzazione>>();
		List<PccContabilizzazione> contabilizzazioniByIdFattura = this.pccContabilizzazioneBD.getContabilizzazioniByIdFattura(idFattura);
		
		List<PccContabilizzazione> contabilizzazioniLiqConStessiDati = new ArrayList<PccContabilizzazione>();
		List<PccContabilizzazione> contabilizzazioniLiqConDatiDiversi = new ArrayList<PccContabilizzazione>();
		List<PccContabilizzazione> contabilizzazioniSosp = new ArrayList<PccContabilizzazione>();
		List<PccContabilizzazione> contabilizzazioniNoliq = new ArrayList<PccContabilizzazione>();
		
		for(PccContabilizzazione contabilizzazione: contabilizzazioniByIdFattura) {
			if(contabilizzazione.getStatoDebito().equals(StatoDebitoType.LIQ)) {
				if(contabilizzazione.getCodiceCig() != null && contabilizzazione.getCodiceCup() != null && contabilizzazione.getNaturaSpesa() != null
						&&
						contabilizzazione.getCodiceCig().equals(cig) && contabilizzazione.getCodiceCup().equals(cup) && contabilizzazione.getNaturaSpesa().getValue().equals(naturaSpesa.value())) {
					contabilizzazioniLiqConStessiDati.add(contabilizzazione);
				} else if(contabilizzazione.getNaturaSpesa() != null && contabilizzazione.getNaturaSpesa().getValue().equals(naturaSpesa.value())) {
					contabilizzazioniLiqConDatiDiversi.add(contabilizzazione);
				}
			} else if(contabilizzazione.getStatoDebito().equals(StatoDebitoType.SOSP)) {
				contabilizzazioniSosp.add(contabilizzazione);
			} else if(contabilizzazione.getStatoDebito().equals(StatoDebitoType.NOLIQ)) {
				contabilizzazioniNoliq.add(contabilizzazione);
			}
		}
		listaDiListe.add(contabilizzazioniLiqConStessiDati);
		listaDiListe.add(contabilizzazioniLiqConDatiDiversi);
		listaDiListe.add(contabilizzazioniSosp);
		listaDiListe.add(contabilizzazioniNoliq);
		return listaDiListe;
	}

	public void aggiornaScadenzeDopoContabilizzazione(IdFattura idFattura) throws Exception {
		List<PccScadenza> scadenzeLst = this.scadenzaBD.getScadenzeByIdFattura(idFattura);

		double importoInScadenza = 0;

		for(PccScadenza scadenza: scadenzeLst) {
			if(scadenza.getImportoInScadenza() == null) return; // quando c'e' una scadenza senza importo non possiamo fare assunzioni, quindi non aggiorniamo le scadenze)
			importoInScadenza += scadenza.getImportoInScadenza();
		}
		
		if(importoInScadenza > 0) {
			List<PccContabilizzazione> lstCont = this.pccContabilizzazioneBD.getContabilizzazioniByIdFattura(idFattura);
			double importoSospLiq = 0;

			for(PccContabilizzazione contabil: lstCont) {
				if(contabil.getStatoDebito().equals(StatoDebitoType.LIQ) || contabil.getStatoDebito().equals(StatoDebitoType.SOSP)) {
					importoSospLiq += contabil.getImportoMovimento();
				}
			}

			double differenzaNoLiq = importoInScadenza - importoSospLiq;

			if(differenzaNoLiq > 0) {
				for(int i =0; i < scadenzeLst.size(); i++) {
					PccScadenza scad = scadenzeLst.get(i);
					if(scad.getImportoInScadenza() != null && scad.getImportoInScadenza().doubleValue() > 0) {
						if(differenzaNoLiq > 0) {
							Double importoScad = scad.getImportoInScadenza();
							Double pagatoRicontabilizzato = scad.getPagatoRicontabilizzato();
							if(differenzaNoLiq > importoScad) {
								scad.setImportoInScadenza(0d);
								scad.setPagatoRicontabilizzato(pagatoRicontabilizzato  + importoScad);
								this.scadenzaBD.update(scad);
							} else {
								scad.setImportoInScadenza(importoScad - differenzaNoLiq);
								scad.setPagatoRicontabilizzato(pagatoRicontabilizzato + differenzaNoLiq);
								this.scadenzaBD.update(scad);
							}
							differenzaNoLiq -= importoScad;
						} else {
							break;
						}
					}
				}
			}
		}
	}

	public void checkImportoScadenze(IdFattura idFattura, double importoTotalePianoScadenze)throws Exception {
		List<PccContabilizzazione> lstCont = this.pccContabilizzazioneBD.getContabilizzazioniByIdFattura(idFattura);
		double importoSospLiq = 0;

		for(PccContabilizzazione contabil: lstCont) {
			if(contabil.getStatoDebito().equals(StatoDebitoType.LIQ) || contabil.getStatoDebito().equals(StatoDebitoType.SOSP)) {
				importoSospLiq += contabil.getImportoMovimento();
			}
		}
		
		if(importoTotalePianoScadenze > importoSospLiq) {
			throw new OperazioneNonPermessaException("Impossibile impostare un piano scadenze di ["+importoTotalePianoScadenze+"], in quanto il totale degli importi contabilizzati in stato SOSP e LIQ e' inferiore ("+importoSospLiq+").");
		}
	}

	public List<ContabilizzazioneTipo> getContabilizzazioniByIdFattura(IdFattura idFattura) throws Exception {
			List<PccContabilizzazione> pccContabilizzazioneList = this.pccContabilizzazioneBD.getContabilizzazioniByIdFattura(idFattura);
			List<ContabilizzazioneTipo> lst = new ArrayList<ContabilizzazioneTipo>();
			for(PccContabilizzazione contabilizzazione: pccContabilizzazioneList) {
				lst.add(toPcc(contabilizzazione));
			}
			return lst;
		}

	public boolean existPagamentiByIdFattura(IdFattura idFattura) throws Exception {
		return this.pccPagamentoBD.existPagamentiByIdFattura(idFattura);
	}

	public List<ContabilizzazioneTipo> getContabilizzazioniByIdFatturaDiversoSistemaRichiedenteEIdImportoDiversi(
			IdFattura idFattura, String sistemaRichiedente,
			List<ContabilizzazioneTipo> contabilizzazioniOriginali) throws ServiceException {
		
		List<String> lstId = new ArrayList<String>();
		
		for(ContabilizzazioneTipo cont: contabilizzazioniOriginali) {
			lstId.add(cont.getIdentificativoMovimento());
		}
		
		List<PccContabilizzazione> pccContabilizzazioneList = this.pccContabilizzazioneBD.getContabilizzazioniByIdFatturaDiversoSistemaRichiedenteEIdImportoDiversi(idFattura, sistemaRichiedente, lstId);
		List<ContabilizzazioneTipo> lst = new ArrayList<ContabilizzazioneTipo>();
		for(PccContabilizzazione contabilizzazione: pccContabilizzazioneList) {
			lst.add(toPcc(contabilizzazione));
		}
		return lst;

	}


}
