package org.govmix.proxy.fatturapa.web.console.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.govmix.proxy.fatturapa.web.commons.utils.LoggerManager;
import org.govmix.proxy.fatturapa.web.console.form.FatturaForm;
import org.govmix.proxy.fatturapa.web.console.util.ConsoleProperties;
import org.richfaces.model.UploadItem;

public class FileUploadBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private static Logger log = LoggerManager.getConsoleLogger();
	
	private Map<String, UploadItem> mapElementiRicevuti = null;
	private Map<String, String> mapChiaviElementi = null;
	
	private List<String> nomeFile;
	private List<byte[]> contenuto;
	private List<Integer> dimensioneFile;
	private String fileUploadErrorMessage;
	
//	private List<UploadItem> files = new ArrayList<UploadItem>();
//	private List<UploadItem> filesCache = new ArrayList<UploadItem>();
	private String acceptedTypes = "";
	private int numeroFile = 1;
	
	private FatturaElettronicaAttivaMBean mBean =null; 
	private FatturaForm form = null;
	
	public FileUploadBean() throws Exception {
		this.setNumeroFile(ConsoleProperties.getInstance(LoggerManager.getConsoleLogger()).getFatturaAttivaCaricamentoMaxNumeroFile());
		this.setAcceptedTypes(ConsoleProperties.getInstance(LoggerManager.getConsoleLogger()).getFatturaAttivaCaricamentoTipologieFileAccettati()); 
		this.nomeFile = new ArrayList<String>();
		this.contenuto = new ArrayList<byte[]>();
		this.dimensioneFile = new ArrayList<Integer>();
		
		this.mapChiaviElementi = new HashMap<String, String>();
		this.mapElementiRicevuti = new HashMap<String, UploadItem>();
	}

	public void setDto(boolean isAdd, byte[] contenuto, String fileName){
	}
	
	public String getDimensioneFile() {
		if(this.dimensioneFile != null){
			return org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils.getInstance().getMessageWithParamsFromResourceBundle("fileUpload.dimensioneFile", this.dimensioneFile);
		}
		
		return org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils.getInstance().getMessageFromResourceBundle("fileUpload.dimensioneFileNonDisponibile");
	}
	public void setDimensioneFile(String dimensioneFile) {
	}

	public String getFileUploadErrorMessage() {
		return this.fileUploadErrorMessage;
	}

	public void setFileUploadErrorMessage(String fileUploadErrorMessage) {
		this.fileUploadErrorMessage = fileUploadErrorMessage;
	}

	public void setNumeroFile(int numeroFile) {
		this.numeroFile = numeroFile;
	}
	
//	public synchronized void fileUploadListener(UploadEvent event) {
//		log.debug("Event upload!: "+ event.getSource());
//		log.debug("Event upload upItem: "+ event.getUploadItem());
//		log.debug("Event upload upItems: "+ event.getUploadItems());
//		
//		this.fileUploadErrorMessage = null;
//		this.mBean.setCheckFormFatturaMessage(null); 
//		UploadItem item = event.getUploadItem();
//		
//		log.debug("Event upload item CT: "+ item.getContentType());
//		log.debug("Event upload item Filename: "+ item.getFileName());
//		log.debug("Event upload item FileSize: "+ item.getFileSize());
//		log.debug("Event upload item File: "+ item.getFile());
//		log.debug("Event upload item isTempfile: "+ item.isTempFile());
//
//		if(item != null  && item.getFile() != null) {
//			log.debug("Nome File ricevuto: "+ item.getFile().getAbsolutePath()); 
//		}
//		if(item!= null && item.getData() != null){
//			this.filesCache.add(item);
////			this.contenuto.add(item.getData());
////			this.nomeFile.add(item.getFileName());
////			this.dimensioneFile.add(item.getFileSize());
////			this.caricato = true; uploadData="#{fatturaElettronicaAttivaMBean.form.fatturaFile.files}"
//		}
//		else {
//			MessageUtils.addErrorMsg(org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils.getInstance().getMessageFromResourceBundle("fileUpload.uploadError"));
//			this.fileUploadErrorMessage = org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils.getInstance().getMessageFromResourceBundle("fileUpload.uploadError");
//		}
//		
//		log.debug("Event upload completato!");
//	}
	
//	public final List<UploadItem> getFiles() {
//		return this.files;
//	}
//	
//	public void setFiles(List<UploadItem> files) {
//		this.files = files;
//	}

