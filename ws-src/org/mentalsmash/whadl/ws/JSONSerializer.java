package org.mentalsmash.whadl.ws;

import java.util.Collection;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.EquipmentInstance;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.UnitSlot;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.patterns.AlternativePattern;
import org.mentalsmash.whadl.model.patterns.ConjunctEntityPatternSet;
import org.mentalsmash.whadl.model.patterns.EmptyPattern;
import org.mentalsmash.whadl.model.patterns.OptionalPattern;
import org.mentalsmash.whadl.model.patterns.Pattern;
import org.mentalsmash.whadl.model.patterns.PatternContentMap;
import org.mentalsmash.whadl.model.patterns.SinglePattern;

public class JSONSerializer {

	public static enum DescriptionType {
		LONG, SHORT;

		public static DescriptionType byString(String val)
				throws WhadlException {
			for (DescriptionType dt : DescriptionType.values()) {
				if (dt.toString().equalsIgnoreCase(val)) {
					return dt;
				}
			}

			throw new WhadlException("Unknown DescryptionType: " + val);
		}
	}

	private int indent = 2;

	public JSONSerializer() {

	}

	public JSONSerializer(int indent) {
		this.indent = indent;
	}

	public String serialize(PatternContentMap map, DescriptionType descType) {
		try {
			JSONObject mapObj = new JSONObject(map);
			return mapObj.toString(indent);
		} catch (Exception ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing map " + map + ": " + ex);
		}
	}

