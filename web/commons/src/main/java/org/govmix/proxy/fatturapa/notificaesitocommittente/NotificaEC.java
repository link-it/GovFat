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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.09 at 03:34:31 PM CEST 
//


package org.govmix.proxy.fatturapa.notificaesitocommittente;

import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NotificaEC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotificaEC">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificativoSdi" type="{http://www.govmix.org/proxy/fatturapa/notificaesitocommittente}IdentificativoSdI"/>
 *         &lt;element name="posizione" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="esito" type="{http://www.govmix.org/proxy/fatturapa/notificaesitocommittente}EsitoCommittente"/>
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificaEC", propOrder = {
    "identificativoSdi",
    "posizione",
    "esito",
    "descrizione"
})

@XmlRootElement
public class NotificaEC {

    @XmlElement(required = true)
    protected BigInteger identificativoSdi;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger posizione;
    @XmlElement(required = true)
    protected EsitoCommittente esito;
    protected String descrizione;

    public static void main(String[] args) throws Exception {
    	JAXBContext ctx = JAXBContext.newInstance(NotificaEC.class.getPackage().getName());
		Marshaller masrshal =  ctx.createMarshaller();
		NotificaEC jaxbElement = new NotificaEC();
		jaxbElement.setEsito(EsitoCommittente.EC_02);
		jaxbElement.setDescrizione("Importo non coerente e committente errato");
		jaxbElement.setIdentificativoSdi(new BigInteger("111"));
		jaxbElement.setPosizione(new BigInteger("1"));
		masrshal.marshal(jaxbElement, System.out);
	}
    
    /**
     * Gets the value of the identificativoSdi property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIdentificativoSdi() {
        return identificativoSdi;
    }

    /**
     * Sets the value of the identificativoSdi property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIdentificativoSdi(BigInteger value) {
        this.identificativoSdi = value;
    }

    /**
     * Gets the value of the posizione property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPosizione() {
        return posizione;
    }

    /**
     * Sets the value of the posizione property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPosizione(BigInteger value) {
        this.posizione = value;
    }

    /**
     * Gets the value of the esito property.
     * 
     * @return
     *     possible object is
     *     {@link EsitoCommittente }
     *     
     */
    public EsitoCommittente getEsito() {
        return esito;
    }

    /**
     * Sets the value of the esito property.
     * 
     * @param value
     *     allowed object is
     *     {@link EsitoCommittente }
     *     
     */
    public void setEsito(EsitoCommittente value) {
        this.esito = value;
    }

    /**
     * Gets the value of the descrizione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Sets the value of the descrizione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrizione(String value) {
        this.descrizione = value;
    }

}
