package org.openhab.habclient.rule2.operators;

import org.openhab.habclient.rule2.Node;

public abstract class OperatorNode implements Node {
    private Node mLeft;
    private Node mRight;

    public Node getLeft() {
        return mLeft;
    }

    public Node getRight() {
        return mRight;
    }

    public void setLeft(Node left) {
        mLeft = left;
    }

    public void setRight(Node right) {
        mRight = right;
    }

    @Override
    public boolean isComplete() {
        return mLeft != null && mRight != null;
    }
}
