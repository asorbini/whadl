package org.mentalsmash.whadl.ws;

import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.ws.JSONSerializer.DescriptionType;

import com.sun.jersey.api.NotFoundException;

public class ArmyResource {

	private final Army a;

	public ArmyResource(Army a) {
		this.a = a;
	}

	@GET
	@Produces({"text/plain","application/json"})
	public String getArmy(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		JSONSerializer serializer = new JSONSerializer();

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize(a, dt);
		return result;
	}

	@GET
	@Produces({"text/plain","application/json"})
	@Path("/units")
	public String getUnits(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		JSONSerializer serializer = new JSONSerializer();

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize((Collection<?>) a.getUnits(), dt);
		return result;
	}

	@Path("/units/{unitName}")
	public UnitResource getUnitResource(
			@PathParam("unitName") String unitName) {
		
		if (unitName == null) {
			throw new NotFoundException("No unit name supplied");
		}

		Unit u = a.getUnit(unitName);
		if (u == null) {
			throw new NotFoundException("No unit named " + unitName
					+ " in army " + a);
		}

		return new UnitResource(u);
	}
	
	
	@GET
	@Path("/conditions")
	@Produces({"text/plain","application/json"})
	public String getConditions(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		JSONSerializer serializer = new JSONSerializer();
		
		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer
				.serialize((Expression) a.getConditions(), dt);
		return result;

	}

	

}