//	public final List<UploadItem> getFilesCache() {
//		return this.filesCache;
//	}
	
	public final void clear(final ActionEvent e) {
//		this.files = new ArrayList<UploadItem>();
//		this.filesCache = new ArrayList<UploadItem>();
		this.nomeFile = new ArrayList<String>();
		this.contenuto = new ArrayList<byte[]>();
		this.dimensioneFile = new ArrayList<Integer>();
//		this.caricato = false;
		this.fileUploadErrorMessage = null;
		this.mBean.setCheckFormFatturaMessage(null); 
		
		this.mapChiaviElementi.clear();
		this.mapElementiRicevuti.clear();
		
		this.form.enableButton();
	}
	
	/***
	 * Inizializza tutte le variabili con le info sui file
	 * N.B. spostato qui perche' ci sono incosistenze tra file gestiti dal framework e quelli salvati nell strutture dati.
	 *  
	 * 
	 */
	public void checkCaricamenti() {
		this.nomeFile = new ArrayList<String>();
		this.contenuto = new ArrayList<byte[]>();
		this.dimensioneFile = new ArrayList<Integer>();
		
		// prendo i file ricevuti  dalla mappa 
		//[TODO] aggiungere check identificativi
		
		for (String itemName : this.mapElementiRicevuti.keySet()) {
			UploadItem item = this.mapElementiRicevuti.get(itemName);
			this.contenuto.add(item.getData());
			this.nomeFile.add(item.getFileName());
			this.dimensioneFile.add(item.getFileSize());
		}
		
		
//		for (int i = 0; i < filesCache.size(); i++) {
//			UploadItem item = this.filesCache.get(i);
//			this.contenuto.add(item.getData());
//			this.nomeFile.add(item.getFileName());
//			this.dimensioneFile.add(item.getFileSize());
//		}
	}

	public int getNumeroFile(){
//		return this.numeroFile - this.files.size();
		return this.numeroFile - this.mapElementiRicevuti.size();
	}
	
	public String getAcceptedTypes() {
		return this.acceptedTypes;
	}

	public void setAcceptedTypes(String acceptedTypes) {
		this.acceptedTypes = acceptedTypes;
	}

	public List<String> getNomeFile() {
		return nomeFile;
	}

	public void setNomeFile(List<String> nomeFile) {
		this.nomeFile = nomeFile;
	}

	public List<byte[]> getContenuto() {
		return contenuto;
	}

	public void setContenuto(List<byte[]> contenuto) {
		this.contenuto = contenuto;
	}

	public void setDimensioneFile(List<Integer> dimensioneFile) {
		this.dimensioneFile = dimensioneFile;
	}

	public FatturaElettronicaAttivaMBean getmBean() {
		return mBean;
	}

	public void setmBean(FatturaElettronicaAttivaMBean mBean) {
		this.mBean = mBean;
	}
	
	public FatturaForm getForm() {
		return form;
	}

	public void setForm(FatturaForm form) {
		this.form = form;
	}

	public final void startUploadsListener(final ActionEvent e) {
		this.form.disableButton();
		this.fileUploadErrorMessage = null;
		this.mBean.setCheckFormFatturaMessage(null); 
	}
	
	public final void stopUploadsListener(final ActionEvent e) {
		this.form.enableButton();
		this.fileUploadErrorMessage = null;
		this.mBean.setCheckFormFatturaMessage(null); 
	}
	
	public final void uploadCanceledListener(final ActionEvent e) {
		this.form.enableButton();
		this.fileUploadErrorMessage = null;
		this.mBean.setCheckFormFatturaMessage(null); 
	}
	
	public final void uploadErrorListener(final ActionEvent e) {
		//this.form.enableButton();
	}
	
	public final void deleteCompleteListener(final ActionEvent e) {
		this.form.enableButton();
		this.fileUploadErrorMessage = null;
		this.mBean.setCheckFormFatturaMessage(null); 
	}

	public Map<String, UploadItem> getMapElementiRicevuti() {
		return mapElementiRicevuti;
	}

	public void setMapElementiRicevuti(Map<String, UploadItem> mapElementiRicevuti) {
		this.mapElementiRicevuti = mapElementiRicevuti;
	}

	public Map<String, String> getMapChiaviElementi() {
		return mapChiaviElementi;
	}

	public void setMapChiaviElementi(Map<String, String> mapChiaviElementi) {
		this.mapChiaviElementi = mapChiaviElementi;
	}
	
	
}