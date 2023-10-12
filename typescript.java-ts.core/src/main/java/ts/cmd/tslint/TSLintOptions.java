/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package ts.cmd.tslint;

import java.io.File;
import java.util.List;

import ts.cmd.AbstractOptions;
import ts.utils.FileUtils;

/**
 * tslint options.
 * 
 * @see http://palantir.github.io/tslint/usage/cli/
 *
 */
public class TSLintOptions extends AbstractOptions {

	private String config;
	private String format;

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void setConfig(File configFile) {
		if (configFile != null) {
			setConfig(FileUtils.getPath(configFile));
		}
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setFormat(TslintFormat format) {
		setFormat(format.name());
	}

	@Override
	public void fillOptions(List<String> args) {
		super.fillOption("--config", getConfig(), args);
		super.fillOption("--format", getFormat(), args);
	}
}
