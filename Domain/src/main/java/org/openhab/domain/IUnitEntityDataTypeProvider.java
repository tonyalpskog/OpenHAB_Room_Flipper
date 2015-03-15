package org.openhab.domain;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.EntityDataTypeSource;
import org.openhab.domain.rule.UnitEntityDataType;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IUnitEntityDataTypeProvider {
    UnitEntityDataType<?> getEntityDataType(OpenHABWidget openHABWidget, String staticValue, EntityDataTypeSource sourceType);

    UnitEntityDataType<?> getUnitEntityDataType(OpenHABWidget openHABWidget);

    UnitEntityDataType<?> getStaticEntityDataType(OpenHABWidget openHABWidget, String staticValue);

    List<UnitEntityDataType<?>> getUnitDataTypeList();
}
