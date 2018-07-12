package org.govmix.proxy.fatturapa.web.console.mbean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.govmix.proxy.fatturapa.orm.Dipartimento;
import org.govmix.proxy.fatturapa.orm.FatturaElettronica;
import org.govmix.proxy.fatturapa.orm.IdEnte;
import org.govmix.proxy.fatturapa.orm.constants.EsitoType;
import org.govmix.proxy.fatturapa.orm.constants.StatoElaborazioneType;
import org.govmix.proxy.fatturapa.web.commons.utils.LoggerManager;
import org.govmix.proxy.fatturapa.web.console.bean.ConservazioneBean;
import org.govmix.proxy.fatturapa.web.console.datamodel.ConservazioneDM;
import org.govmix.proxy.fatturapa.web.console.form.FatturaForm;
import org.govmix.proxy.fatturapa.web.console.iservice.IConservazioneService;
import org.govmix.proxy.fatturapa.web.console.search.ConservazioneSearchForm;
import org.openspcoop2.generic_project.web.impl.jsf1.input.impl.SelectListImpl;
import org.openspcoop2.generic_project.web.impl.jsf1.mbean.DataModelListView;
import org.openspcoop2.generic_project.web.impl.jsf1.mbean.exception.FiltraException;
import org.openspcoop2.generic_project.web.impl.jsf1.mbean.exception.MenuActionException;
import org.openspcoop2.generic_project.web.impl.jsf1.utils.MessageUtils;

