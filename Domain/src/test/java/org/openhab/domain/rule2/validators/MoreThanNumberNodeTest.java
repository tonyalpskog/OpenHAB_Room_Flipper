package org.openhab.domain.rule2.validators;

import org.junit.Test;
import org.openhab.domain.rule2.values.NumberNode;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class MoreThanNumberNodeTest {
    @Test
    public void validate_givenNumberIsBigger_returnTrue() {
        final NumberNode first = new NumberNode(0.123123d);
        final NumberNode second = new NumberNode(0);

        final MoreThanNumberNode lessThanNumberNode = new MoreThanNumberNode(first, second);
        assertThat(lessThanNumberNode.validate(), is(equalTo(true)));
    }

    @Test
    public void validate_givenNumberIsSmaller_returnFalse() {
        final NumberNode first = new NumberNode(4);
        final NumberNode second = new NumberNode(12312.3f);

        final MoreThanNumberNode lessThanNumberNode = new MoreThanNumberNode(first, second);
        assertThat(lessThanNumberNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenNumberIsSame_returnFalse() {
        final NumberNode first = new NumberNode(42);
        final NumberNode second = new NumberNode(42);

        final MoreThanNumberNode lessThanNumberNode = new MoreThanNumberNode(first, second);
        assertThat(lessThanNumberNode.validate(), is(equalTo(false)));
    }
}
