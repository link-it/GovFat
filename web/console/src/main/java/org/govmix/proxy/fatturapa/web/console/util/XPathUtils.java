package org.govmix.proxy.fatturapa.web.console.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.govmix.proxy.fatturapa.web.commons.utils.LoggerManager;

public class XPathUtils {

	private static XPathFactory xPathfactory;
	private static boolean init;

	private static synchronized void init() {
		if(!init) {
			xPathfactory = XPathFactory.newInstance();
		}
		init = true;
	}
	
	public static String validaXpath(String input) {
		init();
		XPath xpath = xPathfactory.newXPath();
		try {
			LoggerManager.getConsoleLogger().debug("Compilazione dell'espressione ["+input+"]...");
			xpath.compile(input);
			LoggerManager.getConsoleLogger().debug("Compilazione dell'espressione ["+input+"] completata con successo");
		} catch(XPathExpressionException e) {
			String msg = e.getMessage();
			Throwable t = e.getCause();
			while(msg == null && t != null) {
				msg = t.getMessage();
				t = t.getCause();
			}
			LoggerManager.getConsoleLogger().error("Errore durante la valutazione dell'xPath:" + msg, e);
			return msg;
		}
		return null;
	}
}