package org.govmix.proxy.fatturapa.web.commons.businessdelegate.filter;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.IDBServiceUtilities;
import org.openspcoop2.generic_project.dao.IExpressionConstructor;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;

public abstract class AbstractFilter implements IFilter {
	
	public static final String ALIAS_ID = "id";
	
	public AbstractFilter(IExpressionConstructor expressionConstructor) {
		this(expressionConstructor, false);
	}
	
	public AbstractFilter(IExpressionConstructor expressionConstructor, boolean simpleSearch) {
		this.expressionConstructor = expressionConstructor;
		this.filterSortList = new ArrayList<FilterSortWrapper>();
		this.simpleSearch = simpleSearch;
		this.listaFieldSimpleSearch = new ArrayList<IField>();
	}
	
	private IExpressionConstructor expressionConstructor;
	private Integer offset;
	private Integer limit;
	protected List<FilterSortWrapper> filterSortList;
	protected boolean simpleSearch = false;
	protected String simpleSearchString = null;
	protected List<IField> listaFieldSimpleSearch = null;

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public List<FilterSortWrapper> getFilterSortList() {
		return filterSortList;
	}

	public void setFilterSortList(List<FilterSortWrapper> filterSortList) {
		this.filterSortList = filterSortList;
	}

	protected IExpression newExpression() throws ServiceException, NotImplementedException {
		return this.expressionConstructor.newExpression();
	}
	
	@Override
	public IPaginatedExpression toPaginatedExpression() throws ServiceException {
		try {
			IPaginatedExpression exp = this.expressionConstructor.toPaginatedExpression(this.toExpression());
			
			if(this.filterSortList != null && !this.filterSortList.isEmpty()){
			
				for(FilterSortWrapper filterSort: this.filterSortList) {
					exp.addOrder(filterSort.getField(), filterSort.getSortOrder());
				}
			} else {
				FilterSortWrapper filterSort = getDefaultFilterSortWrapper();
				exp.addOrder(filterSort.getField(), filterSort.getSortOrder());
			}
			
			
			if(this.offset != null) {
				exp.offset(this.offset);
			}
	
			if(this.limit != null) {
				exp.limit(this.limit);
			}
			
			return exp;
			
		}catch(ExpressionException e) {
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			throw new ServiceException(e);
		} catch (NotImplementedException e) {
			throw new ServiceException(e);
		}
		
	}
	
	@Override
	public IExpression toExpression() throws ServiceException {
		if(!this.simpleSearch)
			return _toExpression();
		else 
			return _toSimpleSearchExpression();
	}
	
	public abstract IExpression _toExpression() throws ServiceException;
	
	/**
	 * Implementazione base della ricerca semplice, mette in or la ricerca per like anywere di tutti i campi indicati nella lista listaFieldSimpleSearch
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public IExpression _toSimpleSearchExpression() throws ServiceException {
		try {
			IExpression newExpression = this.newExpression(); 
			
			if(this.simpleSearchString != null && this.listaFieldSimpleSearch.size() > 0){
				List<IExpression> orExpr = this.getSimpleSearchExpression();
				newExpression.or(orExpr.toArray(new IExpression[orExpr.size()])); 
			}
			
			return newExpression;
		} catch (NotImplementedException e) {
			throw new ServiceException(e);
		} catch (ExpressionNotImplementedException e) {
			throw new ServiceException(e);
		} catch (ExpressionException e) {
			throw new ServiceException(e);
		}
	}

	protected String getRootTable() throws ExpressionException {
		ISQLFieldConverter converter = ((IDBServiceUtilities<?>)this.expressionConstructor).getFieldConverter();
		IModel<?> model = converter.getRootModel();
		return converter.toTable(model);
	}

	protected String getTable(IModel<?> model) throws ExpressionException {
		ISQLFieldConverter converter = ((IDBServiceUtilities<?>)this.expressionConstructor).getFieldConverter();
		return converter.toTable(model);
	}
	
	protected String getColumn(IField field) throws ExpressionException {
		return this.getColumn(field, false);
	}
	
	protected String getColumn(IField field, boolean includeTableName) throws ExpressionException {
		ISQLFieldConverter converter = ((IDBServiceUtilities<?>)this.expressionConstructor).getFieldConverter();
		return converter.toColumn(field,includeTableName);
	}
	
	protected FilterSortWrapper getDefaultFilterSortWrapper() throws ServiceException {
		try {
			CustomField baseField = new CustomField("id", Long.class, "id", getRootTable());
			FilterSortWrapper wrapper = new FilterSortWrapper();
			wrapper.setField(baseField);
			wrapper.setSortOrder(SortOrder.ASC);
			return wrapper;
		} catch (ExpressionException e) {
			throw new ServiceException(e);
		}
	}

	public boolean isSimpleSearch() {
		return simpleSearch;
	}

	public void setSimpleSearch(boolean simpleSearch) {
		this.simpleSearch = simpleSearch;
	}

	public String getSimpleSearchString() {
		return simpleSearchString;
	}

	public void setSimpleSearchString(String simpleSearchString) {
		this.simpleSearchString = simpleSearchString;
	}

	public List<IField> getListaFieldSimpleSearch() {
		return listaFieldSimpleSearch;
	}

	public void setListaFieldSimpleSearch(List<IField> listaFieldSimpleSearch) {
		this.listaFieldSimpleSearch = listaFieldSimpleSearch;
	}
	
	protected List<IExpression> getSimpleSearchExpression() throws ServiceException, ExpressionNotImplementedException, ExpressionException, NotImplementedException{
		List<IExpression> expressions = new ArrayList<IExpression>();
		for (IField field : this.listaFieldSimpleSearch) {
			IExpression expr = this.newExpression();
			expr.ilike(field, this.simpleSearchString,LikeMode.ANYWHERE);
			expressions.add(expr);
		}
		return expressions;
	}
}
