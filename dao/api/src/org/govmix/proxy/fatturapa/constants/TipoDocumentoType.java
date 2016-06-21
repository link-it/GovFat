/*
 * ProxyFatturaPA - Gestione del formato Fattura Elettronica 
 * http://www.gov4j.it/fatturapa
 * 
 * Copyright (c) 2014-2015 Link.it srl (http://link.it). 
 * Copyright (c) 2014-2015 Provincia Autonoma di Bolzano (http://www.provincia.bz.it/). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.govmix.proxy.fatturapa.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento FatturaElettronica.tipoDocumento xsd (tipo:string) 
 *
 * @author Papandrea Giuseppe (papandrea@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "TipoDocumentoType")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum TipoDocumentoType implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("TD01")
	TD01 ("TD01"),
	@javax.xml.bind.annotation.XmlEnumValue("TD02")
	TD02 ("TD02"),
	@javax.xml.bind.annotation.XmlEnumValue("TD03")
	TD03 ("TD03"),
	@javax.xml.bind.annotation.XmlEnumValue("TD04")
	TD04 ("TD04"),
	@javax.xml.bind.annotation.XmlEnumValue("TD05")
	TD05 ("TD05"),
	@javax.xml.bind.annotation.XmlEnumValue("TD06")
	TD06 ("TD06");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoDocumentoType(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoDocumentoType object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof TipoDocumentoType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((TipoDocumentoType)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toString();
  	}
  	public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoDocumentoType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoDocumentoType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoDocumentoType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static TipoDocumentoType toEnumConstant(String value){
		TipoDocumentoType res = null;
		if(TipoDocumentoType.TD01.getValue().equals(value)){
			res = TipoDocumentoType.TD01;
		}else if(TipoDocumentoType.TD02.getValue().equals(value)){
			res = TipoDocumentoType.TD02;
		}else if(TipoDocumentoType.TD03.getValue().equals(value)){
			res = TipoDocumentoType.TD03;
		}else if(TipoDocumentoType.TD04.getValue().equals(value)){
			res = TipoDocumentoType.TD04;
		}else if(TipoDocumentoType.TD05.getValue().equals(value)){
			res = TipoDocumentoType.TD05;
		}else if(TipoDocumentoType.TD06.getValue().equals(value)){
			res = TipoDocumentoType.TD06;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		TipoDocumentoType res = null;
		if(TipoDocumentoType.TD01.toString().equals(value)){
			res = TipoDocumentoType.TD01;
		}else if(TipoDocumentoType.TD02.toString().equals(value)){
			res = TipoDocumentoType.TD02;
		}else if(TipoDocumentoType.TD03.toString().equals(value)){
			res = TipoDocumentoType.TD03;
		}else if(TipoDocumentoType.TD04.toString().equals(value)){
			res = TipoDocumentoType.TD04;
		}else if(TipoDocumentoType.TD05.toString().equals(value)){
			res = TipoDocumentoType.TD05;
		}else if(TipoDocumentoType.TD06.toString().equals(value)){
			res = TipoDocumentoType.TD06;
		}
		return res;
	}
}
