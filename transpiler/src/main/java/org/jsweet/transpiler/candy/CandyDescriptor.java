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
package org.jsweet.transpiler.candy;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * A candy descriptor for the candies store.
 *
 * @see CandyStore
 *
 * @author Louis Grignon
 * @author Johann Sorel
 */
public class CandyDescriptor {

    public static final String UNKNOWN = "unknown";
    private final static Pattern MODEL_VERSION_PATTERN = Pattern
            .compile("[\\<]groupId[\\>]org[.]jsweet[.]candies[.](.*)[\\<]/groupId[\\>]");
    private final static Gson GSON = new Gson(); //todo : is this class really thread safe ?

    public final String name;
    public final String version;
    public final long lastUpdateTimestamp;
    public final String modelVersion;
    public final String transpilerVersion;
    public final String jsOutputDirPath;
    public final String jsDirPath;
    public final List<String> jsFilesPaths;

    public CandyDescriptor(String name, String version, long lastUpdateTimestamp,
            String modelVersion, String transpilerVersion, String jsOutputDirPath, 
            String jsDirPath, List<String> jsFilesPaths) {
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

    public static CandyDescriptor fromCandyJar(JarFile jarFile, String jsOutputDirPath) throws IOException {
        
        final Optional<JarEntry> pomXmlEntry = jarFile.stream().filter((JarEntry t) -> t.getName().endsWith("pom.xml")).findFirst();
        final Optional<JarEntry> pomPropertiesEntry = jarFile.stream().filter((JarEntry t) -> t.getName().endsWith("pom.properties")).findFirst();
        final ZipEntry metadataEntry = jarFile.getEntry("META-INF/candy-metadata.json");
        final long lastUpdateTimestamp = jarFile.getEntry("META-INF/MANIFEST.MF").getTime();

        
        String modelVersion = UNKNOWN;
        String name = FilenameUtils.getBaseName(jarFile.getName());
        String version = UNKNOWN;
        String transpilerVersion = null;

        if (pomPropertiesEntry.isPresent()) {
            final Properties props = new Properties();
            try (InputStream in = jarFile.getInputStream(pomPropertiesEntry.get())) {
                props.load(in);
            }
            name = props.getProperty("artifactId");
            version = props.getProperty("version");
        }
        
        if (pomXmlEntry.isPresent()) {
            final String pomContent;
            try (InputStream in = jarFile.getInputStream(pomXmlEntry.get())) {
                pomContent = IOUtils.toString(in);
            }
            
            // take only general part
            final int dependenciesIndex = pomContent.indexOf("<dependencies>");
            final String pomGeneralPart = dependenciesIndex > 0 ? pomContent.substring(0, dependenciesIndex) : pomContent;

            // extract candy model version from <groupId></groupId>
            final Matcher matcher = MODEL_VERSION_PATTERN.matcher(pomGeneralPart);
            if (matcher.find()) {
                modelVersion = matcher.group(1);
            }
        }

        if (metadataEntry != null) {
            final String metadataContent = IOUtils.toString(jarFile.getInputStream(metadataEntry));
            @SuppressWarnings("unchecked")
            final Map<String, ?> metadata = GSON.fromJson(metadataContent, Map.class);
            transpilerVersion = (String) metadata.get("transpilerVersion");
        }

        final String jsDirPath = "META-INF/resources/webjars/" + (UNKNOWN.equals(version) ? "" : name + "/" + version);
        System.out.println("SEARCHING JS IN : "+jsDirPath);
        final ZipEntry jsDirEntry = jarFile.getEntry(jsDirPath);
        final List<String> jsFilesPaths = new LinkedList<>();
        if (jsDirEntry != null) {
            // collects js files
            jarFile.stream().map(JarEntry::getName)
                    .filter((String t) -> t.startsWith(jsDirPath) && t.endsWith(".js"))
                    .forEach(jsFilesPaths::add);
        }

        return new CandyDescriptor(name,version,lastUpdateTimestamp,modelVersion,
                transpilerVersion,jsOutputDirPath,jsDirPath,jsFilesPaths);
    }

    @Override
    public String toString() {
        return "(" + name + "-" + version + ",t=" + lastUpdateTimestamp + ")";
    }
}
