package com.zenit.habclient.rule;

import com.zenit.habclient.rule.IUnitEntityDataType;
import com.zenit.habclient.rule.RuleUnitEntity;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule {

    protected RuleUnitEntity mDataSource;
    //protected List<IRuleAction> mActions;//OpenHABNFCActionList, Intent writeTagIntent

    public Rule() {
        this("New Rule");
    }

    public Rule(String name) {
        mDataSource = new RuleUnitEntity(name);
    }

    public RuleUnitEntity getOperation() {
        return mDataSource;
    }

    public void setDataSource(RuleUnitEntity dataSource) {
        mDataSource = dataSource;
    }

    public String getName() {
        return mDataSource.getName();
    }

    public void setName(String name) {
        mDataSource.setName(name);
    }
}
