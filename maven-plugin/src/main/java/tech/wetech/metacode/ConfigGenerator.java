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
   * generate reflection-config json with graalVM
   * @param patterns regex paths
   * @return json string
   */
  public String generateReflectionConfig(String compilePath, String... patterns) {
    List<File> classsFileList = getClasssFileList(compilePath);
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    Set<String> classNames = new HashSet<>();
    for (File file : classsFileList) {
      log.debug("Parsing file: " + file);
      String className = file.getPath().replace(compilePath, "")
        .replaceAll("[/\\\\]", ".").replaceAll(".class", "")
        .replaceFirst(".", "");
      log.debug("Extract file class name: " + className);
      for (String pattern : patterns) {
        if (antPathMatcher.match(pattern, className)) {
          log.debug("Found reflection class: " + file);
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
        classFiles.add(file);
      } else if (file.isDirectory()) {
        listFiles(file, classFiles);
      }
    }
  }


}
