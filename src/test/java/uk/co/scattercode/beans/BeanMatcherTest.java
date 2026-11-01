package uk.co.scattercode.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeanMatcherTest {

    // We use the concrete implementation because we wish to test some of the
    // protected methods, which are not available on the public interface.
    BeanMatcher matcher = new BeanMatcher();

    @Test
    void shouldLowercaseFirstLetter() {
        assertEquals("camelCase", matcher.lowercaseFirstLetter("CamelCase"));
        assertEquals("camelCase", matcher.lowercaseFirstLetter("camelCase"));
        assertEquals("", matcher.lowercaseFirstLetter(""));
        assertNull(matcher.lowercaseFirstLetter(null));
    }

    @Test
    void shouldMatchAnythingWithEmptyFilter() {
        assertTrue(
                matcher.matches(new StubBean("foo", "bar"), new String[] {}),
                "An empty filter should always return a match.");
    }

    @Test
    void shouldMatchIfAllFilterPropertesInBean() {
        assertTrue(
                matcher.matches(new StubBean("foo", "bar"), new String[] {"foo=foo", "bar=bar"}),
                "Expected 'getFoo' to return 'foo' and 'getBar' to return 'bar'.");
        assertTrue(
                matcher.matches(new StubBean("foo", "bar"), new String[] {"bar=bar"}),
                "Expected 'getBar' to return 'bar' and to be sufficient for a match.");
        assertTrue(
                matcher.matches(new StubBean("foo", "bar"), new String[] {}),
                "An empty filter should always return true.");
    }

    @Test
    void shouldNotMatchIfFilterPropertyNotInBean() {
        assertFalse(
                matcher.matches(new StubBean("foo", "bar"), new String[] {"uncle=bob"}),
                "There is no bean property 'uncle', so this should not match.");
    }

    @Test
    void shouldNotMatchIfFilterPropertyNotEqualToBeanProperty() {
        assertFalse(
                matcher.matches(new StubBean("foo", "bar"), new String[] {"foo=bar"}),
                "Bean property 'foo' has value 'foo', not 'bar'.");
    }

    @Test
    void shouldMatchPropertiesUsingVarArgs() {
        assertTrue(
                matcher.matches(
                        new StubBean("foo", "bar"),
                        new BeanPropertyFilter("foo", "foo"),
                        new BeanPropertyFilter("bar", "bar")),
                "Expected 'getFoo' to return 'foo' and 'getBar' to return 'bar'.");
        assertTrue(
                matcher.matches(new IntegerBean(3), new BeanPropertyFilter("prop", 3)),
                "Expected 'getProp' to return '3' and to be sufficient for a match.");
        assertFalse(
                matcher.matches(new IntegerBean(3), new BeanPropertyFilter("prop", 4)),
                "Expected 'getProp' to return '3' and to not match 4.");
    }

    @Test
    void shouldCompareObjects() {
        String o1 = "hello";
        String o2 = "hello";
        assertTrue(matcher.isEquivalent(o1, o2));

        Boolean o3 = Boolean.FALSE;
        boolean o4 = false;
        assertTrue(matcher.isEquivalent(o3, o4));
        o4 = true;
        assertFalse(matcher.isEquivalent(o3, o4));
    }

    public static class StubBean {
        private final String foo;
        private final String bar;

        public StubBean(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }
    }

    public static class IntegerBean {
        private Integer prop;

        public IntegerBean(Integer prop) {
            this.prop = prop;
        }

        public Integer getProp() {
            return prop;
        }

        public void setProp(Integer prop) {
            this.prop = prop;
        }
    }
}
