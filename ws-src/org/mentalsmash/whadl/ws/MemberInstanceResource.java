package org.mentalsmash.whadl.ws;

import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.ws.JSONSerializer.DescriptionType;

public class MemberInstanceResource {
	private final JSONSerializer serializer = new JSONSerializer();
	private final UnitMemberInstance m;

	public MemberInstanceResource(UnitMemberInstance m) {
		this.m = m;
	}

	@GET
	@Produces({"text/plain","application/json"})
	public String getMember(
			@DefaultValue("long") @QueryParam("desc") String descType) {
		JSONSerializer serializer = new JSONSerializer();

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize(m, dt);
		return result;
	}

	@GET
	@Path("/equipment")
	@Produces({"text/plain","application/json"})
	public String getEquipment(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		JSONSerializer serializer = new JSONSerializer();

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize((Collection<?>) m.getEquipments(),
				dt);
		return result;

	}

	@GET
	@Path("/cost")
	@Produces({"text/plain","application/json"})
	public String getCost(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}
		String result = this.serializer.serialize(new LiteralExpression(m
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
		String result = this.serializer.serialize(new LiteralExpression(m
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
		String result = this.serializer.serialize(new LiteralExpression(m
				.getName()), dt);
		return result;
	}

}
