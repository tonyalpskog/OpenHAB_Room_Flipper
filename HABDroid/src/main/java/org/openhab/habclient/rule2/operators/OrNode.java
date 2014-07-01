package org.openhab.habclient.rule2.operators;

public class OrNode extends OperatorNode {
    @Override
    public String toString() {
        return getLeft() + " OR " + getRight();
    }
}
