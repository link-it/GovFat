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
package org.govmix.proxy.fatturapa.orm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.govmix.proxy.fatturapa.orm.constants.NomePccOperazioneType;
import java.io.Serializable;


/** <p>Java class for id-operazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-operazione">
 * 		&lt;sequence>
 * 			&lt;element name="nome" type="{http://www.govmix.org/proxy/fatturapa/orm}NomePccOperazioneType" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Giuseppe Papandrea (papandrea@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "id-operazione", 
  propOrder = {
  	"nome"
  }
)

@XmlRootElement(name = "id-operazione")

public class IdOperazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IdOperazione() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public void set_value_nome(String value) {
    this.nome = (NomePccOperazioneType) NomePccOperazioneType.toEnumConstantFromString(value);
  }

  public String get_value_nome() {
    if(this.nome == null){
    	return null;
    }else{
    	return this.nome.toString();
    }
  }

  public org.govmix.proxy.fatturapa.orm.constants.NomePccOperazioneType getNome() {
    return this.nome;
  }

  public void setNome(org.govmix.proxy.fatturapa.orm.constants.NomePccOperazioneType nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_nome;

  @XmlElement(name="nome",required=true,nillable=false)
  protected NomePccOperazioneType nome;

}
