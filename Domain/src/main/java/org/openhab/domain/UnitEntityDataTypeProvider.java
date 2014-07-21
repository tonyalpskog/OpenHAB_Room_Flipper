package org.openhab.domain;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.EntityDataTypeSource;
import org.openhab.domain.rule.UnitEntityDataType;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2014.
 */
@Singleton
public class UnitEntityDataTypeProvider implements IUnitEntityDataTypeProvider {
    @Inject
    public UnitEntityDataTypeProvider() {

    }

    @Override
    public UnitEntityDataType<?> getEntityDataType(OpenHABWidget openHABWidget, String staticValue, final EntityDataTypeSource sourceType) {
        UnitEntityDataType unitEntityDataType = null;
        final boolean isUnitType = sourceType == EntityDataTypeSource.UNIT;

        //TODO - TA: replace static values with constants (ON, OFF, OPEN, CLOSED, Undefined)
        switch(openHABWidget.getItem().getType()) {
            case Switch:
                Boolean aBoolean;

                if((isUnitType && openHABWidget.getItem().getState().equalsIgnoreCase("Undefined")) ||
                        (!isUnitType && (staticValue == null || staticValue.equalsIgnoreCase("Undefined"))))
                    aBoolean = null;
                else
                    aBoolean = isUnitType? openHABWidget.getItem().getState().equalsIgnoreCase("ON") : staticValue.equalsIgnoreCase("ON");

                unitEntityDataType = new UnitEntityDataType<Boolean>(isUnitType ? openHABWidget.getItem().getName() : UUID.randomUUID().toString(), aBoolean, Boolean.class);
                break;

            case Contact:
                if((isUnitType && openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                        || (!isUnitType && (staticValue == null || staticValue.equalsIgnoreCase("Undefined"))))
                    aBoolean = null;
                else
                    aBoolean = isUnitType? openHABWidget.getItem().getState().equalsIgnoreCase("CLOSED") : staticValue.equalsIgnoreCase("CLOSED");

                unitEntityDataType = new UnitEntityDataType<Boolean>(isUnitType? openHABWidget.getItem().getName() : UUID.randomUUID().toString(), aBoolean, Boolean.class);
                break;

            case Rollershutter:
            case Dimmer:
            case Number:
                Double aNumber;
                if((isUnitType && openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                        || (!isUnitType && (staticValue == null || staticValue.equalsIgnoreCase("Undefined"))))
                    aNumber = null;
                else
                    aNumber = isUnitType? Double.valueOf(openHABWidget.getItem().getState()) : Double.valueOf(staticValue);

                unitEntityDataType = new UnitEntityDataType<Double>(isUnitType? openHABWidget.getItem().getName() : UUID.randomUUID().toString(), aNumber, Double.class);
                break;
        }
        return unitEntityDataType;
    }

    @Override
    public UnitEntityDataType<?> getUnitEntityDataType(OpenHABWidget openHABWidget) {
        return getEntityDataType(openHABWidget, null, EntityDataTypeSource.UNIT);
    }

    @Override
    public UnitEntityDataType<?> getStaticEntityDataType(OpenHABWidget openHABWidget, String staticValue) {
        return getEntityDataType(openHABWidget, staticValue, EntityDataTypeSource.STATIC);
    }
}
