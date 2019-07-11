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
package org.govmix.proxy.fatturapa.orm.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.apache.log4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.govmix.proxy.fatturapa.orm.IdSip;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;

import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;

import org.govmix.proxy.fatturapa.orm.SIP;
import org.govmix.proxy.fatturapa.orm.dao.jdbc.JDBCServiceManager;

/**     
 * JDBCSIPServiceImpl
 *
 * @author Giuseppe Papandrea (papandrea@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCSIPServiceImpl extends JDBCSIPServiceSearchImpl
	implements IJDBCServiceCRUDWithId<SIP, IdSip, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, SIP sip, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object sip
		sqlQueryObjectInsert.addInsertTable(this.getSIPFieldConverter().toTable(SIP.model()));
		sqlQueryObjectInsert.addInsertField(this.getSIPFieldConverter().toColumn(SIP.model().REGISTRO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getSIPFieldConverter().toColumn(SIP.model().ANNO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getSIPFieldConverter().toColumn(SIP.model().NUMERO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getSIPFieldConverter().toColumn(SIP.model().STATO_CONSEGNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getSIPFieldConverter().toColumn(SIP.model().ERRORE_TIMEOUT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getSIPFieldConverter().toColumn(SIP.model().DATA_ULTIMA_CONSEGNA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getSIPFieldConverter().toColumn(SIP.model().RAPPORTO_VERSAMENTO,false),"?");

		// Insert sip
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getSIPFetch().getKeyGeneratorObject(SIP.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(sip.getRegistro(),SIP.model().REGISTRO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(sip.getAnno(),SIP.model().ANNO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(sip.getNumero(),SIP.model().NUMERO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(sip.getStatoConsegna(),SIP.model().STATO_CONSEGNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(sip.getErroreTimeout(),SIP.model().ERRORE_TIMEOUT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(sip.getDataUltimaConsegna(),SIP.model().DATA_ULTIMA_CONSEGNA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(sip.getRapportoVersamento(),SIP.model().RAPPORTO_VERSAMENTO.getFieldType())
		);
		sip.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSip oldId, SIP sip, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdSIP(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = sip.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: sip.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			sip.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, sip, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, SIP sip, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		
		boolean setIdMappingResolutionBehaviour = 
			(idMappingResolutionBehaviour==null) ||
			org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) ||
			org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour);
			


		// Object sip
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getSIPFieldConverter().toTable(SIP.model()));
		boolean isUpdate_sip = true;
		java.util.List<JDBCObject> lstObjects_sip = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getSIPFieldConverter().toColumn(SIP.model().REGISTRO,false), "?");
		lstObjects_sip.add(new JDBCObject(sip.getRegistro(), SIP.model().REGISTRO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getSIPFieldConverter().toColumn(SIP.model().ANNO,false), "?");
		lstObjects_sip.add(new JDBCObject(sip.getAnno(), SIP.model().ANNO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getSIPFieldConverter().toColumn(SIP.model().NUMERO,false), "?");
		lstObjects_sip.add(new JDBCObject(sip.getNumero(), SIP.model().NUMERO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getSIPFieldConverter().toColumn(SIP.model().STATO_CONSEGNA,false), "?");
		lstObjects_sip.add(new JDBCObject(sip.getStatoConsegna(), SIP.model().STATO_CONSEGNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getSIPFieldConverter().toColumn(SIP.model().ERRORE_TIMEOUT,false), "?");
		lstObjects_sip.add(new JDBCObject(sip.getErroreTimeout(), SIP.model().ERRORE_TIMEOUT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getSIPFieldConverter().toColumn(SIP.model().DATA_ULTIMA_CONSEGNA,false), "?");
		lstObjects_sip.add(new JDBCObject(sip.getDataUltimaConsegna(), SIP.model().DATA_ULTIMA_CONSEGNA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getSIPFieldConverter().toColumn(SIP.model().RAPPORTO_VERSAMENTO,false), "?");
		lstObjects_sip.add(new JDBCObject(sip.getRapportoVersamento(), SIP.model().RAPPORTO_VERSAMENTO.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects_sip.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_sip) {
			// Update sip
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_sip.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSip id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getSIPFieldConverter().toTable(SIP.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getSIPFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSip id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getSIPFieldConverter().toTable(SIP.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getSIPFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSip id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getSIPFieldConverter().toTable(SIP.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getSIPFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getSIPFieldConverter().toTable(SIP.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getSIPFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getSIPFieldConverter().toTable(SIP.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getSIPFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getSIPFieldConverter().toTable(SIP.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getSIPFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSip oldId, SIP sip, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, sip,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, sip,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, SIP sip, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, sip,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, sip,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, SIP sip) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (sip.getId()!=null) && (sip.getId()>0) ){
			longId = sip.getId();
		}
		else{
			IdSip idSIP = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,sip);
			longId = this.findIdSIP(jdbcProperties,log,connection,sqlQueryObject,idSIP,false);
			if(longId == null){
				return; // entry not exists
			}
		}		
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		// Object sip
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getSIPFieldConverter().toTable(SIP.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete sip
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdSip idSIP) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdSIP(jdbcProperties, log, connection, sqlQueryObject, idSIP, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getSIPFieldConverter()));

	}

	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {

		java.util.List<Long> lst = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, new JDBCPaginatedExpression(expression));
		
		for(Long id : lst) {
			this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		}
		
		return new NonNegativeNumber(lst.size());
	
	}



	// -- DB
	
	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotImplementedException, Exception {
		this._delete(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
}
