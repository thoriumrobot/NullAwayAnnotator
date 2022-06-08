/*
 * Copyright (c) 2022 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package edu.ucr.cs.riple.core.util;

import javax.annotation.Nullable;

/** Config class for Fix Serialization package. */
public class FixSerializationConfig {

  public final boolean suggestEnabled;

  public final boolean suggestEnclosing;

  public final boolean fieldInitInfoEnabled;

  public final boolean methodParamProtectionTestEnabled;

  public final int paramTestIndex;

  /** The directory where all files generated/read by Fix Serialization package resides. */
  @Nullable public final String outputDirectory;

  public final AnnotationConfig annotationConfig;

  /** Default Constructor, all features are disabled with this config. */
  public FixSerializationConfig() {
    suggestEnabled = false;
    suggestEnclosing = false;
    fieldInitInfoEnabled = false;
    methodParamProtectionTestEnabled = false;
    paramTestIndex = Integer.MAX_VALUE;
    annotationConfig = new AnnotationConfig();
    outputDirectory = null;
  }

  public FixSerializationConfig(
      boolean suggestEnabled,
      boolean suggestEnclosing,
      boolean fieldInitInfoEnabled,
      boolean methodParamProtectionTestEnabled,
      int paramTestIndex,
      AnnotationConfig annotationConfig,
      String outputDirectory) {
    this.suggestEnabled = suggestEnabled;
    this.suggestEnclosing = suggestEnclosing;
    this.fieldInitInfoEnabled = fieldInitInfoEnabled;
    this.methodParamProtectionTestEnabled = methodParamProtectionTestEnabled;
    this.paramTestIndex = paramTestIndex;
    this.outputDirectory = outputDirectory;
    this.annotationConfig = annotationConfig;
  }

  /** Builder class for Serialization Config */
  public static class Builder {

    private boolean suggestEnabled;
    private boolean suggestEnclosing;
    private boolean fieldInitInfo;
    private boolean methodParamProtectionTestEnabled;
    private int paramIndex;
    private String nullable;
    private String nonnull;
    @Nullable private String outputDir;

    public Builder() {
      suggestEnabled = false;
      suggestEnclosing = false;
      fieldInitInfo = false;
      nullable = "javax.annotation.Nullable";
      nonnull = "javax.annotation.Nonnull";
    }

    public Builder setSuggest(boolean value, boolean withEnclosing) {
      this.suggestEnabled = value;
      this.suggestEnclosing = withEnclosing && suggestEnabled;
      return this;
    }

    public Builder setAnnotations(String nullable, String nonnull) {
      this.nullable = nullable;
      this.nonnull = nonnull;
      return this;
    }

    public Builder setFieldInitInfo(boolean enabled) {
      this.fieldInitInfo = enabled;
      return this;
    }

    public Builder setOutputDirectory(String outputDir) {
      this.outputDir = outputDir;
      return this;
    }

    public Builder setParamProtectionTest(boolean value, int index) {
      this.methodParamProtectionTestEnabled = value;
      this.paramIndex = index;
      return this;
    }

    /**
     * Builds and writes the config with the state in builder at the given path as XML.
     *
     * @param path path to write the config file.
     */
    public void writeAsXML(String path) {
      FixSerializationConfig config = this.build();
      Utility.writeNullAwayConfigInXMLFormat(config, path);
    }

    public FixSerializationConfig build() {
      if (outputDir == null) {
        throw new IllegalStateException("did not set mandatory output directory");
      }
      return new FixSerializationConfig(
          suggestEnabled,
          suggestEnclosing,
          fieldInitInfo,
          methodParamProtectionTestEnabled,
          paramIndex,
          new AnnotationConfig(nullable, nonnull),
          outputDir);
    }
  }
}
