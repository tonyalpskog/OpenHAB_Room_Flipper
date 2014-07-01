package org.openhab.domain.rule2.conditions;

import org.junit.Test;
import org.openhab.domain.rule2.values.DateNode;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AfterDateNodeTest {
    @Test
    public void validate_givenDateBefore_returnFalse() {
        final DateNode first = new DateNode(new Date(1234));
        final DateNode second = new DateNode(new Date(12345));

        final AfterDateNode afterDateNode = new AfterDateNode(first, second);

        assertThat(afterDateNode.evaluate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenDateEqual_returnFalse() {
        final DateNode first = new DateNode(new Date(1234));
        final DateNode second = new DateNode(new Date(1234));

        final AfterDateNode afterDateNode = new AfterDateNode(first, second);

        assertThat(afterDateNode.evaluate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenDateAfter_returnTrue() {
        final DateNode first = new DateNode(new Date(12345));
        final DateNode second = new DateNode(new Date(1234));

        final AfterDateNode afterDateNode = new AfterDateNode(first, second);

        assertThat(afterDateNode.evaluate(), is(equalTo(true)));
    }
}
