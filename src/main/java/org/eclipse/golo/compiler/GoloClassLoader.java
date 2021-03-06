/*
 * Copyright (c) 2012-2018 Institut National des Sciences Appliquées de Lyon (INSA Lyon) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.golo.compiler;

import java.io.InputStream;
import java.util.List;

import gololang.ir.GoloModule;

/**
 * Provides a facility to dynamically load Golo source code and access the generated code from a dedicated class loader.
 * <p>
 * Golo source files can be compiled and the resulting JVM bytecode be injected into the class loader. It is important
 * to note that this class loader definition is not thread safe.
 * <p>
 * This class loader does not support reloading. Attempts to load source files that may produce the same bytecode
 * definitions will resulting in exceptions.
 */
public class GoloClassLoader extends ClassLoader {

  private final GoloCompiler compiler;

  /**
   * Creates a class loader from a parent.
   *
   * @param parent the parent classloader.
   */
  public GoloClassLoader(ClassLoader parent) {
    super(parent);
    compiler = new GoloCompiler();
  }

  /**
   * Creates a class loader from the default parent.
   */
  public GoloClassLoader() {
    super();
    compiler = new GoloCompiler();
  }

  public GoloCompiler getCompiler() {
    return this.compiler;
  }

  /**
   * Compiles and loads the resulting JVM bytecode for a Golo source file.
   *
   * @param goloSourceFilename    the source file name.
   * @param sourceCodeInputStream the source input stream.
   * @return the class matching the Golo module defined in the source.
   * @throws GoloCompilationException if either of the compilation phase failed.
   */
  public synchronized Class<?> load(String goloSourceFilename, InputStream sourceCodeInputStream) throws GoloCompilationException {
    return load(compiler.compile(goloSourceFilename, sourceCodeInputStream));
  }

  /**
   * Compiles and loads the resulting JVM bytecode for a Golo module IR.
   *
   * @param goloSourceFilename    the source file name.
   * @param module  the Golo module IR to load.
   * @return the class matching the Golo module defined in the IR.
   * @throws GoloCompilationException if either of the compilation phase failed.
   */
  public synchronized Class<?> load(String goloSourceFilename, GoloModule module) {
    compiler.refine(module);
    return load(compiler.generate(module, goloSourceFilename));
  }

  private Class<?> load(List<CodeGenerationResult> results) {
    Class<?> lastClassIsModule = null;
    for (CodeGenerationResult result : results) {
      byte[] bytecode = result.getBytecode();
      lastClassIsModule = defineClass(null, bytecode, 0, bytecode.length);
    }
    return lastClassIsModule;
  }
}