	public String serialize(Expression exp, DescriptionType descType) {
		try {
			JSONObject expObj = new JSONObject();
			expObj.put("value", exp.toString());
			return expObj.toString(indent);
		} catch (Exception ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing expression " + exp
							+ ": " + ex);
		}
	}

	public String serialize(Collection<?> col, DescriptionType descType) {
		try {
			if (col == null) {
				throw new IllegalArgumentException("null collection");
			}

			JSONArray collArray = new JSONArray();

			Iterator<?> it = col.iterator();

			if (it == null) {
				throw new IllegalArgumentException("null collection iterator");
			}

			if (collArray == null) {
				throw new IllegalArgumentException("null json collection");
			}

			while (it.hasNext()) {
				Object next = it.next();

				if (next == null && !(next instanceof Entity)
						&& !(next instanceof Collection<?>)
						&& !(next instanceof Pattern)
						&& !(next instanceof Expression)) {
					throw new WhadlRuntimeException(
							"Cannot serialize an object "
									+ "which is not an entity, "
									+ "a pattern, an expression or a collection: "
									+ next);
				}

				if (next instanceof Entity) {

					collArray.put(new JSONObject(this.serialize((Entity) next,
							descType)));
				} else if (next instanceof Collection<?>) {
					collArray.put(new JSONObject(this.serialize(
							(Collection<?>) next, descType)));
				} else if (next instanceof Expression) {
					Expression exp = (Expression) next;
					collArray
							.put(new JSONObject(this.serialize(exp, descType)));
				} else {
					collArray.put(new JSONObject(this.serialize((Pattern) next,
							descType)));
				}
			}

			return collArray.toString(indent);

		} catch (Exception ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing collection " + col
							+ ": " + ex, ex);
		}

	}

	public String serialize(Entity e, DescriptionType descType) {
		if (e instanceof Army) {
			return this.serialize((Army) e, descType);
		} else if (e instanceof ArmyInstance) {
			return this.serialize((ArmyInstance) e, descType);
		} else if (e instanceof Unit) {
			return this.serialize((Unit) e, descType);
		} else if (e instanceof UnitInstance) {
			return this.serialize((UnitInstance) e, descType);
		} else if (e instanceof UnitMember) {
			return this.serialize((UnitMember) e, descType);
		} else if (e instanceof UnitMemberInstance) {
			return this.serialize((UnitMemberInstance) e, descType);
		} else if (e instanceof UnitSlot) {
			return this.serialize((UnitSlot) e, descType);
		} else if (e instanceof EquipmentInstance) {
			return this.serialize((EquipmentInstance) e, descType);
		}

		throw new WhadlRuntimeException("Don't know how to serialize entity: "
				+ e + " (type: " + e.getClass() + ")");
	}

	public String serialize(EquipmentInstance eq, DescriptionType descType) {
		try {
			JSONObject eqObj = new JSONObject();
			eqObj.put("name", eq.getName());
			eqObj.put("cost", eq.getCost());
			return eqObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing equipment instance "
							+ eq.getName() + ": " + ex);
		}
	}

	public String serialize(UnitSlot slot, DescriptionType descType) {
		try {
			JSONObject slotObj = new JSONObject();
			slotObj.put("slot", slot.getName());
			return slotObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing slot " + slot.getName()
							+ ": " + ex);
		}
	}

	public String serialize(Army a, DescriptionType descType) {
		try {
			JSONObject armyObj = new JSONObject();
			armyObj.put("name", a.getName());

			if (descType == DescriptionType.LONG) {

				armyObj.put("units", new JSONArray());
				for (Unit u : a.getUnits()) {
					JSONObject unitObj = new JSONObject(this.serialize(u,
							descType));
					armyObj.accumulate("units", unitObj);
				}

				Expression conditions = a.getConditions();

				if (conditions != null) {
					armyObj.put("conditions", conditions.toString());
				}
			}

			return armyObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing Army " + a.getName()
							+ ": " + ex);
		}
	}

	public String serialize(Unit u, DescriptionType descType) {
		try {
			JSONObject unitObj = new JSONObject();
			unitObj.put("name", u.getName());

			if (descType == DescriptionType.LONG) {
				unitObj.put(
						"cost",
						new JSONObject(this.serialize(u.getCostExpression(),
								descType)));

				// for (UnitSlot slot : u.getSlots()) {
				// unitObj.accumulate("slots", slot.toString());
				// }

				unitObj.put(
						"slots",
						new JSONObject(this.serialize(u.getSlotsPattern(),
								descType)));

				unitObj.put(
						"composition",
						new JSONObject(this.serialize(
								u.getCompositionPattern(), descType)));
				unitObj.put(
						"linked",
						new JSONObject(this.serialize(
								u.getLinkedUnitsPattern(), descType)));
				unitObj.put(
						"upgrades",
						new JSONObject(this.serialize(u.getUpgradesPattern(),
								descType)));

				unitObj.put("members", new JSONArray());
				for (UnitMember um : u.getMembers()) {
					JSONObject umObj = new JSONObject(this.serialize(um,
							descType));
					unitObj.accumulate("members", umObj);
				}

				unitObj.put("modified", new JSONArray());
				for (Unit un : u.getModifiedUnits()) {
					JSONObject uObj = new JSONObject(this.serialize(un,
							descType));
					unitObj.accumulate("modified", uObj);
				}

				unitObj.put("conditions", u.getConditions().toString());
			}
			return unitObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing Unit " + u.getName()
							+ ": " + ex);
		}
	}

	public String serialize(UnitMember um, DescriptionType descType) {
		try {
			JSONObject memberObj = new JSONObject();
			memberObj.put("name", um.getName());
			if (descType == DescriptionType.LONG) {
				memberObj.put(
						"equipment",
						new JSONObject(this.serialize(um.getEquipmentPattern(),
								descType)));
				memberObj.put("conditions", um.getConditions().toString());
			}
			return memberObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing UnitMember "
							+ um.getName() + ": " + ex);
		}
	}

	public String serialize(ArmyInstance ai, DescriptionType descType) {
		try {
			JSONObject armyObj = new JSONObject();
			armyObj.put("name", ai.getName());

			armyObj.put("type", ai.getTypeName());
			armyObj.put("cost", ai.getFinalCost());

			if (descType == DescriptionType.LONG) {

				armyObj.put("units", new JSONArray());

				for (UnitInstance ui : ai.getUnits()) {
					JSONObject unitObj = new JSONObject(this.serialize(ui,
							descType));
					armyObj.accumulate("units", unitObj);
				}
			}
			return armyObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing ArmyInstance "
							+ ai.getName() + ": " + ex);
		}
	}

	public String serialize(UnitInstance ui, DescriptionType descType) {
		try {
			JSONObject unitObj = new JSONObject();
			unitObj.put("name", ui.getName());
			unitObj.put("type", ui.getTypeName());
			unitObj.put("cost", ui.getFinalCost());
			unitObj.put("slots", ui.getSlotPattern().toMap());
			if (descType == DescriptionType.LONG) {
				unitObj.put("composition", ui.getCompositionPattern().toMap());
				unitObj.put("linked", ui.getLinkedUnitsPattern().toMap());
				unitObj.put("upgrades", ui.getUpgradesPattern().toMap());

				unitObj.put("members", new JSONArray());

				for (UnitMemberInstance umi : ui.getMembers()) {
					JSONObject umiObj = new JSONObject(this.serialize(umi,
							descType));
					unitObj.accumulate("members", umiObj);
				}
			}

			return unitObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing UnitInstance "
							+ ui.getName() + ": " + ex);
		}
	}

	public String serialize(UnitMemberInstance umi, DescriptionType descType) {
		try {
			JSONObject memberObj = new JSONObject();
			memberObj.put("name", umi.getName());
			if (descType == DescriptionType.LONG) {
				memberObj.put("type", umi.getTypeName());
				memberObj.put("cost", umi.getFinalCost());
				memberObj
						.put("equipment",
								new JSONArray(this.serialize(
										(Collection<?>) umi.getEquipments(),
										descType)));
			}
			return memberObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing UnitMemberInstance "
							+ umi.getName() + ": " + ex);
		}
	}

	public String serialize(Pattern p, DescriptionType descType) {
		if (p instanceof ConjunctEntityPatternSet) {
			return this.serialize((ConjunctEntityPatternSet) p, descType);
		} else if (p instanceof AlternativePattern) {
			return this.serialize((AlternativePattern) p, descType);
		} else if (p instanceof OptionalPattern) {
			return this.serialize((OptionalPattern) p, descType);
		} else if (p instanceof SinglePattern) {
			return this.serialize((SinglePattern) p, descType);
		} else if (p instanceof EmptyPattern) {
			return this.serialize((EmptyPattern) p, descType);
		}

		throw new WhadlRuntimeException("Don't know how to serialize pattern: "
				+ p + " (type: " + p.getClass() + ")");
	}

	public String serialize(ConjunctEntityPatternSet p, DescriptionType descType) {
		try {
			JSONObject setObj = new JSONObject();
			setObj.put("type", "conjunct");

			setObj.put("elements", new JSONArray());

			for (Pattern pat : p) {
				setObj.accumulate("elements",
						new JSONObject(this.serialize(pat, descType)));
			}

			return setObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing ConjunctivePatternSet "
							+ p + ": " + ex);
		}
	}

	public String serialize(AlternativePattern p, DescriptionType descType) {
		try {
			JSONObject setObj = new JSONObject();
			setObj.put("type", "alt");

			setObj.put("elements", new JSONArray());

			for (Pattern pat : p) {
				setObj.accumulate("elements",
						new JSONObject(this.serialize(pat, descType)));
			}

			return setObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing AlternativePattern "
							+ p + ": " + ex);
		}
	}

	public String serialize(OptionalPattern p, DescriptionType descType) {
		try {
			JSONObject setObj = new JSONObject();
			setObj.put("type", "opt");

			setObj.put(
					"optional",
					new JSONObject(this.serialize(p.getOptionalPattern(),
							descType)));

			return setObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing OptionalPattern " + p
							+ ": " + ex);
		}
	}

	public String serialize(SinglePattern p, DescriptionType descType) {
		try {
			JSONObject setObj = new JSONObject();
			setObj.put("type", "single");
			setObj.put("name", p.getName());
			setObj.put("quantity", p.getQuantity());
			setObj.put(
					"cost",
					new JSONObject(this.serialize(p.getCostExpression(),
							descType)));

			return setObj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing SinglePattern " + p
							+ ": " + ex);
		}
	}

	public String serialize(EmptyPattern p, DescriptionType descType) {
		return this.serialize(new SinglePattern("NONE", 0), descType);
	}

	public String serialize(WhadlOperationResult e, DescriptionType descType) {

		try {
			JSONObject obj = new JSONObject();
			obj.put("success", e.isSuccessful());
			obj.put("msg", e.getMessage());
			return obj.toString(indent);
		} catch (JSONException ex) {
			throw new WhadlRuntimeException(
					"An error occured while serializing WhadlOperationResult "
							+ e + ": " + ex);
		}

	}

}
