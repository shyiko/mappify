# mappify [![Build Status](https://travis-ci.org/shyiko/mappify.png?branch=master)](https://travis-ci.org/shyiko/mappify)

Dead-simple object mapping in Java.

## Usage

### Standalone usage

0. Add Maven dependency (or download and include jars from [here](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.shyiko.mappify%22%20AND%20v%3A%221.1.0%22))
```xml
<dependency>
    <groupId>com.github.shyiko.mappify</groupId>
    <artifactId>mappify-handcraft</artifactId>
    <version>1.1.0</version>
</dependency>
```
> The latest development version always available through [Sonatype Snapshots repository](https://oss.sonatype.org/content/repositories/snapshots).

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
    mapper.mapToHashSet(Array.asList(new Source(2), new Source(3)), Target.class);
```

### Integration with Spring

0. Add Maven dependency
```xml
<dependency>
    <groupId>com.github.shyiko.mappify</groupId>
    <artifactId>mappify-handcraft-spring</artifactId>
    <version>1.1.0</version>
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

```java
Mapper mapper = mock(AbstractMapper.class, Mockito.CALLS_REAL_METHODS);
do...(...).when(mapper).map(any(), any(), anyString(), any(MappingContext.class));
```

### Frequently Asked Question

* Why does Mapper have mapToArrayList but no mapToLinkedList method?

    All mapTo... aliases were introduced as optimizations to the mapping process. So, instead of
`mapper.map(collection, Target.class, new ArrayList<Target>(collection.size()))`
one could simply use
`mapper.mapToArrayList(collection, Target.class)`. For some data types, like LinkedList, there is nothing that
can be done to improve the performance. As a result, simple
`mapper.map(collection, Target.class, new LinkedList<Target>())` will do just fine.

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)