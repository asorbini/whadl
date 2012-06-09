package org.mentalsmash.whadl.descriptions;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Collection;

import org.mentalsmash.whadl.WhadlRuntimeException;
import org.mentalsmash.whadl.descriptions.parser.DDLParser;
import org.mentalsmash.whadl.descriptions.parser.visitor.ast.DescriptionsASTGenerator;
import org.mentalsmash.whadl.model.Army;
import org.mentalsmash.whadl.model.ArmyInstance;
import org.mentalsmash.whadl.model.Entity;
import org.mentalsmash.whadl.model.Unit;
import org.mentalsmash.whadl.model.UnitInstance;
import org.mentalsmash.whadl.model.UnitMember;
import org.mentalsmash.whadl.model.UnitMemberInstance;
import org.mentalsmash.whadl.model.expressions.BinaryExpression;
import org.mentalsmash.whadl.model.expressions.ConditionalExpression;
import org.mentalsmash.whadl.model.expressions.Expression;
import org.mentalsmash.whadl.model.expressions.ParenthesesExpression;
import org.mentalsmash.whadl.model.expressions.UnaryExpression;

public class LocalDescriptionsManager implements DescriptionsManager {

	private final File repository;

	private final String suffix = ".locale";
	private final String context;

	public LocalDescriptionsManager(String context, File repository) {
		this.repository = repository;
		this.context = context;
	}

	@Override
	public void loadDescriptions(Entity e) {
		try {
			String path = e.getReference().replace('.', File.separatorChar)
					.split(context)[1];
			File dir = new File(repository.getName() + File.separator + path);

			if (e instanceof Army) {
				this.loadDescriptions(dir, (Army) e);
			} else if (e instanceof ArmyInstance) {
				this.loadDescriptions(dir, (ArmyInstance) e);
			} else if (e instanceof Unit) {
				this.loadDescriptions(dir, (Unit) e);
			} else if (e instanceof UnitInstance) {
				this.loadDescriptions(dir, (UnitInstance) e);
			} else if (e instanceof UnitMember) {
				this.loadDescriptions(dir, (UnitMember) e);
			} else if (e instanceof UnitMemberInstance) {
				this.loadDescriptions(dir, (UnitMemberInstance) e);
			}

		} catch (Exception ex) {
			throw new WhadlRuntimeException(
					"Error while loading descriptions: entity="
							+ e.getReference(), ex);
		}

	}

	private String getLocale(File f) {
		return f.getName().substring(0, f.getName().indexOf(suffix));
	}

	private Collection<Description> parse(File f) {
		try {
			FileInputStream in = new FileInputStream(f);
			DDLParser p = new DDLParser(in);
			DescriptionsASTGenerator g = new DescriptionsASTGenerator();
			Collection<Description> descriptions = g.visit(p.Descriptions());
			return descriptions;
		} catch (Exception ex) {
			throw new WhadlRuntimeException(
					"Error while parsing descriptions file: file="
							+ f.getAbsolutePath(), ex);
		}
	}

	private boolean setExpressionDescription(Expression e, String locale,
			LabeledEntityDescription d) {

		if (e.getLabel().equals(d.getLabel())) {
			e.setDescription(locale, d.getText());
			return true;
		} else if (e instanceof ConditionalExpression) {
			ConditionalExpression condExp = (ConditionalExpression) e;
			if (this
					.setExpressionDescription(condExp.getCondition(), locale, d)) {
				return true;
			} else if (this.setExpressionDescription(condExp.getFirstTerm(),
					locale, d)) {
				return true;
			} else {
				return this.setExpressionDescription(condExp.getCondition(),
						locale, d);
			}
		} else if (e instanceof BinaryExpression) {
			BinaryExpression binExp = (BinaryExpression) e;
			if (this.setExpressionDescription(binExp.getFirstTerm(), locale, d)) {
				return true;
			} else {
				return this.setExpressionDescription(binExp.getFirstTerm(),
						locale, d);
			}
		} else if (e instanceof UnaryExpression) {
			UnaryExpression uexp = (UnaryExpression) e;
			return this.setExpressionDescription(uexp.getTerm(), locale, d);
		} else if (e instanceof ParenthesesExpression) {
			return this.setExpressionDescription(((ParenthesesExpression) e)
					.getInner(), locale, d);
		}

		return false;

	}

	protected void loadDescriptions(File dir, Army a) {
		for (File localeFile : dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(suffix);
			}
		})) {
			String locale = this.getLocale(localeFile);
			Collection<Description> descs = this.parse(localeFile);
			for (Description d : descs) {
				System.out.println("DESC: "+d);
				String id = d.getId();
				String aref = a.getReference();
				if (id.equals(aref)) {
					a.setDescription(locale, d.getText());
				} else if (id.equals(aref+".conditions")) {
					a.getConditions().setDescription(locale, d.getText());
				}
			}

		}

		for (File subdir : dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		})) {

			String unitName = subdir.getName();
			Unit u = a.getUnit(unitName);
			this.loadDescriptions(subdir, u);

		}
	}

	protected void loadDescriptions(File dir, ArmyInstance ai) {

	}

	protected void loadDescriptions(File dir, Unit u) {
		for (File localeFile : dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(suffix);
			}
		})) {
			String locale = this.getLocale(localeFile);
			Collection<Description> descs = this.parse(localeFile);
			for (Description d : descs) {
				String id = d.getId();
				String uref = u.getReference();
				if (id.equals(uref)) {
					u.setDescription(locale, d.getText());
				} else if (d instanceof LabeledEntityDescription) {
					this.setExpressionDescription(u.getConditions(), locale,
							(LabeledEntityDescription) d);
				}
			}

		}

		for (File subdir : dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		})) {

			String memberName = subdir.getName();
			UnitMember um = u.getMember(memberName);
			this.loadDescriptions(subdir, um);

		}
	}

	protected void loadDescriptions(File dir, UnitInstance ui) {

	}

	protected void loadDescriptions(File dir, UnitMember um) {
		for (File localeFile : dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(suffix);
			}
		})) {
			String locale = this.getLocale(localeFile);
			Collection<Description> descs = this.parse(localeFile);
			for (Description d : descs) {
				String id = d.getId();
				String umref = um.getReference();
				if (id.equals(umref)) {
					um.setDescription(locale, d.getText());
				} else if (d instanceof LabeledEntityDescription) {
					this.setExpressionDescription(um.getConditions(), locale,
							(LabeledEntityDescription) d);
				}
			}

		}
	}

	protected void loadDescriptions(File dir, UnitMemberInstance umi) {

	}
}
