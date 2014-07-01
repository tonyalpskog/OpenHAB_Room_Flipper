package org.openhab.domain.rule2.validators;

import org.junit.Test;
import org.openhab.domain.rule2.values.BooleanNode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AndNodeTest {
    @Test
    public void validate_givenTrueAndFalse_returnFalse() {
        final BooleanNode first = new BooleanNode(true);
        final BooleanNode second = new BooleanNode(false);

        final AndNode andNode = new AndNode(first, second);

        assertThat(andNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenFalseAndTrue_returnFalse() {
        final BooleanNode first = new BooleanNode(false);
        final BooleanNode second = new BooleanNode(true);

        final AndNode andNode = new AndNode(first, second);

        assertThat(andNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenOnlyFalse_returnFalse() {
        final BooleanNode first = new BooleanNode(false);
        final BooleanNode second = new BooleanNode(false);

        final AndNode andNode = new AndNode(first, second);

        assertThat(andNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenOnlyTrue_returnTrue() {
        final BooleanNode first = new BooleanNode(true);
        final BooleanNode second = new BooleanNode(true);

        final AndNode andNode = new AndNode(first, second);

        assertThat(andNode.validate(), is(equalTo(true)));
    }
}
