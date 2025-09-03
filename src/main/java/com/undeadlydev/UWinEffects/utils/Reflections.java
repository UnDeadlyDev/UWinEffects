package com.undeadlydev.UWinEffects.utils;

import com.google.common.primitives.Primitives;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;


public class Reflections {

    public static <T> @NotNull Optional<Class<T>> getClass(@NotNull String path) {
        try
        {
            return Optional.of((Class<T>) Class.forName(path));
        } catch(ClassNotFoundException e)
        {
            return Optional.empty();
        }
    }

    public static <T> @NotNull Optional<T> getInstance(@NotNull String path, @Nullable Object... args) {
        return getClass(path).flatMap(objectClass -> getInstance((Class<T>) objectClass, args));
    }

    public static <T> @NotNull Optional<T> getInstance(@NotNull Class<T> clazz, @Nullable Object... args) {
        try {
            Class<?>[] argTypes = Arrays.stream(args)
                    .map(Object::getClass)
                    .toArray(Class[]::new);
            Constructor<?> ctor = clazz.getDeclaredConstructor(argTypes);
            ctor.setAccessible(true);
            return Optional.of((T) ctor.newInstance(args));
        } catch(Exception e)
        {
            return Optional.empty();
        }
    }

    private static @NotNull Method findMethod(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object[] args) throws NoSuchMethodException {
        Class<?> current = clazz;
        Class<?>[] argTypes = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);

        while(current != null) {
            for(Method method : current.getDeclaredMethods()) {
                if(!method.getName().equals(name))
                    continue;

                Class<?>[] paramTypes = method.getParameterTypes();
                boolean isVarArgs = method.isVarArgs();

                if(isCompatible(argTypes, paramTypes, isVarArgs))
                {
                    method.setAccessible(true);
                    return method;
                }
            }
            current = current.getSuperclass();
        }

        throw new NoSuchMethodException("No compatible method " + name + " found in class " + clazz.getName() + "(" + Arrays.toString(args) + ")");
    }

    private static boolean isCompatible(@NotNull Class<?>[] args, @NotNull Class<?>[] params, boolean isVarArgs) {
        if(!isVarArgs) {
            if(args.length != params.length)
                return false;
            for(int i = 0; i < args.length; i++)
            {
                if(!Primitives.wrap(params[i]).isAssignableFrom(Primitives.wrap(args[i])))
                    return false;
            }
            return true;
        }

        if(args.length < params.length - 1)
            return false;
        for(int i = 0; i < params.length - 1; i++) {
            if(!Primitives.wrap(params[i]).isAssignableFrom(Primitives.wrap(args[i])))
                return false;
        }

        Class<?> varArgType = Primitives.wrap(params[params.length - 1].getComponentType());
        for(int i = params.length - 1; i < args.length; i++) {
            if(!varArgType.isAssignableFrom(Primitives.wrap(args[i])))
                return false;
        }
        return true;
    }

    public static <V> @NotNull ReflectionChain<V> invokeMethod(@NotNull Object object, @NotNull String methodName, @Nullable Object... args) {
        try {
            Method method = findMethod(object.getClass(), methodName, args);
            method.setAccessible(true);
            return new ReflectionChain<>((V) method.invoke(object, args));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> @NotNull ReflectionChain<V> invokeStaticMethod(@NotNull String classPath, @NotNull String methodName, @Nullable Object... args) {
        try {
            Class<?> clazz = Class.forName(classPath);
            Method method = findMethod(clazz, methodName, args);
            method.setAccessible(true);
            return new ReflectionChain<>((V) method.invoke(null, args));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull Field findField(@NotNull Class<?> clazz, @NotNull String fieldName) throws NoSuchFieldException {
        while(clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch(NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }

    public static <T> @NotNull ReflectionChain<T> getField(@NotNull Object object, @NotNull String fieldName) {
        try {
            Field field = findField(object.getClass(), fieldName);
            return new ReflectionChain<>((T) field.get(object));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, V> @Nullable V getStaticField(@NotNull Class<T> clazz, @Nullable String fieldName) {
        try {
            Field field = findField(clazz, fieldName);
            return (V) field.get(null);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> @Nullable T getStaticField(@NotNull String classPath, @NotNull String fieldName) {
        try {
            Class<?> clazz = Class.forName(classPath);
            Field field = findField(clazz, fieldName);
            return (T) field.get(null);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(@NotNull Object object, @NotNull String fieldName, @Nullable Object value) {
        try {
            Field field = findField(object.getClass(), fieldName);
            field.set(object, value);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setStaticField(@NotNull String classPath, @NotNull String fieldName, @Nullable Object value) {
        try {
            Class<?> clazz = Class.forName(classPath);
            Field field = findField(clazz, fieldName);
            field.set(null, value);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class ReflectionChain<V> {
        private final @Nullable V value;

        public ReflectionChain(@Nullable V value) {
            this.value = value;
        }

        public @NotNull ReflectionChain<V> thanInvoke(@NotNull String methodName, @Nullable Object... args) {
            if(value == null)
                return new ReflectionChain<>(null);

            try {
                Method method = findMethod(value.getClass(), methodName, args);
                method.setAccessible(true);
                return new ReflectionChain<>((V) method.invoke(value, args));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        public @NotNull ReflectionChain<V> thanGetField(@NotNull String fieldName) {
            if(value == null)
                return new ReflectionChain<>(null);
            try {
                Field field = findField(value.getClass(), fieldName);
                return new ReflectionChain<>((V) field.get(value));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        public @Nullable V get() {
            return value;
        }
    }
}
