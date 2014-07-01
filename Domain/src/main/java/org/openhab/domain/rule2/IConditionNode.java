package org.openhab.domain.rule2;

public interface IConditionNode extends INode<Boolean> {
    Boolean evaluate();
}
