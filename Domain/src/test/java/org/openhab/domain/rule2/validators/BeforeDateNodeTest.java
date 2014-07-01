package org.openhab.domain.rule2.validators;

import org.junit.Test;
import org.openhab.domain.rule2.values.DateNode;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class BeforeDateNodeTest {
    @Test
    public void validate_givenDateBefore_returnTrue() {
        final DateNode first = new DateNode(new Date(1234));
        final DateNode second = new DateNode(new Date(12345));

        final BeforeDateNode afterDateNode = new BeforeDateNode(first, second);

        assertThat(afterDateNode.validate(), is(equalTo(true)));
    }

    @Test
    public void validate_givenDateEqual_returnFalse() {
        final DateNode first = new DateNode(new Date(1234));
        final DateNode second = new DateNode(new Date(1234));

        final BeforeDateNode afterDateNode = new BeforeDateNode(first, second);

        assertThat(afterDateNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenDateAfter_returnFalse() {
        final DateNode first = new DateNode(new Date(12345));
        final DateNode second = new DateNode(new Date(1234));

        final BeforeDateNode afterDateNode = new BeforeDateNode(first, second);

        assertThat(afterDateNode.validate(), is(equalTo(false)));
    }
}
