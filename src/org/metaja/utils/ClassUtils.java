package org.metaja.utils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ClassUtils {

    private ClassUtils() {}

    public static class ReflectiveOperationRuntimeException extends RuntimeException {
        public ReflectiveOperationRuntimeException(ReflectiveOperationException cause) {
            super(cause);
        }
    }

    public static <C> Class<C> loadClass(ClassLoader cl, String name) throws ReflectiveOperationRuntimeException {
        try {
            @SuppressWarnings("unchecked")
            Class<C> clazz = (Class<C>) cl.loadClass(name);
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new ReflectiveOperationRuntimeException(e);
        }
    }

    public static <C> C newInstance(boolean includingPrivateConstructors, Class<C> clazz, Object ... constructorArgs) throws ReflectiveOperationRuntimeException {
        try {
            Constructor<?> matchedConstructor = null;
            for (Constructor<?> constructor : includingPrivateConstructors ? clazz.getDeclaredConstructors() : clazz.getConstructors()) {
                if (constructor.getParameterCount() == constructorArgs.length) {
                    boolean matched = true;
                    for (int i =0; i < constructor.getParameterCount() && matched ;i++) {
                        matched = isAssignable(constructor.getParameters()[i].getType(), constructorArgs[i].getClass());
                    }
                    if (matched && matchedConstructor != null) {
                        throw new IllegalArgumentException("More than one constructor matched!");
                    }
                    matchedConstructor = constructor;
                }
            }
            if (matchedConstructor == null) {
                throw new NoSuchMethodException();
            }

            if (includingPrivateConstructors && !matchedConstructor.isAccessible()) {
                matchedConstructor.setAccessible(true);
            }

            @SuppressWarnings("unchecked")
            C instance = (C) matchedConstructor.newInstance(constructorArgs);

            return instance;
        } catch (ReflectiveOperationException e) {
            throw new ReflectiveOperationRuntimeException(e);
        }
    }

    public static <C> C newInstance(Class<C> clazz, Object ... constructorArgs) throws ReflectiveOperationRuntimeException {
        return newInstance(false, clazz, constructorArgs);
    }

    public static boolean isAssignable(Class<?> cSuper, Class<?> cExtends) {
        return isAssignable.apply(cSuper, cExtends);
    }

    public static final BiFunction<Class<?>, Class<?>, Boolean> isAssignable = new BiFunction<Class<?>, Class<?>, Boolean>() {
        class _boolean {}
        class _double {}
        class _float extends _double {}
        class _long extends _float {}
        class _int extends _long {}
        class _char extends _int {}
        class _short extends _int {}
        class _byte extends _short {}

        private final Map<Class<?>, Class<?>> unboxing = new HashMap<>();
        {
            unboxing.put(java.lang.Boolean.TYPE, _boolean.class);
            unboxing.put(java.lang.Double.TYPE, _double.class);
            unboxing.put(java.lang.Float.TYPE, _float.class);
            unboxing.put(java.lang.Long.TYPE, _long.class);
            unboxing.put(java.lang.Integer.TYPE, _int.class);
            unboxing.put(java.lang.Character.TYPE, _char.class);
            unboxing.put(java.lang.Short.TYPE, _short.class);
            unboxing.put(java.lang.Byte.TYPE, _byte.class);

            unboxing.put(java.lang.Boolean.class, _boolean.class);
            unboxing.put(java.lang.Double.class, _double.class);
            unboxing.put(java.lang.Float.class, _float.class);
            unboxing.put(java.lang.Long.class, _long.class);
            unboxing.put(java.lang.Integer.class, _int.class);
            unboxing.put(java.lang.Character.class, _char.class);
            unboxing.put(java.lang.Short.class, _short.class);
            unboxing.put(java.lang.Byte.class, _byte.class);
        }

        private Class<?> unbox(Class<?> c) {
            Class<?> cUnboxed = unboxing.get(c);
            return cUnboxed != null ? cUnboxed : c;
        }

        @Override
        public Boolean apply(Class<?> cSuper, Class<?> cExtends) {
            if (Object.class.equals(cSuper)) {
                return true;
            } else if (cSuper.isPrimitive()) {
                return unbox(cSuper).isAssignableFrom(unbox(cExtends));
            } else if (cExtends.isPrimitive()) {
                return unbox(cSuper) == unbox(cExtends);
            } else {
                return cSuper.isAssignableFrom(cExtends);
            }
        }
    };
}
