# metacode-maven-plugin

MetaCode的构建辅助插件

* 支持通过包名生成针对graalVM的reflection-config.json文件

## 使用示例

引入依赖：

```xml
<plugins>
  <plugin>
    <groupId>tech.wetech.metacode</groupId>
    <artifactId>metacode-maven-plugin</artifactId>
    <version>1.0.5</version>
    <executions>
      <execution>
        <phase>compile</phase>
        <goals>
          <goal>exec</goal>
        </goals>
      </execution>
    </executions>
    <configuration>
      <graalVMReflectionIncludes>
        tech.wetech.metacode.application.dto.*,
        tech.wetech.metacode.domain.**.model.*,
        tech.wetech.metacode.domain.**.event.*,
        tech.wetech.metacode.infrastructure.jackson.JacksonUtils,
        tech.wetech.metacode.application.dto.SessionDataDTO,
        tech.wetech.metacode.infrastructure.jackson.BaseEntitySerializer,
        tech.wetech.metacode.infrastructure.jsonlogic.JsonLogicUtils,
        tech.wetech.metacode.infrastructure.pinyin4j.Pinyin4jUtil,
        tech.wetech.metacode.infrastructure.cache.NotABeanKeyGen
      </graalVMReflectionIncludes>
      <graalVMReflectionExcludes>
        tech.wetech.metacode.domain.**.model.Q*
      </graalVMReflectionExcludes>
    </configuration>
  </plugin>
</plugins>
```

## 配置说明

### graalVMReflectionIncludes

**必须** Ant-style 分格路径引入的 Reflection class，通过逗号分隔

**默认值** 无

### graalVMReflectionExcludes

_可选_ Ant-style 分格路径排除的 Reflection class，通过逗号分隔

**默认值** 无

### graalVMReflectionFileName

_可选_ 生成的文件名称

**默认值** reflection-config.json
