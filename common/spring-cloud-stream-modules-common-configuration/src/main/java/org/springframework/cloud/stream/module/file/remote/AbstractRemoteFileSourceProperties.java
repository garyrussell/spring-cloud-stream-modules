/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.stream.module.file.remote;

import java.io.File;
import java.util.regex.Pattern;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Common properties for remote file sources (e.g. (S)FTP).
 *
 * @author David Turanski
 * @author Gary Russell
 *
 */
public abstract class AbstractRemoteFileSourceProperties {

	/**
	 * The remote FTP directory.
	 */
	private String remoteDir = "/";

	/**
	 * Set to true to delete remote files after successful transfer.
	 */
	private boolean deleteRemoteFiles = false;

	/**
	 * The local directory to use for file transfers.
	 */
	private File localDir = new File(System.getProperty("java.io.tmpdir") + "/xd/ftp");

	/**
	 * Set to true to create the local directory if it does not exist.
	 */
	private boolean autoCreateLocalDir = true;

	/**
	 * The suffix to use while the transfer is in progress.
	 */
	private String tmpFileSuffix = ".tmp";

	/**
	 * A filter pattern to match the names of files to transfer.
	 */
	private String filenamePattern;

	/**
	 * A filter regex pattern to match the names of files to transfer.
	 */
	private Pattern filenameRegex;

	/**
	 * The remote file separator.
	 */
	private String remoteFileSeparator = "/";

	/**
	 * Set to true to preserve the original timestamp.
	 */
	private boolean preserveTimestamp = true;

	public boolean isAutoCreateLocalDir() {
		return autoCreateLocalDir;
	}

	public void setAutoCreateLocalDir(boolean autoCreateLocalDir) {
		this.autoCreateLocalDir = autoCreateLocalDir;
	}

	@NotBlank
	public String getRemoteDir() {
		return remoteDir;
	}

	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	public boolean isDeleteRemoteFiles() {
		return deleteRemoteFiles;
	}

	public void setDeleteRemoteFiles(boolean deleteRemoteFiles) {
		this.deleteRemoteFiles = deleteRemoteFiles;
	}

	@NotNull
	public File getLocalDir() {
		return localDir;
	}

	public void setLocalDir(File localDir) {
		this.localDir = localDir;
	}

	@NotBlank
	public String getTmpFileSuffix() {
		return tmpFileSuffix;
	}

	public void setTmpFileSuffix(String tmpFileSuffix) {
		this.tmpFileSuffix = tmpFileSuffix;
	}

	public String getFilenamePattern() {
		return filenamePattern;
	}

	public void setFilenamePattern(String filenamePattern) {
		this.filenamePattern = filenamePattern;
	}

	public Pattern getFilenameRegex() {
		return filenameRegex;
	}

	public void setFilenameRegex(Pattern filenameRegex) {
		this.filenameRegex = filenameRegex;
	}

	@NotBlank
	public String getRemoteFileSeparator() {
		return remoteFileSeparator;
	}

	public void setRemoteFileSeparator(String remoteFileSeparator) {
		this.remoteFileSeparator = remoteFileSeparator;
	}

	public boolean isPreserveTimestamp() {
		return preserveTimestamp;
	}

	public void setPreserveTimestamp(boolean preserveTimestamp) {
		this.preserveTimestamp = preserveTimestamp;
	}

	@AssertTrue(message = "filenamePattern and filenameRegex are mutually exclusive")
	public boolean exclusivePatterns() {
		return !(this.filenamePattern != null && this.filenameRegex != null);
	}

}
