//package org.mentalsmash.whadl.ws;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import javax.ws.rs.core.Application;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.joran.JoranConfigurator;
//import ch.qos.logback.core.joran.spi.JoranException;
//import ch.qos.logback.core.util.StatusPrinter;
//
//import com.sun.grizzly.http.SelectorThread;
//import com.sun.grizzly.http.embed.GrizzlyWebServer;
//import com.sun.grizzly.http.servlet.ServletAdapter;
//import com.sun.grizzly.tcp.StaticResourcesAdapter;
//import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
//import com.sun.grizzly.tcp.http11.GrizzlyRequest;
//import com.sun.grizzly.tcp.http11.GrizzlyResponse;
//import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
//import com.sun.jersey.spi.container.servlet.ServletContainer;
//
//public class WhadlWSApplication extends Application {
//
//	private final static Logger log = LoggerFactory
//			.getLogger(WhadlWSApplication.class);
//
//	@Override
//	public Set<Object> getSingletons() {
//		WhadlRootResource root = new WhadlRootResource();
//		Set<Object> result = new HashSet<Object>();
//		result.add(root);
//		return result;
//	}
//
//	public static void main(String[] args) throws IOException {
//		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//
//		try {
//			JoranConfigurator configurator = new JoranConfigurator();
//			configurator.setContext(lc);
//			lc.reset();
//			configurator.doConfigure("data/logback.xml");
//		} catch (JoranException je) {
//			// StatusPrinter will handle this
//		}
//		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
//
//		GrizzlyWebServer gws = new GrizzlyWebServer(9998,
//				"/Users/as/AndroidWorkspace/whadl-read-only/client");
//
//		ServletAdapter whadlAdapter = new ServletAdapter();
//		whadlAdapter.addInitParameter(
//				"com.sun.jersey.config.property.packages",
//				"org.mentalsmash.whadl.ws");
//		whadlAdapter.setContextPath("/whadl");
//		whadlAdapter.setServletInstance(new ServletContainer(
//				new WhadlWSApplication()));
//		gws.addGrizzlyAdapter(whadlAdapter, new String[] { "/whadl" });
//
//		GrizzlyAdapter filesAdapter = new GrizzlyAdapter() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public void service(GrizzlyRequest arg0, GrizzlyResponse arg1) {
//
//			}
//		};
//		filesAdapter.setHandleStaticResources(true);
//
//		gws.addGrizzlyAdapter(filesAdapter, new String[] { "/client" });
//
//		log.info("Starting grizzly...");
//		gws.start();
//
//		System.in.read();
//		gws.stop();
//		log.info("Grizzly stopped.");
//		System.exit(0);
//	}
//}


package org.mentalsmash.whadl.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.sun.grizzly.http.SelectorThread;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.tcp.StaticResourcesAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class WhadlWSApplication extends Application {

	private final static Logger log = LoggerFactory.getLogger(WhadlWSApplication.class);

	@Override
	public Set<Object> getSingletons() {
		WhadlRootResource root = new WhadlRootResource();
		Set<Object> result = new HashSet<Object>();
		result.add(root);
		return result;
	}


	public static void main(String[] args) throws IOException {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure("data/logback.xml");
		} catch (JoranException je) {
			// StatusPrinter will handle this
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

		GrizzlyWebServer gws = new GrizzlyWebServer(9998, "./client");
		
		ServletAdapter whadlAdapter = new ServletAdapter();
		whadlAdapter.addInitParameter(
				"com.sun.jersey.config.property.packages",
				"org.mentalsmash.whadl.ws");
		whadlAdapter.setContextPath("/whadl");
		whadlAdapter.setServletInstance(new ServletContainer(new WhadlWSApplication()));
		gws.addGrizzlyAdapter(whadlAdapter, new String[]{"/whadl"});

		
		GrizzlyAdapter filesAdapter = new GrizzlyAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void service(GrizzlyRequest arg0, GrizzlyResponse arg1) {
				
			}
		};
		filesAdapter.setHandleStaticResources(true);
		
		gws.addGrizzlyAdapter(filesAdapter, new String[]{"/client"});
		
		log.info("Starting grizzly...");
		gws.start();
		
		System.in.read();
		gws.stop();
		log.info("Grizzly stopped.");
		System.exit(0);
	}
}
