package org.openhab.habclient.rule2.operators;

import org.openhab.habclient.rule2.Node;

public class AndNode<T extends Node> extends OperatorNode {
    @Override
    public String toString() {
        return getLeft() + " AND " + getRight();
    }
}
