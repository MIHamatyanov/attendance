package ru.vmk.attendance.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;

public class NullableModelMapper extends ModelMapper {

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        if (source == null)
            return null;

        Assert.notNull(destinationType, "destinationType");

        return super.map(source, destinationType);
    }
}

