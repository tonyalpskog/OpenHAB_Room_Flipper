package org.openhab.domain.rule2.validators;

import org.junit.Test;
import org.openhab.domain.rule2.values.BooleanNode;
import org.openhab.domain.rule2.values.DateNode;
import org.openhab.domain.rule2.values.NumberNode;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EqualNodeTest {
    @Test
    public void validate_givenTwoDifferentNumbers_returnFalse() {
        final NumberNode first = new NumberNode(3);
        final NumberNode second = new NumberNode(4);

        final EqualNode<Number> equalNode = new EqualNode<Number>(first, second);

        assertThat(equalNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenSameNumbers_returnTrue() {
        final NumberNode first = new NumberNode(35.5f);
        final NumberNode second = new NumberNode(35.5f);

        final EqualNode<Number> equalNode = new EqualNode<Number>(first, second);

        assertThat(equalNode.validate(), is(equalTo(true)));
    }

    @Test
    public void validate_givenDifferentDates_returnFalse() {
        final DateNode first = new DateNode(new Date(123));
        final DateNode second = new DateNode(new Date(1234));

        final EqualNode<Date> equalNode = new EqualNode<Date>(first, second);

        assertThat(equalNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenSameDates_returnTrue() {
        final DateNode first = new DateNode(new Date(123));
        final DateNode second = new DateNode(new Date(123));

        final EqualNode<Date> equalNode = new EqualNode<Date>(first, second);

        assertThat(equalNode.validate(), is(equalTo(true)));
    }

    @Test
    public void validate_givenDifferentBooleans_returnFalse() {
        final BooleanNode first = new BooleanNode(false);
        final BooleanNode second = new BooleanNode(true);

        final EqualNode<Boolean> equalNode = new EqualNode<Boolean>(first, second);

        assertThat(equalNode.validate(), is(equalTo(false)));
    }

    @Test
    public void validate_givenSameBooleans_returnTrue() {
        final BooleanNode first = new BooleanNode(false);
        final BooleanNode second = new BooleanNode(false);

        final EqualNode<Boolean> equalNode = new EqualNode<Boolean>(first, second);

        assertThat(equalNode.validate(), is(equalTo(true)));
    }
}
