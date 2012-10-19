    # mappify

Dead-simple object mapping in Java.

## Usage

0.Add Maven dependency

```xml
<dependency>
    <groupId>com.github.shyiko.mappify</groupId>
    <artifactId>mappify-handcraft-spring</artifactId>
    <version>0.1.0</version>
</dependency>
```

1.Add the following line to your spring context

```xml
<import resource="classpath:mappify-handcraft-spring-context.xml"/>
```

2.Define mappings

```java
@MappingProvider
public class EntityMappingProvider {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping
    public void mapToDTO(Entity entity, EntityDTO dto) {
        ...
    }

    @Mapping
    public void mapFromDTO(EntityDTO entityDTO, Entity entity, MappingContext context) {
        ...
    }
}
```
> NOTE: @MappingProvider extends @Controller. As a result, mapping providers can be discovered with Spring's 
`<context:component-scan .../>`. Otherwise, they need to be hooked up using `<bean ... class="mapping provider class"/>`.

3.Call the ObjectMapper

```java
    @Autowired
    private ObjectMapper objectMapper;

    ...
    Entity entity = ...;
    EntityDTO dto = objectMapper.map(entity, EntityDTO.class);
    ...
```

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)