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
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.ws.JSONSerializer.DescriptionType;

import com.sun.jersey.api.NotFoundException;

public class UnitResource {

	private final JSONSerializer serializer = new JSONSerializer();
	private final Unit u;

	public UnitResource(Unit u) {
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
	public MemberResource getMemberResource(
			@PathParam("memberName") String memberName) {

		if (memberName == null) {
			throw new NotFoundException("No member name supplied");
		}

		UnitMember m = u.getMember(memberName);
		if (m == null) {
			throw new NotFoundException("No member named " + memberName
					+ " in unit " + u + " of army " + u.getArmy());
		}

		return new MemberResource(m);
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
		String result = this.serializer.serialize(u.getSlotsPattern(), dt);
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
				.serialize(u.getCompositionPattern(), dt);
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
		String result = this.serializer.serialize(u.getUpgradesPattern(), dt);
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
		String result = this.serializer.serialize((Expression) u
				.getCostExpression(), dt);
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
				.serialize(u.getLinkedUnitsPattern(), dt);
		return result;
	}

	@GET
	@Path("/conditions")
	@Produces({"text/plain","application/json"})
	public String getConditions(
			@DefaultValue("long") @QueryParam("desc") String descType) {

		DescriptionType dt = null;

		try {
			dt = DescriptionType.byString(descType);
		} catch (WhadlException ex) {
			throw new WebApplicationException();
		}

		String result = serializer
				.serialize((Expression) u.getConditions(), dt);
		return result;

	}
}
