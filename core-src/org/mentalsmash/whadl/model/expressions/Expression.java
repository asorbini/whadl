package org.mentalsmash.whadl.model.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.model.CollectionEntity;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.ObjectWrapperEntity;
import org.mentalsmash.whadl.validation.EntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Expression {

	public static final Expression DEFAULT_EXPRESSION = new LiteralExpression(0);

	public String getLabel();
	
	public void setLabel(String label);
	
	public Map<String, String> getDescriptions();
	
	public void setDescription(String locale, String description);
	
	public void accept(ExpressionVisitor visitor);

	public Object evaluate(EntityContext context);

	public enum Operator {
		SELECTATTRIBUTE("->") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {

				Class<? extends Object>[] argsTypes = this.getArguments(
						Entity.class, String.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				Entity entity = (Entity) args[0];
				String attribute = (String) args[1];

				Object result = entity.getAttribute(attribute);

				this.logResult(result, args);

				return result;
			}
		},
		CONDITION("?") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {

				Class<? extends Object>[] argsTypes = this.getArguments(
						Boolean.class, Object.class, Object.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				Boolean cond = (Boolean) args[0];
				Object a = args[1];
				Object b = args[2];

				Object result = cond ? a : b;

				this.logResult(result, args);

				return result;
			}
		},
		PLUS("+") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {

				Class<? extends Object>[] argsTypes = this.getArguments(
						Number.class, Number.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Integer> ints = new ArrayList<Integer>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					ints.add((Integer) o);
				}

				Object result = this.eval(ints.toArray(new Integer[0]));

				this.logResult(result, args);

				return result;
			}

			private Integer eval(Integer... args) {
				Integer result = 0;

				for (Integer i : args) {
					result += i;
				}

				return result;
			}

		},
		MINUS("-") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Number.class, Number.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Integer> ints = new ArrayList<Integer>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					ints.add((Integer) o);
				}

				Object result = this.eval(ints.toArray(new Integer[0]));

				this.logResult(result, args);

				return result;
			}

			private Integer eval(Integer... args) {
				Integer result = args[0];

				Integer[] left = new Integer[args.length - 1];
				for (int i = 0; i < left.length; i++) {
					left[i] = args[i + 1];
				}

				for (Integer i : left) {
					result -= i;
				}

				return result;
			}
		},
		MULTIPLY("*") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Number.class, Number.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Integer> ints = new ArrayList<Integer>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					ints.add((Integer) o);
				}

				Object result = this.eval(ints.toArray(new Integer[0]));

				this.logResult(result, args);

				return result;
			}

			private Integer eval(Integer... args) {
				Integer result = 0;

				for (Integer i : args) {

					result *= i;

				}

				return result;
			}
		},
		DIVISION("/") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Number.class, Number.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Integer> ints = new ArrayList<Integer>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					ints.add((Integer) o);
				}

				Object result = this.eval(ints.toArray(new Integer[0]));

				this.logResult(result, args);

				return result;
			}

			private Integer eval(Integer... args) {
				Integer result = args[0];

				Integer[] left = new Integer[args.length - 1];
				for (int i = 0; i < left.length; i++) {
					left[i] = args[i + 1];
				}

				for (Integer i : left) {
					result /= i;
				}

				// System.out.println("DIVISION: elements: " + args +
				// ", result: "
				// + result);

				return result;
			}
		},
		EQUALS("==") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Object.class, Object.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				Boolean result = true;

				Object curr = args[0];

				for (int i = 1; i < args.length; i++) {
					Object next = args[i];
					next = this.getObjectWrapperEntityInnerObject(next);
					result = result && (curr.equals(next));
					if (!result)
						return false;
					curr = next;
				}

				return result;
			}
		},
		NOTEQUALS("!=") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Object.class, Object.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				Boolean result = true;

				Object curr = args[0];

				for (int i = 1; i < args.length; i++) {
					Object next = args[i];
					next = this.getObjectWrapperEntityInnerObject(next);
					result = result && !(curr.equals(next));
					if (!result)
						return false;
					curr = next;
				}

				this.logResult(result, args);

				return result;
			}
		},
		LESS("<") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Number.class, Number.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Integer> comparables = new ArrayList<Integer>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);

					comparables.add((Integer) o);
				}

				Boolean result = true;

				Iterator<Integer> it = comparables.iterator();

				Integer curr = it.next();

				while (it.hasNext()) {
					Integer next = it.next();
					result = result && (curr.compareTo(next) < 0);
					if (!result)
						return false;
					curr = next;
				}

				this.logResult(result, args);

				return result;
			}
		},
		LESSEQ("<=") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Number.class, Number.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Integer> comparables = new ArrayList<Integer>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					comparables.add((Integer) o);
				}

				Boolean result = true;

				Iterator<Integer> it = comparables.iterator();

				Integer curr = it.next();

				while (it.hasNext()) {
					Integer next = it.next();
					result = result
							&& (((Integer) curr).compareTo((Integer) next) <= 0);
					if (!result)
						return false;
					curr = next;
				}

				this.logResult(result, args);

				return result;
			}
		},
		MORE(">") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Integer.class, Integer.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Integer> comparables = new ArrayList<Integer>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					comparables.add((Integer) o);
				}

				Boolean result = true;

				Iterator<Integer> it = comparables.iterator();

				Integer curr = it.next();

				while (it.hasNext()) {
					Integer next = it.next();
					result = result
							&& (((Integer) curr).compareTo((Integer) next) > 0);
					if (!result)
						return false;
					curr = next;
				}

				this.logResult(result, args);

				return result;
			}
		},
		MOREEQ(">=") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Integer.class, Integer.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Integer> comparables = new ArrayList<Integer>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					comparables.add((Integer) o);
				}

				Boolean result = true;

				Iterator<Integer> it = comparables.iterator();

				Integer curr = it.next();

				while (it.hasNext()) {
					Integer next = it.next();
					result = result
							&& (((Integer) curr).compareTo((Integer) next) >= 0);
					if (!result)
						return false;
					curr = next;
				}

				this.logResult(result, args);

				return result;
			}
		},
		AND("&&") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Boolean.class, Boolean.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Boolean> comparables = new ArrayList<Boolean>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					comparables.add((Boolean) o);
				}

				Boolean result = true;

				Iterator<Boolean> it = comparables.iterator();

				while (it.hasNext()) {
					Boolean curr = it.next();
					if (!curr)
						return false;
					result = result && curr;
				}

				this.logResult(result, args);

				return result;
			}
		},
		OR("||") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						Boolean.class, Boolean.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				ArrayList<Boolean> comparables = new ArrayList<Boolean>();

				for (Object o : args) {
					o = this.getObjectWrapperEntityInnerObject(o);
					comparables.add((Boolean) o);
				}

				Boolean result = false;

				Iterator<Boolean> it = comparables.iterator();

				while (it.hasNext()) {
					Boolean curr = it.next();
					result = result || curr;
				}

				this.logResult(result, args);

				return result;
			}
		},
		NOT("!") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this
						.getArguments(Boolean.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				Object o = args[0];
				o = this.getObjectWrapperEntityInnerObject(o);

				Boolean result = !((Boolean) o);

				this.logResult(result, args);

				return result;
			}
		},
		CONTAINS("contains") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						CollectionEntity.class, CollectionEntity.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				CollectionEntity<Entity> collection = (CollectionEntity<Entity>) args[0];
				CollectionEntity<Entity> contained = (CollectionEntity<Entity>) args[1];

				Boolean result = true;

				for (Entity o : contained) {
					result = result && collection.contains(o);
					if (!result)
						return false;
				}

				this.logResult(result, args);

				return result;
			}
		},
		UNION("union") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						CollectionEntity.class, CollectionEntity.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				CollectionEntity<Entity> collection = (CollectionEntity<Entity>) args[0];
				CollectionEntity<Entity> contained = (CollectionEntity<Entity>) args[1];

				CollectionEntity<Entity> union = new CollectionEntity<Entity>();
				union.addAll(collection);

				for (Entity o : contained) {
					if (!union.contains(o)) {
						union.add(o);
					}
				}

				this.logResult(union, args);

				return union;
			}
		},
		INTERSECT("intersect") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						CollectionEntity.class, CollectionEntity.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				CollectionEntity<Entity> collection = (CollectionEntity<Entity>) args[0];
				CollectionEntity<Entity> contained = (CollectionEntity<Entity>) args[1];

				CollectionEntity<Entity> intersection = new CollectionEntity<Entity>();

				for (Entity o : collection) {

					for (Entity entity : contained) {
						if (o.equals(entity)) {
							intersection.add(o);
							break;
						}
					}
				}

				for (Entity o : contained) {
					if (collection.contains(o) && !intersection.contains(o)) {
						intersection.add(o);
					}
				}

				this.logResult(intersection, args);

				return intersection;
			}
		},
		SELECT("select") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						CollectionEntity.class, Expression.class,
						EntityContext.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				CollectionEntity<?> collection = (CollectionEntity<?>) args[0];
				Expression exp = (Expression) args[1];
				EntityContext context = (EntityContext) args[2];

				CollectionEntity<Entity> selected = new CollectionEntity<Entity>();

				for (Entity e : collection) {

					if (exp.evaluate(context.getSubContext(e)).equals(
							Boolean.TRUE)) {
						selected.add(e);
					}
				}

				this.logResult(selected, args);

				return selected;
			}
		},
		EACH("each") {
			@SuppressWarnings("unchecked")
			public Object eval(Object... args)
					throws WrongArgumentsNumberException,
					IllegalArgumentException {
				Class<? extends Object>[] argsTypes = this.getArguments(
						CollectionEntity.class, Expression.class,
						EntityContext.class);
				this.checkArguments(args, argsTypes.length, argsTypes);

				Collection<Entity> collection = (Collection<Entity>) args[0];
				Expression exp = (Expression) args[1];
				EntityContext context = (EntityContext) args[2];

				boolean result = true;

				for (Entity e : collection) {
					result = result
							&& exp.evaluate(context.getSubContext(e)).equals(
									Boolean.TRUE);
					if (!result) {
						return false;
					}
				}

				this.logResult(result, args);

				return result;
			}
		};

		private final String str;

		Operator(String str) {
			this.str = str;
		}

		public String toString() {
			return this.str;
		}

		public static Operator getByString(String str) {
			for (Operator op : EnumSet.allOf(Operator.class)) {
				if (op.str.equals(str)) {
					return op;
				}
			}

			throw new WhadlRuntimeException(
					"Unknown string for operator enum : " + str);
		}

		protected void checkArguments(Object[] args, int num,
				Class<? extends Object>[] argsTypes)
				throws WrongArgumentsNumberException, IllegalArgumentException {
			if (args.length != num) {
				throw new WrongArgumentsNumberException(this, args.length, num);
			}

			// for (int i = 0; i < args.length; i++) {
			// Object curr = args[i];
			// args[i] = this.checkIfObjectWrapper(curr);
			// System.err.println("RESULT: "+curr.getClass());
			// if (curr instanceof ObjectWrapperEntity) {
			// System.err.println("**** WTF?!!");
			// }
			// }

			for (int i = 0; i < args.length; i++) {
				Class<? extends Object> type = argsTypes[i];
				Object arg = args[i];

				arg = this.getObjectWrapperEntityInnerObject(arg);

				if (!type.isInstance(arg)) {
					throw new IllegalArgumentException(this, arg, type);
				}

			}
		}

		protected Class<? extends Object>[] getArguments(
				Class<? extends Object>... argsTypes) {
			return argsTypes;
		}

		protected static final Logger log = LoggerFactory
				.getLogger(Operator.class);

		protected void logResult(Object result, Object[] args) {
			log.trace("Operation " + this);
			for (Object o : args) {
				log.trace("  OP: " + o);
			}
			log.trace("  RESULT: " + result);
		}

		public abstract Object eval(Object... args)
				throws WrongArgumentsNumberException, IllegalArgumentException;

		@SuppressWarnings("unchecked")
		protected Object getObjectWrapperEntityInnerObject(Object o) {
			if (o instanceof ObjectWrapperEntity) {
				Object obj = ((ObjectWrapperEntity) o).getObject();
				// System.err.println("DEWRAPPING SUBOBJ: " + obj + " (from: " +
				// o
				// + ")");
				obj = getObjectWrapperEntityInnerObject(obj);
				// System.err.println("RESULT: " + obj + " (type: "
				// + obj.getClass() + ")");
				//
				// System.err.println("EQUALS: " + (o.equals(obj)));

				return obj;
			} else if (o instanceof Collection) {
				// Collection coll = new CollectionEntity<Entity>();
				// for (Object obj : (Collection)o) {
				// coll.add(this.checkIfObjectWrapper(obj));
				// }
				//				
				// System.err.println("NEW COLLECTION: "+coll);
				// return coll;
				return o;

			} else {
				// System.err.println("NO OP: "+o+ " (type: "+o.getClass()+")");
				return o;
			}
		}
	}

}
