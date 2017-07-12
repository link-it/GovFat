/*
 * ProxyFatturaPA - Gestione del formato Fattura Elettronica 
 * http://www.gov4j.it/fatturapa
 * 
 * Copyright (c) 2014-2017 Link.it srl (http://link.it). 
 * Copyright (c) 2014-2017 Provincia Autonoma di Bolzano (http://www.provincia.bz.it/). 
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
// Generated on: 2014.11.06 at 12:42:43 PM CET 
//


package org.govmix.proxy.fatturapa.recuperofatture;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.govmix.proxy.fatturapa.recuperofatture package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ListaFattureNonConsegnateResponse_QNAME = new QName("http://www.govmix.org/proxy/fatturapa/recuperoFatture", "listaFattureNonConsegnateResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.govmix.proxy.fatturapa.recuperofatture
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Fattura }
     * 
     */
    public Fattura createFattura() {
        return new Fattura();
    }

    /**
     * Create an instance of {@link ListaFattureNonConsegnateResponse }
     * 
     */
    public ListaFattureNonConsegnateResponse createListaFattureNonConsegnateResponse() {
        return new ListaFattureNonConsegnateResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListaFattureNonConsegnateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.govmix.org/proxy/fatturapa/recuperoFatture", name = "listaFattureNonConsegnateResponse")
    public JAXBElement<ListaFattureNonConsegnateResponse> createListaFattureNonConsegnateResponse(ListaFattureNonConsegnateResponse value) {
        return new JAXBElement<ListaFattureNonConsegnateResponse>(_ListaFattureNonConsegnateResponse_QNAME, ListaFattureNonConsegnateResponse.class, null, value);
    }

}
