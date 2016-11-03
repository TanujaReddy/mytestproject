package org.strut.amway.core.json.parser.monthnavigation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.strut.amway.core.model.MonthNavigationModel;
import org.strut.amway.core.util.DateTimeUtils;

public class MonthNavigationJsonParserTest {

    MonthNavigationJsonParser classUnderTest;

    @Mock
    ResourceResolver resourceResolver;

    @Before
    public void init() {
        classUnderTest = new MonthNavigationJsonParser();
    }

    @Test
    public void shouldParseJsonSuccessfully() throws Exception {
        int offsetClientTZ = 3;
        final List<MonthNavigationModel> datas = new ArrayList<MonthNavigationModel>();
        final DateTime january = new DateTime(2014, 1, 24, 5, 5, 5, 5, DateTimeZone.forOffsetHours(offsetClientTZ));
        final MonthNavigationModel januaryModel = new MonthNavigationModel("JAN", DateTimeUtils.getDateTimeRangeByServerTimeOf(january));
        final DateTime february = new DateTime(2014, 2, 10, 5, 5, 5, 5, DateTimeZone.forOffsetHours(offsetClientTZ));
        final MonthNavigationModel februaryModel = new MonthNavigationModel("FEB", DateTimeUtils.getDateTimeRangeByServerTimeOf(february));
        final DateTime march = new DateTime(2014, 3, 8, 5, 5, 5, 5, DateTimeZone.forOffsetHours(offsetClientTZ));
        final MonthNavigationModel marchModel = new MonthNavigationModel("MAR", DateTimeUtils.getDateTimeRangeByServerTimeOf(march));
        datas.add(januaryModel);
        datas.add(februaryModel);
        datas.add(marchModel);

        final String result = classUnderTest.parse(resourceResolver, datas);

        // expect for January
        final DateTime firstDayOfJanuary = new DateTime(2014, 1, 1, 0, 0, 0, 0, DateTimeZone.forOffsetHours(offsetClientTZ));
        final DateTime lastDayOfJanuary = new DateTime(2014, 1, 31, 23, 59, 59, 999, DateTimeZone.forOffsetHours(offsetClientTZ));

        // expect for February
        final DateTime firstDayOfFebruary = new DateTime(2014, 2, 1, 0, 0, 0, 0, DateTimeZone.forOffsetHours(offsetClientTZ));
        final DateTime lastDayOfFebruary = new DateTime(2014, 2, 28, 23, 59, 59, 999, DateTimeZone.forOffsetHours(offsetClientTZ));

        // expect for March
        final DateTime firstDayOfMarch = new DateTime(2014, 3, 1, 0, 0, 0, 0, DateTimeZone.forOffsetHours(offsetClientTZ));
        final DateTime lastDayOfMarch = new DateTime(2014, 3, 31, 23, 59, 59, 999, DateTimeZone.forOffsetHours(offsetClientTZ));

        final DateTimeZone serverTimeZone = new DateTime().getZone();

        assertEquals(
                "{\"months\":[{\"label\":\"JAN\",\"startDate\":\"" + firstDayOfJanuary.withZone(serverTimeZone) + "\",\"endDate\":\""
                        + lastDayOfJanuary.withZone(serverTimeZone) + "\"},{\"label\":\"FEB\",\"startDate\":\"" + firstDayOfFebruary.withZone(serverTimeZone)
                        + "\",\"endDate\":\"" + lastDayOfFebruary.withZone(serverTimeZone) + "\"},{\"label\":\"MAR\",\"startDate\":\""
                        + firstDayOfMarch.withZone(serverTimeZone) + "\",\"endDate\":\"" + lastDayOfMarch.withZone(serverTimeZone) + "\"}]}", result);
    }

    @Test
    public void shouldParseJsonInCaseOfNotFoundMessageForI18n() throws Exception {
        final List<MonthNavigationModel> datas = new ArrayList<MonthNavigationModel>();
        final String label = null;
        final MonthNavigationModel notfound = new MonthNavigationModel(label, null);
        datas.add(notfound);

        final String result = classUnderTest.parse(resourceResolver, datas);

        assertEquals("{\"months\":[{\"label\":\"Not Found\"}]}", result);
    }

}
