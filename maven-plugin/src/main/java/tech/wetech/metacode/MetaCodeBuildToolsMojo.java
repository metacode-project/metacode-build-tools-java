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
  private String[] graalVMReflectionPattern;

  /**
   * Use Regex match resources
   */
  private String[] graalVMResourcesPattern;

  /**
   * Reflection file name to generate, default value is reflection-config.json
   */
  @Parameter(property = "graalVMReflectionFileName", defaultValue = "reflection-config.json")
  private String graalVMReflectionFileName;

  /**
   * Resources file name to generate, default value is resources-config.json
   */
  @Parameter(property = "graalVMResourcesFileName", defaultValue = "resources-config.json")
  private String graalVMResourcesFileName;

  @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
  private List<String> compilePath;

  @Parameter(property = "graalVMReflectionPattern")
  public void setGraalVMReflectionPattern(String[] graalVMReflectionPattern) {
    this.graalVMReflectionPattern = replaceSpecialCharacter(graalVMReflectionPattern);
  }

  @Parameter(property = "graalVMResourcesPattern")
  public void setGraalVMResourcesPattern(String[] graalVMResourcesPattern) {
    this.graalVMResourcesPattern = replaceSpecialCharacter(graalVMResourcesPattern);
  }

  @Override
  public void execute() {
    printParametersIfDebugEnabled();
    ConfigGenerator generator = new ConfigGenerator();
    generator.setLog(getLog());
    String path = compilePath.getFirst();
    String reflectionConfigJson = generator.generateReflectionConfig(path, graalVMReflectionPattern);
    String resourcesConfigJson = generator.generateResourcesConfig(graalVMResourcesPattern);
    output(reflectionConfigJson, Path.of(path, graalVMReflectionFileName).toFile());
    output(resourcesConfigJson, Path.of(path, graalVMResourcesFileName).toFile());
  }

  private void printParametersIfDebugEnabled() {
    getLog().debug("graalVMReflectionPattern=" + Arrays.toString(graalVMReflectionPattern));
    getLog().debug("graalVMResourcesPattern=" + Arrays.toString(graalVMResourcesPattern));
    getLog().debug("graalVMReflectionFileName=" + graalVMReflectionFileName);
    getLog().debug("graalVMResourcesFileName=" + graalVMResourcesFileName);
    getLog().debug("compilePath=" + compilePath);
  }

  private void output(String content, File file) {
    try {
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
