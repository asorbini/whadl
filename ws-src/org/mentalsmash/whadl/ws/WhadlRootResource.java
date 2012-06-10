package org.mentalsmash.whadl.ws;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.mentalsmash.whadl.Whadl;
import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.plugins.gw40k.armies.GW40kArmiesWhadlPlugin;
import org.mentalsmash.whadl.plugins.gw40k.builds.GW40kBuildsWhadlPlugin;
import org.mentalsmash.whadl.plugins.gw40k.core.GW40kCoreWhadlPlugin;
import org.mentalsmash.whadl.ws.JSONSerializer.DescriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

@Path("")
public class WhadlRootResource {

	private final Whadl w;

	public WhadlRootResource() {
		// if (w == null) {
		System.out.println("CREATING ROOT RESOURCE");
		try {
			w = new Whadl(100);
			w.loadPlugin(GW40kCoreWhadlPlugin.class);
			w.loadPlugin(GW40kArmiesWhadlPlugin.class);
			w.loadPlugin(GW40kBuildsWhadlPlugin.class);
		} catch (Exception e) {
			throw new WhadlRuntimeException(e);
		}

	}

	private final static Logger log = LoggerFactory
			.getLogger(WhadlRootResource.class);

	@GET
	@Path("/armies")
	@Produces( { "text/plain", "application/json" })
	public String getArmies(
			@DefaultValue("long") @QueryParam("desc") String descType) {
		JSONSerializer serializer = new JSONSerializer();

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize(w.getArmies(), dt);
		return result;
	}

	@Path("/armies/{armyName}")
	public ArmyResource getArmyResource(@PathParam("armyName") String armyName) {
		if (armyName == null) {
			throw new NotFoundException("No army name supplied");
		}

		Army a = w.getArmy(armyName);
		if (a == null) {
			throw new NotFoundException("No army named " + armyName);
		}

		return new ArmyResource(a);
	}

	@GET
	@Path("/lists")
	@Produces( { "text/plain", "application/json" })
	public String getArmyInstances(
			@DefaultValue("long") @QueryParam("desc") String descType) {
		JSONSerializer serializer = new JSONSerializer();

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize(w.getArmyInstances(), dt);
		return result;
	}

	@Path("/lists/{armyName}")
	public ArmyInstanceResource getBuildResource(
			@PathParam("armyName") String armyName) {
		if (armyName == null) {
			throw new NotFoundException("No build name supplied");
		}

		ArmyInstance a = w.getArmyInstance(armyName);
		if (a == null) {
			throw new NotFoundException("No build named " + armyName);
		}

		return new ArmyInstanceResource(a);
	}

	@POST
	@Path("/validator/")
	@Consumes("application/x-www-form-urlencoded")
	public Response validateList(@FormParam("list") String listDefinition,
			@QueryParam("tries") int tries, @Context UriInfo uriInfo) {
		if (listDefinition == null) {
			log.error("No list definition supplied...");
			throw new WebApplicationException();
		}
		
		
		ArmyInstance ai = null;
		
		try {
			if (tries == 0) {
				tries = 10;
			}
			
			log.info("Parsing supplied list definition...");
			ai = w.parseArmyInstance(listDefinition);
			log.info("List definition parsed, now validating... (result="+ai+")");
			w.validate(ai,tries,false);
			log.info("List validated. TOTAL POINTS: "+ai.getFinalCost());
			
		} catch (Exception ex) {

			String errMsg = getMostInnerCauseMsg(ex);
			
			
			log.error("Error: "+errMsg);
			StringWriter writ = new StringWriter();
			PrintWriter w = new PrintWriter(writ);
			ex.printStackTrace(w);
			log.error("Stack trace: "+writ.toString());
			
			
			WhadlOperationResult res = new WhadlOperationResult(false, errMsg);
			String serRes = new JSONSerializer(2).serialize(res,
					DescriptionType.LONG);

			ResponseBuilder builder = new ResponseBuilderImpl();
			builder.status(Status.OK);
			GenericEntity<String> ent = new GenericEntity<String>(serRes,
					String.class);
			builder.entity(ent);
			builder.type("text/plain");
			
			return builder.build();
		}

		WhadlOperationResult res = new WhadlOperationResult(true, ""+ai.getFinalCost());
		String serRes = new JSONSerializer(2).serialize(res,
				DescriptionType.LONG);

		ResponseBuilder builder = new ResponseBuilderImpl();
		builder.status(Status.OK);
		GenericEntity<String> ent = new GenericEntity<String>(serRes,
				String.class);
		builder.entity(ent);
		builder.type("text/plain");
		
		return builder.build();
	}
	
	
	private String appendCauses(String msg, Throwable ex){
		if (ex.getCause() != null) {
			Throwable cause = ex.getCause();
			msg = msg+" - Caused by: "+cause.getMessage();
			
			if (cause.getCause() != null){
				return appendCauses(msg,cause);
			}
		}
		
		return msg;
	}
	
	private String getMostInnerCauseMsg(Throwable ex) {
		if (ex.getCause() != null) {
			return getMostInnerCauseMsg(ex.getCause());
		} else {
			return ex.getMessage();
		}
	}
	
	@Path("/echo")
	@Produces("text/whadl")
	public String echo(@FormParam("content") String content){
		if (content != null) {
			return content;
		} else {
			return "";
		}
	}
}
