package com.zenit.habclient.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRuleActivity {
    public String getRuleName();
    public void setRuleName(String name);
    public Rule getRule();
    public void setRule(Rule rule);
}
