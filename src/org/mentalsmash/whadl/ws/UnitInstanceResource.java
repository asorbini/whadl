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
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.expressions.LiteralExpression;
import org.mentalsmash.whadl.ws.JSONSerializer.DescriptionType;

import com.sun.jersey.api.NotFoundException;

public class UnitInstanceResource {

	private final JSONSerializer serializer = new JSONSerializer();
	private final UnitInstance u;

	public UnitInstanceResource(UnitInstance u) {
		this.u = u;
	}

	@GET
	@Produces({"text/plain","application/json"})
	public String getUnit(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		JSONSerializer serializer = new JSONSerializer();

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer.serialize(u, dt);
		return result;
	}

	@GET
	@Produces({"text/plain","application/json"})
	@Path("/members")
	public String getMembers(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		JSONSerializer serializer = new JSONSerializer();

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer
				.serialize((Collection<?>) u.getMembers(), dt);
		return result;
	}

	@Path("/members/{memberName}")
	public MemberInstanceResource getMemberResource(
			@PathParam("memberName") String memberName) {

		if (memberName == null) {
			throw new NotFoundException("No member name supplied");
		}

		UnitMemberInstance m = u.getMember(memberName);
		if (m == null) {
			throw new NotFoundException("No member named " + memberName
					+ " in unit " + u + " of army " + u.getArmy());
		}

		return new MemberInstanceResource(m);
	}

	@GET
	@Path("/slots")
	@Produces({"text/plain","application/json"})
	public String getSlots(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}
		String result = this.serializer.serialize(u.getSlotPattern().toMap(),
				dt);
		return result;
	}

	@GET
	@Path("/composition")
	@Produces({"text/plain","application/json"})
	public String getComposition(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}
		String result = this.serializer
				.serialize(u.getCompositionPattern().toMap(), dt);
		return result;
	}

	@GET
	@Path("/upgrades")
	@Produces({"text/plain","application/json"})
	public String getUpgrades(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}
		String result = this.serializer.serialize(u.getUpgradesPattern().toMap(), dt);
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
		String result = this.serializer.serialize(new LiteralExpression(u
				.getFinalCost()), dt);
		return result;
	}

	@GET
	@Path("/linked")
	@Produces({"text/plain","application/json"})
	public String getLinkedUnits(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = this.serializer
				.serialize(u.getLinkedUnitsPattern().toMap(), dt);
		
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
		String result = this.serializer.serialize(new LiteralExpression(u
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
		String result = this.serializer.serialize(new LiteralExpression(u
				.getName()), dt);
		return result;
	}

}
