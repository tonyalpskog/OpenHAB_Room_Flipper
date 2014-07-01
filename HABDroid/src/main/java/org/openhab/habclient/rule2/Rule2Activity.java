package org.openhab.habclient.rule2;

import android.app.Activity;
import android.os.Bundle;

import org.openhab.habclient.rule2.operators.EqualNode;
import org.openhab.habclient.rule2.operators.AndNode;
import org.openhab.habclient.rule2.values.BooleanValueNode;
import org.openhab.habclient.rule2.values.NumberValueNode;

public class Rule2Activity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndNode andNode = new AndNode();

        NumberValueNode valueNode = new NumberValueNode();
        valueNode.setValue(3);

        NumberValueNode valueNode2 = new NumberValueNode();
        valueNode.setValue(123);

        EqualNode equalNode = new EqualNode();
        equalNode.setLeft(valueNode);
        equalNode.setRight(valueNode2);

        andNode.setLeft(valueNode);

        BooleanValueNode booleanValueNode = new BooleanValueNode();
        booleanValueNode.setValue(true);
        andNode.setRight(booleanValueNode);
    }
}