public class ConservazioneMBean extends DataModelListView<ConservazioneBean, Long,
ConservazioneSearchForm, FatturaForm, ConservazioneDM, FatturaElettronica, 
IConservazioneService>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Anno
	private List<SelectItem> listaAnni = null;	
	
	// Tipo Fattura
	private List<SelectItem> listaTipiFattura = null;
	
	// Ente
	private List<SelectItem> listaEnti = null;
	
	// Stato Invio
	private List<SelectItem> listaStatiInvio = null;
	
	public ConservazioneMBean(){
		super(LoggerManager.getConsoleLogger());
		this.initTables();
		this.setOutcomes();

		this.log.debug("ConservazioneMBean");

	}
	public void initTables() {
		try{
			this.table = this.factory.getTableFactory().createPagedDataTable();
			this.table.setId("conservazioneTable"); 
			this.table.setEnableDelete(false);
			this.table.setShowAddButton(false);
			this.table.setShowDetailColumn(false);
			this.table.setShowSelectAll(false);
			this.table.setHeaderText("conservazione.label.ricercaConservazione.tabellaRisultati");
			this.table.setMBean(this);
			this.table.setMetadata(this.getMetadata()); 

		}catch (Exception e) {
			log.error("Errore durante la init ConservazioneMBean:" + e.getMessage(),e);  
		}
	}

	private void setOutcomes(){
		this.getNavigationManager().setAnnullaOutcome(null);
		this.getNavigationManager().setDeleteOutcome(null);
		this.getNavigationManager().setDettaglioOutcome(null);
		this.getNavigationManager().setFiltraOutcome("listaConservazione?faces-redirect=true");
		this.getNavigationManager().setInviaOutcome(null);
		this.getNavigationManager().setMenuActionOutcome("listaConservazione");
		this.getNavigationManager().setModificaOutcome(null);
		this.getNavigationManager().setNuovoOutcome(null);
		this.getNavigationManager().setResetOutcome("listaConservazione?faces-redirect=true");
		this.getNavigationManager().setRestoreSearchOutcome("listaConservazione");
	}


	@Override
	protected String _filtra() throws FiltraException {
		return super._filtra();
	}

	// Override del set della ricerca, popolo i field di tipo selectList.
	@Override
	public void setSearch(ConservazioneSearchForm search) {
		this.search = search;
		this.selectedElement = null;

		// Popolo le selectList Statiche
		((SelectListImpl)this.search.getAnno()).setElencoSelectItems(this.getListaAnni());
		((SelectListImpl)this.search.getTipoFattura()).setElencoSelectItems(this.getListaTipiFattura());
		((SelectListImpl)this.search.getEnte()).setElencoSelectItems(this.getListaEnti());
		((SelectListImpl)this.search.getStatoInvio()).setElencoSelectItems(this.getListaStatiInvio());
		this.search.setmBean(this);
	}
	
	
	public List<SelectItem> getListaAnni() {
		if (this.listaAnni == null) {
			this.listaAnni = new ArrayList<SelectItem>();
			
			this.listaAnni.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem("2018", "2018")));
			this.listaAnni.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem("2017", "2017")));
			this.listaAnni.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem("2016", "2016")));
		}
		
		return listaAnni;
	}
	public void setListaAnni(List<SelectItem> listaAnni) {
		this.listaAnni = listaAnni;
	}
	public List<SelectItem> getListaTipiFattura() {
		if (this.listaTipiFattura == null) {
			this.listaTipiFattura = new ArrayList<SelectItem>();
			
			this.listaTipiFattura.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem("attiva", ("conservazione.search.tipoFattura.attiva"))));
			this.listaTipiFattura.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem("passiva", ("conservazione.search.tipoFattura.passiva"))));
		
		}
		return listaTipiFattura;
	}
	public void setListaTipiFattura(List<SelectItem> listaTipiFattura) {
		this.listaTipiFattura = listaTipiFattura;
	}
	public List<SelectItem> getListaEnti() {
		this.listaEnti = new ArrayList<SelectItem>();
		
		boolean fatturazioneAttiva = false;
		org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem tipoFatturaSelezionata = ((SelectListImpl)this.search.getTipoFattura()).getValue();
		if(tipoFatturaSelezionata != null) {
			fatturazioneAttiva = "attiva".equals(tipoFatturaSelezionata.getValue());
		}
		
		this.listaEnti = _getEnti(true, fatturazioneAttiva);
		
		return listaEnti;
	}
	public void setListaEnti(List<SelectItem> listaEnti) {
		this.listaEnti = listaEnti;
	}
	
	public List<SelectItem> _getEnti(boolean addQualsiasi, boolean fatturazioneAttiva) {
		List<SelectItem> listaDipartimenti = new ArrayList<SelectItem>();

		if(addQualsiasi)
			listaDipartimenti.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem("*",  ("commons.label.qualsiasi"))));

		List<Dipartimento> listaDipartimentiLoggedUtente = org.govmix.proxy.fatturapa.web.console.util.Utils.getListaDipartimentiLoggedUtente();
		List<String> listaAggiunti = new ArrayList<String>();
		if(listaDipartimentiLoggedUtente != null && listaDipartimentiLoggedUtente.size() > 0)
			for (Dipartimento dipartimento : listaDipartimentiLoggedUtente) {
				boolean add = true;

				if(fatturazioneAttiva) {
					add = dipartimento.isFatturazioneAttiva();
				}

				if(add) {
					IdEnte ente = dipartimento.getEnte();
					if(!listaAggiunti.contains(ente.getNome())){
						listaAggiunti.add(ente.getNome());
						listaDipartimenti.add(new SelectItem(
							new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(ente.getNome(),ente.getNome())));
					}
				}
			}


		return listaDipartimenti;
	}
	
	/*
	 * Non inviata in conservazione
	   Presa in carico
       In riconsegna
	   Errore consegna
	   Conservazione competata
	   Conservazione fallita 
	 */
	public List<SelectItem> getListaStatiInvio() {
		if (this.listaStatiInvio == null) {
			this.listaStatiInvio = new ArrayList<SelectItem>();
			
			
			boolean fatturazioneAttiva = false;
			org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem tipoFatturaSelezionata = ((SelectListImpl)this.search.getTipoFattura()).getValue();
			if(tipoFatturaSelezionata != null) {
				fatturazioneAttiva = "attiva".equals(tipoFatturaSelezionata.getValue());
			}

			this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem("*", ("commons.label.qualsiasi"))));
			if(fatturazioneAttiva) {
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(StatoElaborazioneType.ERRORE_DI_FIRMA.getValue(), "Non inviata in conservazione")));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(StatoElaborazioneType.PRESA_IN_CARICO.getValue(),  "Presa in carico")));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(StatoElaborazioneType.ERRORE_DI_PROTOCOLLO.getValue(),  ("In riconsegna"))));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(StatoElaborazioneType.ERRORE_DI_SPEDIZIONE.getValue(),  ("Errore consegna"))));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(StatoElaborazioneType.PROTOCOLLATA.getValue(),  ("Conservazione competata"))));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(StatoElaborazioneType.MANCATA_CONSEGNA.getValue(),  ("Conservazione fallita"))));
				
			}
			else {
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem("E",   "Non inviata in conservazione")));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(EsitoType.IN_ELABORAZIONE_ACCETTATO.getValue(),   ("Presa in carico"))));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(EsitoType.IN_ELABORAZIONE_RIFIUTATO.getValue(),  ("In riconsegna"))));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(EsitoType.INVIATA_RIFIUTATO.getValue() ,  ("Errore consegna"))));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(EsitoType.INVIATA_ACCETTATO.getValue(),   ("Conservazione competata"))));
				this.listaStatiInvio.add(new SelectItem(new org.openspcoop2.generic_project.web.impl.jsf1.input.SelectItem(EsitoType.SCARTATA_ACCETTATO.getValue(),   ("Conservazione fallita"))));
			}
		}
		
		return listaStatiInvio;
	}
	public void setListaStatiInvio(List<SelectItem> listaStatiInvio) {
		this.listaStatiInvio = listaStatiInvio;
	}

	
	@Override
	protected String _menuAction() throws MenuActionException {
		this.search.setRestoreSearch(true);
		return super._menuAction();
	}
	
	public String inviaFattureInConservazione() {
		try {

			// recupero lista diagnostici
			List<Long> idFatture = new ArrayList<Long>();

			// se nn sono in select all allore prendo solo quelle selezionate
			if (!this.isSelectedAll()) {
				Iterator<ConservazioneBean> it = this.selectedIds.keySet().iterator();
				while (it.hasNext()) {
					ConservazioneBean fatturaBean = it.next();
					if (this.selectedIds.get(fatturaBean).booleanValue()) {
						idFatture.add(fatturaBean.getDTO().getId());
						it.remove();
					}
				}
			}

			MessageUtils.addInfoMsg(org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils.getInstance().getMessageFromResourceBundle("conservazione.invio.ok"));

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			this.log.error(e, e);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils.getInstance().getMessageFromResourceBundle("conservazione.invio.genericError"),null));
		}

		return null;
	}
}
