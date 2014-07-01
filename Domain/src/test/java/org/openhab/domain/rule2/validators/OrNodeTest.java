package org.openhab.domain.rule2.validators;

import org.junit.Test;
import org.openhab.domain.rule2.values.BooleanNode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OrNodeTest {
    @Test
    public void validate_givenTrueAndFalse_returnTrue() {
        final BooleanNode first = new BooleanNode(true);
        final BooleanNode second = new BooleanNode(false);

        final OrNode andNode = new OrNode(first, second);

        assertThat(andNode.validate(), is(equalTo(true)));
    }

    @Test
    public void validate_givenFalseAndTrue_returnTrue() {
        final BooleanNode first = new BooleanNode(false);
        final BooleanNode second = new BooleanNode(true);

        final OrNode andNode = new OrNode(first, second);

        assertThat(andNode.validate(), is(equalTo(true)));
    }

    @Test
    public void validate_givenOnlyFalse_returnFalse() {
        final BooleanNode first = new BooleanNode(false);
        final BooleanNode second = new BooleanNode(false);

        final OrNode andNode = new OrNode(first, second);

        assertThat(andNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenOnlyTrue_returnTrue() {
        final BooleanNode first = new BooleanNode(true);
        final BooleanNode second = new BooleanNode(true);

        final OrNode andNode = new OrNode(first, second);

        assertThat(andNode.validate(), is(equalTo(true)));
    }
}
