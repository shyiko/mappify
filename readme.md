# mappify

Dead-simple object mapping in Java.

## Usage

### Standalone usage

0. Add Maven dependency
```xml
<dependency>
    <groupId>com.github.shyiko.mappify</groupId>
    <artifactId>mappify-handcraft</artifactId>
    <version>1.0.0</version>
</dependency>
```

1. Define mapping provider(s)
```java
    class MappingProvider {
        @Mapping
        public void mapFromSourceToTarget(Source source, Target target) {
            target.name = "Target #" + source.id;
        }
    }
```
> NOTE: Each mapping provider can define arbitrary number of @Mapping-annotated methods.

2. Create & configure Mapper instance
```java
Mapper mapper = new HandcraftMapper();
mapper.register(new MappingProvider()); // call this method for each mapping provider you have
```

3. Use it across the application
```java
Target mappedTarget = mapper.map(new Source(1), Target.class);
Set<Target> mappedTargets =
    mapper.map(Array.asList(new Source(2), new Source(3)), Target.class, new HashSet<Target>());
```

### Integration with Spring

0. Add Maven dependency
```xml
<dependency>
    <groupId>com.github.shyiko.mappify</groupId>
    <artifactId>mappify-handcraft-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

1. Include the following line into your spring context configuration file
```xml
<import resource="classpath:mappify-handcraft-spring-context.xml"/>
```

2. Define mapping provider(s)
```java
    @MappingProvider
    public class EntityMappingProvider {

        @Mapping
        public void mapToDTO(Entity entity, EntityDTO dto) {
            ...
        }

        @Mapping
        public void mapFromDTO(EntityDTO entityDTO, Entity entity, MappingContext context) {
            ...
        }

        ...
    }
```
> NOTE: @MappingProvider extends @Controller. As a result, mapping providers are discoverable with Spring's
`<context:component-scan .../>`. If you don't use it, just make sure mapping providers are wired into the context
(e.g. by using `<bean ... class="mapping provider class"/>`).

3. Call the Mapper
```java
    @Autowired
    private Mapper mapper;

    ...
    Entity entity = ...;
    EntityDTO dto = mapper.map(entity, EntityDTO.class);
    ...
```

### Testing with Mockito

    Mapper mapper = mock(AbstractMapper.class, Mockito.CALLS_REAL_METHODS);
    do...(...).when(mapper).map(any(), any(), anyString(), any(MappingContext.class));

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)