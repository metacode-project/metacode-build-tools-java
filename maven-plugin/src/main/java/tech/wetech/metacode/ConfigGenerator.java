package tech.wetech.metacode;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * generate reflection-config.json for native-image
 * @author cjbi
 */
public class ConfigGenerator {

  private Log log = new SystemStreamLog();

  public void setLog(Log log) {
    this.log = log;
  }

  /**
   * generate resource-config json with graalVM
   * @param patterns Ant-Style paths
   * @return json string
   */
  public String generateResourcesConfig(String... patterns) {
    StringBuilder sb = new StringBuilder("[");
    for (String pattern : patterns) {
      sb.append(String.format("""
        {
          "pattern": "%s"
        },""", pattern));
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append("]");
    return sb.toString();
  }

  /**
   * generate reflection-config json with graalVM
   * @param patterns regex paths
   * @return json string
   */
  public String generateReflectionConfig(String compilePath, String... patterns) {
    List<File> classsFileList = getClasssFileList(compilePath);
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    Set<String> classNames = new HashSet<>();
    for (File file : classsFileList) {
      String className = file.getPath().replace(compilePath, "")
        .replace("\\", ".").replaceAll(".class", "")
        .replaceFirst(".", "");
      for (String pattern : patterns) {
        if (antPathMatcher.match(pattern, className)) {
          classNames.add(className);
        }
      }
    }
    StringBuilder sb = new StringBuilder("[");
    for (String className : classNames) {
      sb.append(String.format("""
        {
          "name" : "%s",
          "allDeclaredConstructors": true,
          "allPublicConstructors": true,
          "allDeclaredMethods": true,
          "allPublicMethods": true,
          "allDeclaredFields": true,
          "allPublicFields": true
        },""", className));
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append("]");
    return sb.toString();
  }

  private List<File> getClasssFileList(String compilePath) {
    File file = new File(compilePath);
    List<File> fileList = new ArrayList<>();
    listFiles(file, fileList);
    return fileList;
  }

  private void listFiles(File rootFile, List<File> classFiles) {
    File[] files = rootFile.listFiles();
    assert files != null;
    for (File file : files) {
      if (file.isFile() && file.getName().endsWith(".class")) {
        log.debug("Found reflection class file: " + file);
        classFiles.add(file);
      } else if (file.isDirectory()) {
        listFiles(file, classFiles);
      }
    }
  }


}
