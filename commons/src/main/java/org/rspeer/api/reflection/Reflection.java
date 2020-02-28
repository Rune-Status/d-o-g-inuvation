package org.rspeer.api.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Reflection {

    private Reflection() {

    }

    public static <K> FieldContainer<K> declaredField(String name) {
        return new FieldContainer<>(name, true);
    }

    public static <K> FieldContainer<K> field(String name) {
        return new FieldContainer<>(name, false);
    }

    public static <K> MethodContainer<K> declaredMethod(String name) {
        return new MethodContainer<>(name, true);
    }

    public static <K> MethodContainer<K> method(String name) {
        return new MethodContainer<>(name, false);
    }

    public static <K> ConstructorContainer<K> constructor() {
        return new ConstructorContainer<>();
    }

    public static class MethodContainer<K> extends Container {
        // <Class, <Method Name, <Signature, Method>>
        private static final Map<Class, Map<String, List<Method>>> lookup = new HashMap<>();
        private Class<?>[] param;
        private boolean declared;
        private Method raw;

        public MethodContainer(String name, boolean declared) {
            super(name);
            this.declared = declared;
        }

        public <E> MethodContainer<E> withReturnType(Class<E> clazz) {
            return (MethodContainer<E>) this;
        }

        public MethodContainer<K> in(Object clazz) {
            if (!(clazz instanceof Class)) {
                in = clazz;
                target = clazz.getClass();
            } else {
                target = (Class<?>) clazz;
            }
            return this;
        }

        public MethodContainer<K> withParameters(Class<?>... args) {
            param = args;
            return this;
        }

        public K invoke(Object... args) {
            try {
                Method raw = getRaw();
                boolean accessible = raw.isAccessible();
                raw.setAccessible(true);
                K ret = (K) raw.invoke(in, args);
                raw.setAccessible(accessible);
                return ret;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Method getRaw() {
            if (raw != null)
                return raw;
            Map<String, List<Method>> methods = lookup.get(target);
            Method raw = null;
            if (methods != null) {
                List<Method> get = methods.get(name);
                if (get != null)
                    for (int i = 0; i < get.size(); i++) {
                        Method m = get.get(i);
                        Class<?>[] args = m.getParameterTypes();
                        boolean noParams = param == null;
                        if ((args.length == 0 && noParams) || (!noParams && Arrays.equals(args, param))) {
                            raw = m;
                            break;
                        }
                    }
            }
            if (raw == null) {
                try {
                    if (param != null && param.length > 0) {
                        raw = declared ? target.getDeclaredMethod(name, param) : target.getMethod(name, param);
                    } else {
                        raw = declared ? target.getDeclaredMethod(name) : target.getMethod(name);
                    }
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }

                Map<String, List<Method>> cache = methods;
                if (methods == null) {
                    lookup.put(target, cache = new HashMap<>());
                }
                List<Method> ms = cache.get(name);
                if (ms == null) {
                    cache.put(name, ms = new ArrayList<>());
                }
                ms.add(raw);
                this.raw = raw;
            }
            return raw;
        }

        public MethodHandle handle() {
            try {
                return MethodHandles.lookup().unreflect(getRaw());
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static class ConstructorContainer<K> extends Container {
        private Class<?>[] param;

        public ConstructorContainer<K> withParameters(Class<?>... args) {
            param = args;
            return this;
        }

        public <E> ConstructorContainer<E> in(Class<E> clazz) {
            return (ConstructorContainer<E>) this;
        }

        public K newInstance(Object... args) {
            try {
                Constructor raw = getRaw();
                boolean accessible = raw.isAccessible();
                raw.setAccessible(true);
                K instance = (K) raw.newInstance(args);
                raw.setAccessible(accessible);
                return instance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Constructor<?> getRaw() {
            try {
                return (param != null && param.length > 0) ? target.getDeclaredConstructor(param) : target.getDeclaredConstructor();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class FieldContainer<K> extends Container {
        protected boolean declared;

        public FieldContainer(String name, boolean declared) {
            super(name);
            this.declared = declared;
        }

        public <E> FieldContainer<E> ofType(Class<E> clazz) {
            return (FieldContainer<E>) this;
        }

        public FieldContainer<K> in(Optional<Object> clazz) {
            return in(clazz.get());
        }

        public FieldContainer<K> in(Object clazz) {
            if (!(clazz instanceof Class)) {
                in = clazz;
                target = clazz.getClass();
            } else {
                target = (Class<?>) clazz;
            }
            return this;
        }

        public void set(Object object) {
            try {
                Field raw = getRaw();
                boolean accessible = raw.isAccessible();
                raw.setAccessible(true);
                raw.set(in, object);
                raw.setAccessible(accessible);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public K get() {
            try {
                Field raw = getRaw();
                boolean accessible = raw.isAccessible();
                raw.setAccessible(true);
                K ret = (K) raw.get(in);
                raw.setAccessible(accessible);
                return ret;
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        public Field getRaw() {
            try {
                return declared ? target.getDeclaredField(name) : target.getField(name);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Container<K> {
        protected String name;
        protected K in;
        protected Class<?> target;

        public Container(String name) {
            this.name = name;
        }

        public Container() {
        }
    }
}
