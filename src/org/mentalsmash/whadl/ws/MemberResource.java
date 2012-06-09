package org.mentalsmash.whadl.ws;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.ws.JSONSerializer.DescriptionType;

public class MemberResource {
	private final UnitMember m;

	public MemberResource(UnitMember m) {
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

		String result = serializer.serialize(m.getEquipmentPattern(), dt);
		return result;
		
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

		String result = serializer.serialize((Expression)m.getConditions(), dt);
		return result;
		
	}

}
