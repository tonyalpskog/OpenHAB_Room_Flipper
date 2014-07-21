package org.openhab.domain.rule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.DocumentFactory;
import org.openhab.domain.IDocumentFactory;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.domain.PopularNameProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.RegularExpression;

import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTest {
    private IOpenHABWidgetProvider mWidgetProvider;

    @Before
    public void setUp() throws Exception {
        final ILogger logger = mock(ILogger.class);
        final IColorParser colorParser = mock(IColorParser.class);
        final RegularExpression regularExpression = new RegularExpression();
        final IPopularNameProvider popularNameProvider = new PopularNameProvider();
        mWidgetProvider = new OpenHABWidgetProvider(regularExpression, logger, popularNameProvider);
        final IDocumentFactory documentFactory = new DocumentFactory();
        final HttpDataSetup httpDataSetup = new HttpDataSetup(logger, colorParser, documentFactory);
        mWidgetProvider.setOpenHABWidgets(httpDataSetup.loadTestData());

    }

    //================================= UNITS ===================================

    @Test
    public void testGetWidgetById() {
        OpenHABWidget unit = mWidgetProvider.getWidgetByID("GF_Kitchen_0");
        Assert.assertEquals("Ceiling", unit.getLabel());
        Assert.assertEquals("Light_GF_Kitchen_Ceiling", unit.getItem().getName());
    }

    @Test
    public void testMathEpsilonDiff() {
        final double epsilon = 8E-7f;
        final Float f = 34.7f;
        final Double d = 34.7;
        assertThat(Math.abs(f.doubleValue() - d), is(lessThan(epsilon)));
        assertThat(Math.abs(d - f.doubleValue()), is(lessThan(epsilon)));
    }

    @Test
    public void test_Verify_value_in_UnitEntityDataType() {
        UnitEntityDataType rue = new UnitEntityDataType<Double>("Test Value", 50.7d, Double.class);
        Assert.assertTrue(rue.getValue() != null);
        Assert.assertEquals(50.7, rue.getValue());
        Assert.assertEquals("Test Value", rue.getName());
    }




}
