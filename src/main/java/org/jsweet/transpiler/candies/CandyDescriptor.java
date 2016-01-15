/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.transpiler.candies;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A candy descriptor for the candies store.
 * 
 * @see CandiesStore
 */
class CandyDescriptor {
	String name;
	String version;
	long lastUpdateTimestamp;
	String modelVersion;
	String transpilerVersion;

	public CandyDescriptor(String name, String version, long lastUpdateTimestamp, String modelVersion, String transpilerVersion) {
		this.name = name;
		this.version = version;
		this.lastUpdateTimestamp = lastUpdateTimestamp;
		this.modelVersion = modelVersion;
		this.transpilerVersion = transpilerVersion;
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
				&& lastUpdateTimestamp == other.lastUpdateTimestamp;
	}

	private final static Pattern MODEL_VERSION_PATTERN = Pattern.compile("[\\<]groupId[\\>]org[.]jsweet[.]candies[.](.*)[\\<]/groupId[\\>]");
	private final static Pattern ARTIFACT_ID_PATTERN = Pattern.compile("[\\<]artifactId[\\>](.*)[\\<]/artifactId[\\>]");
	private final static Pattern VERSION_PATTERN = Pattern.compile("[\\<]version[\\>](.*)[\\<]/version[\\>]");

	private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static CandyDescriptor fromCandyJar(JarFile jarFile) throws IOException {
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
		String pomGeneralPart = pomContent.substring(0, pomContent.indexOf("<dependencies>"));

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

		return new CandyDescriptor(name, version, lastUpdateTimestamp, modelVersion, transpilerVersion);
	}

	@Override
	public String toString() {
		return "(" + name + "-" + version + ",t=" + lastUpdateTimestamp + ")";
	}
}