package org.openhab.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRuleEditActivity {
    public String getRuleName();
    public void setRuleName(String name);
    public Rule getRule();
    public void setRule(Rule rule);
}
