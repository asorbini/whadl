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
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.ws.JSONSerializer.DescriptionType;

import com.sun.jersey.api.NotFoundException;

public class ArmyInstanceResource {

	private final JSONSerializer serializer = new JSONSerializer();
	private final ArmyInstance ai;

	public ArmyInstanceResource(ArmyInstance ai) {
		this.ai = ai;
	}

	@GET
	@Produces({"text/plain","application/json"})
	public String getArmyInstance(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize(ai, dt);
		return result;
	}

	@GET
	@Produces({"text/plain","application/json"})
	@Path("/units")
	public String getUnits(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize((Collection<?>) ai.getUnits(), dt);
		return result;
	}

	@Path("/units/{unitName}")
	public UnitInstanceResource getUnitResource(
			@PathParam("unitName") String unitName) {

		if (unitName == null) {
			throw new NotFoundException("No unit name supplied");
		}

		UnitInstance u = ai.getUnit(unitName);
		if (u == null) {
			throw new NotFoundException("No unit named " + unitName
					+ " in army " + ai);
		}

		return new UnitInstanceResource(u);
	}

	@GET
	@Path("/cost")
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

		String result = serializer.serialize(new LiteralExpression(ai
				.getFinalCost()), dt);
		return result;

	}

	@GET
	@Path("/type")
	@Produces({"text/plain","application/json"})
	public String getType(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}
		String result = this.serializer.serialize(new LiteralExpression(ai
				.getTypeName()), dt);
		return result;
	}

	@GET
	@Path("/name")
	@Produces({"text/plain","application/json"})
	public String getName(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}
		String result = this.serializer.serialize(new LiteralExpression(ai
				.getName()), dt);
		return result;
	}

}
