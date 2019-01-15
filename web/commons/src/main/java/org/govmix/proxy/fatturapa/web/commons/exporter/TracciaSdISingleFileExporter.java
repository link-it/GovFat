package org.govmix.proxy.fatturapa.web.commons.exporter;

import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.govmix.proxy.fatturapa.orm.FatturaElettronica;
import org.govmix.proxy.fatturapa.orm.TracciaSDI;
import org.govmix.proxy.fatturapa.orm.constants.TipoComunicazioneType;
import org.govmix.proxy.fatturapa.orm.dao.IDBTracciaSDIServiceSearch;
import org.govmix.proxy.fatturapa.orm.dao.ITracciaSDIServiceSearch;
import org.govmix.proxy.fatturapa.web.commons.businessdelegate.filter.FatturaFilter;
import org.govmix.proxy.fatturapa.web.commons.dao.DAOFactory;
import org.govmix.proxy.fatturapa.web.commons.exporter.PDFCreator.TipoXSL;
import org.govmix.proxy.fatturapa.web.commons.exporter.exception.ExportException;
import org.govmix.proxy.fatturapa.web.commons.utils.CommonsProperties;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;

public class TracciaSdISingleFileExporter extends AbstractSingleFileXMLExporter<TracciaSDI, Long> {

	private ITracciaSDIServiceSearch tracciaSdiSearchDAO;

	public TracciaSdISingleFileExporter(Logger log, Connection connection, boolean autocommit)
			throws ServiceException, NotImplementedException, Exception {
		super(log, connection, autocommit);
		this.tracciaSdiSearchDAO = DAOFactory.getInstance().getServiceManager(connection, autocommit).getTracciaSDIServiceSearch();
	}

	public TracciaSdISingleFileExporter(Logger log) throws ServiceException, NotImplementedException, Exception {
		super(log);
		this.tracciaSdiSearchDAO = DAOFactory.getInstance().getServiceManager().getTracciaSDIServiceSearch();
	}

	@Override
	protected String getNomeRisorsaXLST(TracciaSDI object) throws Exception {
		return CommonsProperties.getInstance(this.log).getXslTraccia(object.getTipoComunicazione());
	}

	@Override
	protected TipoXSL getTipoXsl(TracciaSDI object) throws Exception {
		switch(object.getTipoComunicazione()){
		case AT: return TipoXSL.TRACCIA_AT;
		case DT_PASS:
		case DT_ATT: return TipoXSL.NOTIFICA_DT;
		case EC: return TipoXSL.NOTIFICA_EC;
		case FAT_IN: throw new Exception("Usare classe: " + FatturaSingleFileExporter.class.getName());
		case FAT_OUT: throw new Exception("Usare classe: " + FatturaSingleFileExporter.class.getName());
		case MC: return TipoXSL.TRACCIA_MC;
		case MT:return TipoXSL.TRACCIA_MT;
		case NE:return TipoXSL.TRACCIA_NE;
		case NS:return TipoXSL.TRACCIA_NS;
		case RC:return TipoXSL.TRACCIA_RC;
		case SE: return TipoXSL.SCARTO_EC;
		default: return null;

		}
	}

	@Override
	public void export(TracciaSDI object, OutputStream out,
			org.govmix.proxy.fatturapa.web.commons.exporter.AbstractSingleFileExporter.FORMAT format)
					throws ExportException {
		switch(format) {
		case PDF: this.exportAsPdf(object, out);
		break;
		case RAW:this.exportAsRaw(object, out);
		break;
		case ZIP:this.exportAsZip(Arrays.asList(object), out);
		break;
		default: throw new ExportException("Formato ["+format+"] non supportato");
		}
	}

	@Override
	public TracciaSDI convertToObject(Long id) throws ExportException {
		try {
			return ((IDBTracciaSDIServiceSearch)this.tracciaSdiSearchDAO).get(id);
		} catch (ServiceException e) {
			throw new ExportException(e);
		} catch (NotFoundException e) {
			throw new ExportException(e);
		} catch (MultipleResultException e) {
			throw new ExportException(e);
		} catch (NotImplementedException e) {
			throw new ExportException(e);
		}
	}

	@Override
	public TracciaSDI convertToObject(String id) throws ExportException {
		return this.convertToObject(Long.parseLong(id));
	}

	@Override
	public byte[] getRawContent(TracciaSDI object) {
		return object.getRawData();
	}

	@Override
	public String getRawName(TracciaSDI object) {
		int endIndex =object.getNomeFile().lastIndexOf(".");

		return endIndex > 0 ? object.getNomeFile().substring(0, endIndex): object.getNomeFile();
	}

	@Override
	public String getRawExtension(TracciaSDI object) {
		if(TipoComunicazioneType.AT.equals(object.getTipoComunicazione())) {
			return "zip";
		}
		return super.getRawExtension(object);
	}

	@Override
	protected List<String> findCodiciDipartimento(String[] ids, boolean fatturazioneAttiva) throws ServiceException, NotFoundException{
		try {
			TracciaSDI traccia = this.convertToObject(ids[0]);
			List<String> lst = new ArrayList<String>();
			FatturaFilter filter = this.getFatturaBD().newFilter();
			filter.setIdentificativoSdi(traccia.getIdentificativoSdi());
			filter.setFatturazioneAttiva(fatturazioneAttiva);
			List<FatturaElettronica> findAll = this.getFatturaBD().findAll(filter);
			
			for(FatturaElettronica f: findAll) {
				lst.add(f.getCodiceDestinatario());
			}

			return lst;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}
