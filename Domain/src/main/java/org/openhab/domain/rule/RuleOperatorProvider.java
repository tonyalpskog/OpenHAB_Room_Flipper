package org.openhab.domain.rule;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.operators.AfterDateTimeRuleOperator;
import org.openhab.domain.rule.operators.AfterOrEqualDateTimeRuleOperator;
import org.openhab.domain.rule.operators.AndLogicBooleanRuleOperator;
import org.openhab.domain.rule.operators.BeforeOrEqualDateTimeRuleOperator;
import org.openhab.domain.rule.operators.BetweenDateTimeRuleOperator;
import org.openhab.domain.rule.operators.BetweenNumberRuleOperator;
import org.openhab.domain.rule.operators.EqualBooleanRuleOperator;
import org.openhab.domain.rule.operators.EqualDateTimeRuleOperator;
import org.openhab.domain.rule.operators.EqualDateTimeStringRuleOperator;
import org.openhab.domain.rule.operators.EqualLogicBooleanRuleOperator;
import org.openhab.domain.rule.operators.EqualNumberRuleOperator;
import org.openhab.domain.rule.operators.LessOrEqualNumberRuleOperator;
import org.openhab.domain.rule.operators.LessThanNumberRuleOperator;
import org.openhab.domain.rule.operators.MoreOrEqualNumberRuleOperator;
import org.openhab.domain.rule.operators.MoreThanNumberRuleOperator;
import org.openhab.domain.rule.operators.NotEqualBooleanRuleOperator;
import org.openhab.domain.rule.operators.NotEqualDateTimeRuleOperator;
import org.openhab.domain.rule.operators.NotEqualDateTimeStringRuleOperator;
import org.openhab.domain.rule.operators.NotEqualLogicBooleanRuleOperator;
import org.openhab.domain.rule.operators.NotEqualNumberRuleOperator;
import org.openhab.domain.rule.operators.OrLogicBooleanRuleOperator;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.domain.rule.operators.WithinDateTimeRuleOperator;
import org.openhab.domain.rule.operators.WithinNumberRuleOperator;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperatorProvider implements IRuleOperatorProvider {
    private HashMap<Class<?>, HashMap<RuleOperatorType, ?>> mOperatorHash;

    @Inject
    public RuleOperatorProvider() {
        mOperatorHash = new HashMap<Class<?>, HashMap<RuleOperatorType, ?>>();

        createLogicOperators();
    }

    private void createLogicOperators() {
        mOperatorHash.put(Number.class, createNumericLogicOperators());
        mOperatorHash.put(LogicBoolean.class, createLogicBooleanLogicOperators());
        mOperatorHash.put(Boolean.class, createBooleanLogicOperators());
        mOperatorHash.put(String.class, createDateTimeStringLogicOperators());
        mOperatorHash.put(Date.class, createDateLogicOperators());
    }

    private HashMap<RuleOperatorType, RuleOperator<Number>> createNumericLogicOperators() {
        final HashMap<RuleOperatorType, RuleOperator<Number>> numberOperatorHash = new HashMap<RuleOperatorType, RuleOperator<Number>>();

        final RuleOperator<Number> equalNum = new EqualNumberRuleOperator();
        numberOperatorHash.put(equalNum.getType(), equalNum);

        final RuleOperator<Number> notEqualNum = new NotEqualNumberRuleOperator();
        numberOperatorHash.put(notEqualNum.getType(), notEqualNum);

        final RuleOperator<Number> lessThan = new LessThanNumberRuleOperator();
        numberOperatorHash.put(lessThan.getType(), lessThan);

        final RuleOperator<Number> moreThan = new MoreThanNumberRuleOperator();
        numberOperatorHash.put(moreThan.getType(), moreThan);

        final RuleOperator<Number> lessOrEqual = new LessOrEqualNumberRuleOperator();
        numberOperatorHash.put(lessOrEqual.getType(), lessOrEqual);

        final RuleOperator<Number> moreOrEqual = new MoreOrEqualNumberRuleOperator();
        numberOperatorHash.put(moreOrEqual.getType(), moreOrEqual);

        final RuleOperator<Number> between = new BetweenNumberRuleOperator();
        numberOperatorHash.put(between.getType(), between);

        final RuleOperator<Number> within = new WithinNumberRuleOperator();
        numberOperatorHash.put(within.getType(), within);

        return numberOperatorHash;
    }

    private HashMap<RuleOperatorType, RuleOperator<LogicBoolean>> createLogicBooleanLogicOperators() {
        final HashMap<RuleOperatorType, RuleOperator<LogicBoolean>> logicBooleanOperatorHash = new HashMap<RuleOperatorType, RuleOperator<LogicBoolean>>();

        final RuleOperator<LogicBoolean> orOperator = new OrLogicBooleanRuleOperator();
        logicBooleanOperatorHash.put(orOperator.getType(), orOperator);

        final RuleOperator<LogicBoolean> andOperator = new AndLogicBooleanRuleOperator();
        logicBooleanOperatorHash.put(andOperator.getType(), andOperator);

        final RuleOperator<LogicBoolean> equalBool = new EqualLogicBooleanRuleOperator();
        logicBooleanOperatorHash.put(equalBool.getType(), equalBool);

        final RuleOperator<LogicBoolean> notEqualBool = new NotEqualLogicBooleanRuleOperator();
        logicBooleanOperatorHash.put(notEqualBool.getType(), notEqualBool);

        return logicBooleanOperatorHash;
    }

    private HashMap<RuleOperatorType, RuleOperator<Boolean>> createBooleanLogicOperators() {
        final HashMap<RuleOperatorType, RuleOperator<Boolean>> booleanOperatorHash = new HashMap<RuleOperatorType, RuleOperator<Boolean>>();

        final RuleOperator<Boolean> equalBool = new EqualBooleanRuleOperator();
        booleanOperatorHash.put(equalBool.getType(), equalBool);

        final RuleOperator<Boolean> notEqualBool = new NotEqualBooleanRuleOperator();
        booleanOperatorHash.put(notEqualBool.getType(), notEqualBool);

        return booleanOperatorHash;
    }

    private HashMap<RuleOperatorType, RuleOperator<String>> createDateTimeStringLogicOperators() {
        final HashMap<RuleOperatorType, RuleOperator<String>> stringOperatorHash = new HashMap<RuleOperatorType, RuleOperator<String>>();

        final RuleOperator<String> equalString = new EqualDateTimeStringRuleOperator();
        stringOperatorHash.put(equalString.getType(), equalString);

        final RuleOperator<String> notEqualString = new NotEqualDateTimeStringRuleOperator();
        stringOperatorHash.put(notEqualString.getType(), equalString);

        return stringOperatorHash;
    }

    private HashMap<RuleOperatorType, RuleOperator<java.util.Date>> createDateLogicOperators() {
        final HashMap<RuleOperatorType, RuleOperator<java.util.Date>> dateOperatorHash = new HashMap<RuleOperatorType, RuleOperator<java.util.Date>>();

        final RuleOperator<Date> equalDate = new EqualDateTimeRuleOperator();
        dateOperatorHash.put(equalDate.getType(), equalDate);

        final RuleOperator<Date> notEqualDate = new NotEqualDateTimeRuleOperator();
        dateOperatorHash.put(notEqualDate.getType(), notEqualDate);

        final RuleOperator<Date> beforeDate = new BeforeOrEqualDateTimeRuleOperator();
        dateOperatorHash.put(beforeDate.getType(), beforeDate);

        final RuleOperator<Date> afterDate = new AfterDateTimeRuleOperator();
        dateOperatorHash.put(afterDate.getType(), afterDate);

        final RuleOperator<Date> beforeOrEqualDate = new BeforeOrEqualDateTimeRuleOperator();
        dateOperatorHash.put(beforeOrEqualDate.getType(), beforeOrEqualDate);

        final RuleOperator<Date> afterOrEqualDate = new AfterOrEqualDateTimeRuleOperator();
        dateOperatorHash.put(afterOrEqualDate.getType(), afterOrEqualDate);

        final RuleOperator<Date> betweenDate = new BetweenDateTimeRuleOperator();
        dateOperatorHash.put(betweenDate.getType(), betweenDate);

        final RuleOperator<Date> withinDate = new WithinDateTimeRuleOperator();
        dateOperatorHash.put(withinDate.getType(), withinDate);

        return dateOperatorHash;
    }

    @Override
    public RuleOperator<?> getRuleOperator(OpenHABWidget openHABWidget, RuleOperatorType type) {
        if(openHABWidget == null)
            return null;

        switch(openHABWidget.getItem().getType()) {
            case Contact:
            case Switch:
                return getRuleOperator(Boolean.class, type);
            case Number:
            case Dimmer:
            case Rollershutter:
                return getRuleOperator(Number.class, type);
            case String:
            case Color:
                return getRuleOperator(String.class, type);
            default:
                return null;
        }
    }

    @Override
    public RuleOperator<?> getRuleOperator(Class<?> operandClass, RuleOperatorType type) {
        if(operandClass == null) throw new IllegalArgumentException("operandClass is null");
        if(type == null) throw new IllegalArgumentException("type is null");

        final HashMap<RuleOperatorType, ?> hashMap = mOperatorHash.get(operandClass);
        if(hashMap == null)
            return null;

        return (RuleOperator<?>) hashMap.get(type);
    }

    @Override
    public Set<RuleOperatorType> getRuleOperatorTypes(Class<?> operandClass) {
        final HashMap<RuleOperatorType, ?> hashMap = mOperatorHash.get(operandClass);
        if(hashMap == null)
            return null;

        return hashMap.keySet();
    }
}
