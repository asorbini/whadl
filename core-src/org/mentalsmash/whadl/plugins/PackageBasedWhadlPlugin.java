package org.mentalsmash.whadl.plugins;


import org.mentalsmash.whadl.WhadlException;

public class PackageBasedWhadlPlugin extends CodeSourceBasedWhadlPlugin {

	public static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(PackageBasedWhadlPlugin.class);

	public static String getObjectPackageRelPath(Object obj) {
		Class<?> pluginClass = obj.getClass();
		Package pluginPkg = pluginClass.getPackage();
		String pkgName = pluginPkg.getName();
		String relPath = pkgName.replace('.', '/');
		return relPath;
	}

	private final String pkgRelPath;

	public PackageBasedWhadlPlugin() throws WhadlException {
		super();

		this.pkgRelPath = PackageBasedWhadlPlugin.getObjectPackageRelPath(this);

		try {
			this.loadResources(pkgRelPath);
		} catch (Exception ex) {
			log.error("Cannot load resources from plugin's jar file", ex);
		}
	
	}

}
