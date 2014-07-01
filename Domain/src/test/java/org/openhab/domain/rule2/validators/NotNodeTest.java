package org.openhab.domain.rule2.validators;

import org.junit.Test;
import org.openhab.domain.rule2.values.BooleanNode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NotNodeTest {
    @Test
    public void validate_givenTrue_returnFalse() {
        final BooleanNode booleanNode = new BooleanNode(true);
        final NotNode notNode = new NotNode(booleanNode);
        assertThat(notNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenFalse_returnTrue() {
        final BooleanNode booleanNode = new BooleanNode(false);
        final NotNode notNode = new NotNode(booleanNode);
        assertThat(notNode.validate(), is(equalTo(true)));
    }
}
