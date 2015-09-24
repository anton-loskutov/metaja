package org.metaja.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReflectionUtils {

    public enum Modifier {
        PUBLIC(java.lang.reflect.Modifier.PUBLIC),
        PROTECTED(java.lang.reflect.Modifier.PROTECTED),
        PRIVATE(java.lang.reflect.Modifier.PRIVATE),

        ABSTRACT(java.lang.reflect.Modifier.ABSTRACT),
        STATIC(java.lang.reflect.Modifier.STATIC),
        ;

        Modifier(int modifier) {
            this.modifier = modifier;
        }

        private final int modifier;

        public boolean isModified(Class c) {
            return (c.getModifiers() & modifier) == modifier;
        }

        public boolean notModified(Class c) {
            return (c.getModifiers() & modifier) == 0;
        }

        public boolean isModified(Method m) {
            return (m.getModifiers() & modifier) == modifier;
        }

        public boolean notModified(Method m) {
            return (m.getModifiers() & modifier) == 0;
        }
    }

    public static Stream<IterationUtils.Iteration<Method>> getMethods(Class<?> clazz, Predicate<Method> filter) {
        return IterationUtils.stream(Stream.of(clazz.getMethods()).filter(filter).iterator());
    }

    public static boolean isPrimitive(Class c) {
        return c.isPrimitive();
    }

    public static boolean isNotPrimitive(Class c) {
        return !c.isPrimitive();
    }

    public static boolean isFloating(Class c) {
        return c.equals(Double.TYPE) || c.equals(Float.TYPE);
    }

    public static boolean isNotFloating(Class c) {
        return !c.equals(Double.TYPE) && !c.equals(Float.TYPE);
    }

    private ReflectionUtils() {
    }

    // for org.metaja.template.TemplateOps
    @Deprecated
    public static ReflectionUtils INSTANCE = new ReflectionUtils();
}
