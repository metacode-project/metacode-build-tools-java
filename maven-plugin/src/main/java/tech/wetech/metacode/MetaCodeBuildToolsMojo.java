package tech.wetech.metacode;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;


/**
 * Some MetaCode build helper tools
 * @author cjbi
 */
@Mojo(name = "exec", requiresDependencyResolution = COMPILE, defaultPhase = LifecyclePhase.VALIDATE)
public class MetaCodeBuildToolsMojo extends AbstractMojo {

  /**
   * Use Ant-style path match reflection class
   */
  private String[] graalVMReflectionIncludes;

  private String[] graalVMReflectionExcludes;

  /**
   * Reflection file name to generate, default value is reflection-config.json
   */
  @Parameter(property = "graalVMReflectionFileName", defaultValue = "reflection-config.json")
  private String graalVMReflectionFileName;

  @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
  private List<String> compilePath;

  @Parameter(property = "graalVMReflectionIncludes")
  public void setGraalVMReflectionIncludes(String[] graalVMReflectionIncludes) {
    this.graalVMReflectionIncludes = replaceSpecialCharacter(graalVMReflectionIncludes);
  }

  @Parameter(property = "graalVMReflectionExcludes")
  public void setGraalVMReflectionExcludes(String[] graalVMReflectionExcludes) {
    this.graalVMReflectionExcludes = replaceSpecialCharacter(graalVMReflectionExcludes);
  }

  @Override
  public void execute() {
    printParametersIfDebugEnabled();
    ConfigGenerator generator = new ConfigGenerator();
    generator.setLog(getLog());
    String path = compilePath.getFirst();
    if (graalVMReflectionIncludes.length > 0) {
      String reflectionConfigJson = generator.generateReflectionConfig(path, graalVMReflectionIncludes, graalVMReflectionExcludes);
      output(reflectionConfigJson, Path.of(path, graalVMReflectionFileName).toFile());
    }
  }

  private void printParametersIfDebugEnabled() {
    getLog().debug("graalVMReflectionIncludes=" + Arrays.toString(graalVMReflectionIncludes));
    getLog().debug("graalVMReflectionExcludes=" + Arrays.toString(graalVMReflectionExcludes));
    getLog().debug("graalVMReflectionFileName=" + graalVMReflectionFileName);
    getLog().debug("compilePath=" + compilePath);
  }

  private void output(String content, File file) {
    try {
      getLog().debug("Out File Content:" + content);
      getLog().debug("Output File:" + file);
      file.createNewFile();
      try (OutputStream os = new FileOutputStream(file)) {
        os.write(content.getBytes());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Replace the escape and blank character
   * @param options
   * @return
   */
  private String[] replaceSpecialCharacter(String[] options) {
    for (int i = 0; i < options.length; i++) {
      options[i] = options[i].replace("\n", "")
        .replace("\t", "")
        .trim();
    }
    return options;
  }

}
