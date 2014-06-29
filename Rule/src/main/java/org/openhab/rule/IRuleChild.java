package org.openhab.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRuleChild {
    public String getName();
    public void setName(String name);
    public String getDescription();
    public void setDescription(String description);
}
