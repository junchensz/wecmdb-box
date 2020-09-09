package com.webank.cmdb.wecmdbbox.utils;

import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ParameterizedTypeImpl implements ParameterizedType {
    private final @Nullable Type ownerType;
    private final ImmutableList<Type> argumentsList;
    private final Class<?> rawType;

    public ParameterizedTypeImpl(@Nullable Type ownerType, Class<?> rawType, Type[] typeArguments) {
        checkNotNull(rawType);
        checkArgument(typeArguments.length == rawType.getTypeParameters().length);
        disallowPrimitiveType(typeArguments, "type parameter");
        this.ownerType = ownerType;
        this.rawType = rawType;
        this.argumentsList = ImmutableList.copyOf(typeArguments);
    }

    private static void disallowPrimitiveType(Type[] types, String usedAs) {
        for (Type type : types) {
            if (type instanceof Class) {
                Class<?> cls = (Class<?>) type;
                checkArgument(!cls.isPrimitive(), "Primitive type '%s' used as %s", cls, usedAs);
            }
        }
    }

    private static Type[] toArray(Collection<Type> types) {
        return types.toArray(new Type[types.size()]);
    }

    @Override
    public Type[] getActualTypeArguments() {
        return toArray(argumentsList);
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

}
