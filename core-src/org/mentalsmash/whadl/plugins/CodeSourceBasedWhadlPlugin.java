package org.mentalsmash.whadl.plugins;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.mentalsmash.whadl.Whadl;
import org.mentalsmash.whadl.WhadlException;
import org.mentalsmash.whadl.model.Army;

public class CodeSourceBasedWhadlPlugin implements WhadlPlugin {
	public static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CodeSourceBasedWhadlPlugin.class);

	public static String getJarFilePath(Class<?> pluginClass) {
		String path = null;
		String decodedPath = null;
		URL jarLocation = pluginClass.getProtectionDomain().getCodeSource()
				.getLocation();
		try {
			path = jarLocation.toURI().getPath();
			decodedPath = URLDecoder.decode(path, "UTF-8");
		} catch (URISyntaxException e) {
			log.error("Cannot convert source code location " + jarLocation
					+ " to URI", e);
		} catch (UnsupportedEncodingException e) {
			log.error("Cannot decode URI using UTF-8 encoding", e);
		}

		return decodedPath;
	}

	protected String pathSeparator = "/";
	protected String codeSourceLocation;
	protected final Map<String, String> armyDefinitionFiles = new HashMap<String, String>();
	protected final Map<String, String> armyBuildFiles = new HashMap<String, String>();

	public CodeSourceBasedWhadlPlugin() {

	}

	public CodeSourceBasedWhadlPlugin(String pathSeparator) {
		this.pathSeparator = pathSeparator;
	}

	protected void loadResources(String relPath) throws FileNotFoundException,
			IOException, WhadlException {
		Class<?> pluginClass = this.getClass();
		this.codeSourceLocation = CodeSourceBasedWhadlPlugin
				.getJarFilePath(pluginClass);
		if (this.codeSourceLocation == null) {
			throw new FileNotFoundException(
					"Cannot locate code source location for class "
							+ pluginClass);
		}

		log.info("CodeSource location at: {}", codeSourceLocation);

		if (this.codeSourceLocation.endsWith(".jar")) {
			this.loadWhadlFilesFromJar(relPath);
		} else {
			this.loadWhadlFilesFromFileSystem(relPath);
		}
	}

	private void loadWhadlFilesFromJar(String relPath) throws IOException {
		JarFile jarFile = new JarFile(codeSourceLocation);
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			if (entryName.startsWith(relPath)
					&& entryName.length() > (relPath.length() + "/".length())) {
				// String fileName = entryName.replace(relPath,
				// "").substring(1);
				String fileName = entryName;

				if (entryName.endsWith(Whadl.FILE_EXTENSION_ARMY_DEF)) {
					log.info("Discovered Army definition file: {} [entry={}]",
							new Object[] { fileName, entryName });
					this.armyDefinitionFiles.put(fileName, entryName);
				} else if (entryName.endsWith(Whadl.FILE_EXTENSION_ARMY_BUILD)) {
					log.info("Discovered Army build file: {} [entry={}]",
							new Object[] { fileName, entryName });
					this.armyBuildFiles.put(fileName, entryName);
				}

			}
		}
	}

	private void findFilesRecursively(final File baseDir, final String prefix,
			final String suffix, final List<File> output) {
		if (baseDir != null && baseDir.exists() && baseDir.isDirectory()) {
			// log.debug("Looking for whadl data in directory: "
			// + baseDir.getAbsolutePath());
			// log.debug("Prefix: {}; Suffix: {};",
			// new Object[] { prefix, suffix });

			if (!baseDir.getAbsolutePath().endsWith(prefix)) {
				// log.debug("Directory doesn't include prefix");
			} else {

				File[] containedFiles = baseDir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File f) {
						String fpath = f.getAbsolutePath()
								.replace(baseDir.getAbsolutePath(), "")
								.substring(1);
						// log.debug("Checking file: {}", fpath);
						return fpath.endsWith(suffix);
					}
				});

				for (File file : containedFiles) {
					log.info("Found file [prefix={}, suffix={}]: {}",
							new Object[] { prefix, suffix, file });
					output.add(file);
				}
			}

			File[] subDirs = baseDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory();
				}
			});

			for (File subDir : subDirs) {
				this.findFilesRecursively(subDir, prefix, suffix, output);
			}
		}
	}

	private void loadWhadlFilesFromFileSystem(final String relPath)
			throws IOException, WhadlException {
		File dataDir = new File(this.codeSourceLocation);
		if (!dataDir.exists() || !dataDir.isDirectory()) {
			throw new WhadlException("Can't find directory "
					+ this.codeSourceLocation);
		}

		List<File> whadlFiles = new ArrayList<File>();
		this.findFilesRecursively(dataDir, relPath,
				Whadl.FILE_EXTENSION_ARMY_DEF, whadlFiles);
		this.findFilesRecursively(dataDir, relPath,
				Whadl.FILE_EXTENSION_ARMY_BUILD, whadlFiles);

		if (whadlFiles.size() == 0) {
			log.error("No Whadl data files found in " + dataDir);
		}

		for (File dataFile : whadlFiles) {
			String fileFullPath = dataFile.getAbsolutePath();
			String fileName = relPath + this.pathSeparator + dataFile.getName();
			if (fileName.endsWith(Whadl.FILE_EXTENSION_ARMY_DEF)) {
				this.armyDefinitionFiles.put(fileName, fileFullPath);
			} else if (fileName.endsWith(Whadl.FILE_EXTENSION_ARMY_BUILD)) {
				this.armyBuildFiles.put(fileName, fileFullPath);
			}
		}

	}

	@Override
	public Map<String, String> getArmyDefinitionFiles() {
		return this.armyDefinitionFiles;
	}

	@Override
	public Map<String, String> getArmyBuildFiles() {
		return this.armyBuildFiles;
	}

}
