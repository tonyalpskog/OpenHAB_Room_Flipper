package org.openhab.habclient.rule2.operators;

public class MoreThanNode extends OperatorNode {
    @Override
    public String toString() {
        return getLeft() + " > " + getRight();
    }
}
