package eu.simpleg.cuplan.core.config;

import eu.simpleg.cuplan.core.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FileGetterTest {
    private Cache cache;
    private FileGetter getter;
    private long expireCacheItemAfterMilliseconds;

    @BeforeEach
    public void initialize() {
        URL resourceUrl = getClass().getClassLoader().getResource("config/FileGetterTest/");
        cache = new Cache(10000000);
        expireCacheItemAfterMilliseconds = 10000000;
        getter = new FileGetter(Path.of(resourceUrl.getPath()), cache, expireCacheItemAfterMilliseconds);
    }

    @Test
    public void getExistingKeyReturnsExpectedValue() throws ExecutionException, InterruptedException {
        final int expectedValue = 1234;

        Integer value = getter
                .get(Path.of("application.yaml"), "configuration:nested:test", Integer.class)
                .get()
                .unwrap();

        Assertions.assertEquals(expectedValue, value);
    }

    @Test
    public void getExistingComplexKeyReturnsExpectedValue() throws ExecutionException, InterruptedException {
        ChildObject childObject = new ChildObject();
        childObject.a = 2;
        childObject.b = 4;
        Child expectedValue = new Child();
        expectedValue.name = "Alpha";
        expectedValue.description = "Whatever";
        expectedValue.object = childObject;

        Child value = getter
                .get(Path.of("dir1/dir2/example.yaml"), "parent:child", Child.class)
                .get()
                .unwrap();

        Assertions.assertEquals(expectedValue, value);
    }

    @Test
    public void getExistingComplexKeyThatPointsToArrayReturnsExpectedValue()
            throws ExecutionException, InterruptedException {
        String[] expectedValue = new String[]{
                "http://localhost:4200",
                "https://cuplan.simpleg.eu"
        };

        String[] value = getter
                .get(Path.of("other/new/array.yaml"), "cors:origins", String[].class)
                .get()
                .unwrap();

        Assertions.assertArrayEquals(expectedValue, value);
    }

    @Test
    public void cleanCacheClearsCache() {
        final String key = "EXAMPLE";
        final String value = "HAHAHA";
        cache.set(key, value, expireCacheItemAfterMilliseconds);

        getter.cleanCache();

        Assertions.assertTrue(cache.isEmpty());
    }

    private static class Child {
        public String name;
        public String description;
        public ChildObject object;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Child child = (Child) o;
            return Objects.equals(name, child.name) &&
                    Objects.equals(description, child.description) &&
                    Objects.equals(object, child.object);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, object);
        }
    }

    private static class ChildObject {
        public int a;
        public int b;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChildObject that = (ChildObject) o;
            return a == that.a && b == that.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}
