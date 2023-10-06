package ts.client.configure;

import ts.client.format.FormatCodeSettings;

/**
 * Information found in a configure request.
 */
public class ConfigureRequestArguments {

	/**
	 * Information about the host, for example 'Emacs 24.4' or 'Sublime Text
	 * version 3075'
	 */
	private String hostInfo;

	/**
	 * If present, tab settings apply only to this file.
	 */
	private String file;

	/**
	 * The format options to use during formatting and other code editing
	 * features.
	 */
	private FormatCodeSettings formatOptions;

	/**
	 * The host's additional supported file extensions
	 */
	// extraFileExtensions?: FileExtensionInfo[];

	public String getHostInfo() {
		return hostInfo;
	}

	public ConfigureRequestArguments setHostInfo(String hostInfo) {
		this.hostInfo = hostInfo;
		return this;
	}

	public String getFile() {
		return file;
	}

	public ConfigureRequestArguments setFile(String file) {
		this.file = file;
		return this;
	}

	public FormatCodeSettings getFormatOptions() {
		return formatOptions;
	}

	public ConfigureRequestArguments setFormatOptions(FormatCodeSettings formatOptions) {
		this.formatOptions = formatOptions;
		return this;
	}

}
