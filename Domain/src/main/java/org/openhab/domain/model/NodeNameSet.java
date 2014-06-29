package org.openhab.domain.model;

import java.util.EnumSet;

/**
 * Created by Tony Alpskog in 2014.
 */
public class NodeNameSet {
    /*public static EnumSet<NodeName> UnitItem = EnumSet.of(NodeName., NodeName.DAMAGED, NodeName.NOT_IN_CONFORMITY_DISCREPANCY
            , NodeName.MISSING_PACKAGE, NodeName.INSUFFICIENT_PACKAGING
            , NodeName.MISSING_INCORRECT_DOCUMENTS, NodeName.WITHOUT_SIGNATURE);

    public static EnumSet<NodeName> NavigationItem = EnumSet.of(NodeName.ADDRESS_CLOSED_NOT_HOME
            , NodeName.DAMAGED, NodeName.WRONG_ADDRESS, NodeName.REFUSED_NO_REASON
            , NodeName.INSUFFICIENT_PACKAGING, NodeName.MISSING_INCORRECT_DOCUMENTS
            , NodeName.WEATHER_CONDITIONS, NodeName.NOT_READY, NodeName.QUEUE_AT_CUSTOMERS_CONGESTION);

    public static EnumSet<NodeName> WidgetItem = EnumSet.of(NodeName.IN_CONFORMITY, NodeName.DAMAGED, NodeName.NOT_IN_CONFORMITY_DISCREPANCY
            , NodeName.MISSING_PACKAGE, NodeName.INSUFFICIENT_PACKAGING
            , NodeName.MISSING_INCORRECT_DOCUMENTS, NodeName.WITHOUT_SIGNATURE);

    public static EnumSet<NodeName> All = EnumSet.of(NodeName.IN_CONFORMITY, NodeName.ADDRESS_CLOSED_NOT_HOME
            , NodeName.DAMAGED, NodeName.NOT_IN_CONFORMITY_DISCREPANCY, NodeName.WRONG_ADDRESS
            , NodeName.MISSING_PACKAGE, NodeName.REFUSED_NO_REASON
            , NodeName.INSUFFICIENT_PACKAGING
            , NodeName.MECHANICAL_BREAKDOWN, NodeName.MISSING_INCORRECT_DOCUMENTS
            , NodeName.VISIT_REARRANGED, NodeName.NOT_LOADED
            , NodeName.REFUSED_NO_PAYMENT_COD, NodeName.UNSUCCESSFUL_VISIT_ATTEMPT
            , NodeName.DELAYED_OUT_OF_TIME_SCHEDULE_CHANGED, NodeName.UNDEFINED_REASON_DRIVER
            , NodeName.WEATHER_CONDITIONS, NodeName.NOT_READY
            , NodeName.QUEUE_AT_CUSTOMERS_CONGESTION, NodeName.WITHOUT_SIGNATURE);*/

    public NodeName[] getNodeName(EnumSet<NodeName> enumSet) {
        return enumSet.toArray(new NodeName[0]);
    }
}