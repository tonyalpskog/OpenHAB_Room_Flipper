package com.zenit.habclient.rule;

import com.zenit.habclient.rule.IUnitEntityDataType;
import com.zenit.habclient.rule.RuleUnitEntity;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule {

    private RuleUnitEntity<Boolean> mDataSource;
    private String mName;

    public Rule() {
        mDataSource = new RuleUnitEntity<Boolean>("New Rule") {

            public Boolean getValue() {
                return Boolean.valueOf(mDataSource.getOperation().getResult());
            }

            public String getFormattedString(){
                return getValue()? "On": "Off";//TODO - Language independent
            }

            @Override
            public Boolean valueOf(String input) {
                return Boolean.valueOf(input);
            }
        };
    }

    public IUnitEntityDataType<Boolean> getRuleDataSource() {
        return mDataSource;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
