/* 
 * JSweet transpiler - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.transpiler.candies;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A candy descriptor for the candies store.
 * 
 * @see CandiesStore
 * 
 * @author Louis Grignon
 */
class CandyDescriptor {
	final String name;
	final String version;
	final long lastUpdateTimestamp;
	final String modelVersion;
	final String transpilerVersion;
	final String jsOutputDirPath;
	final String jsDirPath;
	final List<String> jsFilesPaths;

	public CandyDescriptor( //
			String name, //
			String version, //
			long lastUpdateTimestamp, //
			String modelVersion, //
			String transpilerVersion, //
			String jsOutputDirPath, //
			String jsDirPath, //
			List<String> jsFilesPaths) {
		this.name = name;
		this.version = version;
		this.lastUpdateTimestamp = lastUpdateTimestamp;
		this.modelVersion = modelVersion;
		this.transpilerVersion = transpilerVersion;
		this.jsOutputDirPath = jsOutputDirPath;
		this.jsDirPath = jsDirPath;
		this.jsFilesPaths = jsFilesPaths;
	}

	public boolean hasJsFiles() {
		return jsFilesPaths.size() > 0;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CandyDescriptor)) {
			return false;
		}

		CandyDescriptor other = (CandyDescriptor) obj;
		return name.equals(other.name) //
				&& version.equals(other.version) //
				&& lastUpdateTimestamp == other.lastUpdateTimestamp //
				&& StringUtils.equals(jsOutputDirPath, other.jsOutputDirPath);
	}

	private final static Pattern MODEL_VERSION_PATTERN = Pattern.compile("[\\<]groupId[\\>]org[.]jsweet[.]candies[.](.*)[\\<]/groupId[\\>]");
	private final static Pattern ARTIFACT_ID_PATTERN = Pattern.compile("[\\<]artifactId[\\>](.*)[\\<]/artifactId[\\>]");
	private final static Pattern VERSION_PATTERN = Pattern.compile("[\\<]version[\\>](.*)[\\<]/version[\\>]");

	private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static CandyDescriptor fromCandyJar(JarFile jarFile, String jsOutputDirPath) throws IOException {
		JarEntry pomEntry = null;
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry current = entries.nextElement();
			if (current.getName().endsWith("pom.xml")) {
				pomEntry = current;
			}
		}

		String pomContent = IOUtils.toString(jarFile.getInputStream(pomEntry));

		// take only general part
		int dependenciesIndex = pomContent.indexOf("<dependencies>");
		String pomGeneralPart = dependenciesIndex > 0 ? pomContent.substring(0, dependenciesIndex) : pomContent;

		// extract candy model version from <groupId></groupId>
		Matcher matcher = MODEL_VERSION_PATTERN.matcher(pomGeneralPart);
		String modelVersion = "unknown";
		if (matcher.find()) {
			modelVersion = matcher.group(1);
		}

		// extract name from <artifactId></artifactId>
		matcher = ARTIFACT_ID_PATTERN.matcher(pomGeneralPart);
		String name = "unknown";
		if (matcher.find()) {
			name = matcher.group(1);
		}

		matcher = VERSION_PATTERN.matcher(pomGeneralPart);
		String version = "unknown";
		if (matcher.find()) {
			version = matcher.group(1);
		}

		long lastUpdateTimestamp = jarFile.getEntry("META-INF/MANIFEST.MF").getTime();

		String transpilerVersion = null;

		ZipEntry metadataEntry = jarFile.getEntry("META-INF/candy-metadata.json");
		if (metadataEntry != null) {
			String metadataContent = IOUtils.toString(jarFile.getInputStream(metadataEntry));

			@SuppressWarnings("unchecked")
			Map<String, ?> metadata = gson.fromJson(metadataContent, Map.class);

			transpilerVersion = (String) metadata.get("transpilerVersion");
		}

		String jsDirPath = "META-INF/resources/webjars/" + name + "/" + version;
		ZipEntry jsDirEntry = jarFile.getEntry(jsDirPath);
		List<String> jsFilesPaths = new LinkedList<>();
		if (jsDirEntry != null) {
			// collects js files
			jarFile.stream() //
					.filter(entry -> entry.getName().startsWith(jsDirPath) && entry.getName().endsWith(".js")) //
					.map(entry -> entry.getName()) //
					.forEach(jsFilesPaths::add);
		}

		return new CandyDescriptor( //
				name, //
				version, //
				lastUpdateTimestamp, //
				modelVersion, //
				transpilerVersion, //
				jsOutputDirPath, //
				jsDirPath, //
				jsFilesPaths);
	}

	@Override
	public String toString() {
		return "(" + name + "-" + version + ",t=" + lastUpdateTimestamp + ")";
	}
}