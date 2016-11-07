package org.strut.amway.core.servlets.monthnavigation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.Property;

import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.enumeration.MonthMessageKey;
import org.strut.amway.core.model.DateTimeRange;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.DateTimeUtils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

public class MonthNavigationServletTest {

    MonthNavigationServlet classUnderTest;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Mock
    Resource resource;

    ByteArrayOutputStream outputStream;
    ResourceBundle enResourceBundle;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        createMonthNavigationServlet();

        outputStream = new ByteArrayOutputStream(1024);
        PrintWriter writer = new PrintWriter(outputStream);

        enResourceBundle = createResourceBundleForEnglish();
        when(slingHttpServletRequest.getMethod()).thenReturn("GET");
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);
    }

    @Test
    public void shouldReturnMonthNavigationForOneMonthAway() throws Exception {
        final String clientTime = "2014-11-10T15:11:55.11+03:00";
        final DateTime parsedClientTime = DateTimeUtils.parseClientTimeWithClientTZ(clientTime);
        final DateTime currentServerTime = new DateTime();
        final GregorianCalendar createdDateHomePage = currentServerTime.toGregorianCalendar();

        final Page currentPage = Mockito.mock(Page.class);
        mockParentPage(currentPage, createdDateHomePage);
        when(currentPage.getLanguage(false)).thenReturn(Locale.ENGLISH);
        when(slingHttpServletRequest.getParameter(eq("clientTime"))).thenReturn(clientTime);
        when(slingHttpServletRequest.getResourceBundle(eq(Locale.ENGLISH))).thenReturn(enResourceBundle);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(resource.adaptTo(Page.class)).thenReturn(currentPage);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final DateTime currentServerTimeWithClientTz = new DateTime().withZone(parsedClientTime.getZone());
        final String label = enResourceBundle.getString(MonthMessageKey.resolve(currentServerTimeWithClientTz.getMonthOfYear()).getMessageKey());
        final DateTimeRange dateTimeRange = DateTimeUtils.getDateTimeRangeByServerTimeOf(currentServerTimeWithClientTz);
        final DateTime startTime = dateTimeRange.getStartTime();
        final DateTime endTime = dateTimeRange.getEndTime();
        assertEquals("{\"months\":[{\"label\":\"" + label + "\",\"startDate\":\"" + startTime + "\",\"endDate\":\"" + endTime + "\"}]}",
                outputStream.toString());
    }

    @Test
    public void shouldReturnMonthNavigationForTwoMonthAway() throws Exception {
        final String clientTime = "2014-11-10T15:11:55.11+03:00";
        final DateTime parsedClientTime = DateTimeUtils.parseClientTimeWithClientTZ(clientTime);
        final DateTime currentServerTime = new DateTime();
        final GregorianCalendar createdDateHomePage = currentServerTime.minusMonths(1).toGregorianCalendar();
        
        final Page currentPage = Mockito.mock(Page.class);
        mockParentPage(currentPage, createdDateHomePage);
        when(currentPage.getLanguage(false)).thenReturn(Locale.ENGLISH);
        when(slingHttpServletRequest.getParameter(eq("clientTime"))).thenReturn(clientTime);
        when(slingHttpServletRequest.getResourceBundle(eq(Locale.ENGLISH))).thenReturn(enResourceBundle);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(resource.adaptTo(Page.class)).thenReturn(currentPage);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final DateTime currentServerTimeWithClientTz = new DateTime().withZone(parsedClientTime.getZone());
        final DateTime month1 = currentServerTimeWithClientTz;
        final DateTimeRange month1Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month1);
        String month1Label = enResourceBundle.getString(MonthMessageKey.resolve(month1.getMonthOfYear()).getMessageKey());
        final DateTime month2 = month1.minusMonths(1);
        final DateTimeRange month2Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month2);
        String month2Label = enResourceBundle.getString(MonthMessageKey.resolve(month2.getMonthOfYear()).getMessageKey());
        assertEquals(
                "{\"months\":[{\"label\":\"" + month1Label + "\",\"startDate\":\"" + month1Range.getStartTime() + "\",\"endDate\":\""
                        + month1Range.getEndTime() + "\"},{\"label\":\"" + month2Label + "\",\"startDate\":\"" + month2Range.getStartTime()
                        + "\",\"endDate\":\"" + month2Range.getEndTime() + "\"}]}", outputStream.toString());
    }

    @Test
    public void shouldReturnMonthNavigationWhenMaximumValueIsReached() throws Exception {
        final String clientTime = "2014-11-10T15:11:55.11+02:00";
        final DateTime parsedClientTime = DateTimeUtils.parseClientTimeWithClientTZ(clientTime);
        final DateTime currentServerTime = new DateTime();
        final GregorianCalendar createdDateHomePage = currentServerTime.minusMonths(7).toGregorianCalendar();

        final Page currentPage = Mockito.mock(Page.class);
        mockParentPage(currentPage, createdDateHomePage);
        when(currentPage.getLanguage(false)).thenReturn(Locale.ENGLISH);
        when(slingHttpServletRequest.getParameter(eq("clientTime"))).thenReturn(clientTime);
        when(slingHttpServletRequest.getResourceBundle(eq(Locale.ENGLISH))).thenReturn(enResourceBundle);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(resource.adaptTo(Page.class)).thenReturn(currentPage);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final DateTime currentServerTimeWithClientTz = new DateTime().withZone(parsedClientTime.getZone());
        final DateTime month1 = currentServerTimeWithClientTz;
        final DateTimeRange month1Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month1);
        String month1Label = enResourceBundle.getString(MonthMessageKey.resolve(month1.getMonthOfYear()).getMessageKey());

        final DateTime month2 = month1.minusMonths(1);
        final DateTimeRange month2Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month2);
        String month2Label = enResourceBundle.getString(MonthMessageKey.resolve(month2.getMonthOfYear()).getMessageKey());

        final DateTime month3 = month1.minusMonths(2);
        final DateTimeRange month3Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month3);
        String month3Label = enResourceBundle.getString(MonthMessageKey.resolve(month3.getMonthOfYear()).getMessageKey());

        final DateTime month4 = month1.minusMonths(3);
        final DateTimeRange month4Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month4);
        String month4Label = enResourceBundle.getString(MonthMessageKey.resolve(month4.getMonthOfYear()).getMessageKey());

        final DateTime month5 = month1.minusMonths(4);
        final DateTimeRange month5Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month5);
        String month5Label = enResourceBundle.getString(MonthMessageKey.resolve(month5.getMonthOfYear()).getMessageKey());

        final DateTime month6 = month1.minusMonths(5);
        final DateTimeRange month6Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month6);
        String month6Label = enResourceBundle.getString(MonthMessageKey.resolve(month6.getMonthOfYear()).getMessageKey());

        assertEquals(
                "{\"months\":[{\"label\":\"" + month1Label + "\",\"startDate\":\"" + month1Range.getStartTime() + "\",\"endDate\":\""
                        + month1Range.getEndTime() + "\"},{\"label\":\"" + month2Label + "\",\"startDate\":\"" + month2Range.getStartTime()
                        + "\",\"endDate\":\"" + month2Range.getEndTime() + "\"},{\"label\":\"" + month3Label + "\",\"startDate\":\""
                        + month3Range.getStartTime() + "\",\"endDate\":\"" + month3Range.getEndTime() + "\"},{\"label\":\"" + month4Label
                        + "\",\"startDate\":\"" + month4Range.getStartTime() + "\",\"endDate\":\"" + month4Range.getEndTime() + "\"},{\"label\":\""
                        + month5Label + "\",\"startDate\":\"" + month5Range.getStartTime() + "\",\"endDate\":\"" + month5Range.getEndTime()
                        + "\"},{\"label\":\"" + month6Label + "\",\"startDate\":\"" + month6Range.getStartTime() + "\",\"endDate\":\""
                        + month6Range.getEndTime() + "\"}]}", outputStream.toString());
    }

    @Test
    public void shouldReturnErrorWhenClientTimeParamIsMissing() throws Exception {
        final String clientTime = null;

        when(slingHttpServletRequest.getParameter(eq("clientTime"))).thenReturn(clientTime);
        when(slingHttpServletRequest.getResourceBundle(eq(Locale.ENGLISH))).thenReturn(createResourceBundleForEnglish());

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalStateException: clientTime Param can not be null\"}", outputStream.toString());
    }

    @Test
    public void shouldReturnErrorWhenPageCanNotResolve() throws Exception {
        final String clientTime = "2014-11-10T15:11:55.11+03:00";
        final Page page = null;

        when(slingHttpServletRequest.getParameter(eq("clientTime"))).thenReturn(clientTime);
        when(slingHttpServletRequest.getResourceBundle(eq(Locale.ENGLISH))).thenReturn(createResourceBundleForEnglish());
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(resource.adaptTo(Page.class)).thenReturn(page);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        assertEquals("{\"error\":\"IllegalStateException: Can not resolve Sling Resource to Page from path /content/asia-pac/australia-new-zealand/australia/amway-today/en_au\"}", outputStream.toString());
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldProcessProperlyWhenManuallyChooseInstallationDate() throws Exception {
        final String clientTime = "2014-11-10T15:11:55.11+03:00";
        final DateTime parsedClientTime = DateTimeUtils.parseClientTimeWithClientTZ(clientTime);
        final DateTime currentServerTime = new DateTime();
        final GregorianCalendar createdDateHomePageManual = currentServerTime.minusMonths(1).toGregorianCalendar();

        final Page currentPage = Mockito.mock(Page.class);
        mockParentPage(currentPage, createdDateHomePageManual);

        ValueMap valueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(Constants.INSTALLATION_DATE_PROPERTY, createdDateHomePageManual);
            }
        });
        Page homepage = currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL);
        when(homepage.getProperties()).thenReturn(valueMap);
        when(currentPage.getLanguage(false)).thenReturn(Locale.ENGLISH);
        when(slingHttpServletRequest.getParameter(eq("clientTime"))).thenReturn(clientTime);
        when(slingHttpServletRequest.getResourceBundle(eq(Locale.ENGLISH))).thenReturn(enResourceBundle);
        when(resource.getPath()).thenReturn("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au");
        when(resource.adaptTo(Page.class)).thenReturn(currentPage);

        classUnderTest.service(slingHttpServletRequest, slingHttpServletResponse);

        final DateTime currentServerTimeWithClientTz = new DateTime().withZone(parsedClientTime.getZone());
        final DateTime month1 = currentServerTimeWithClientTz;
        final DateTimeRange month1Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month1);
        String month1Label = enResourceBundle.getString(MonthMessageKey.resolve(month1.getMonthOfYear()).getMessageKey());
        final DateTime month2 = month1.minusMonths(1);
        final DateTimeRange month2Range = DateTimeUtils.getDateTimeRangeByServerTimeOf(month2);
        String month2Label = enResourceBundle.getString(MonthMessageKey.resolve(month2.getMonthOfYear()).getMessageKey());
        assertEquals(
                "{\"months\":[{\"label\":\"" + month1Label + "\",\"startDate\":\"" + month1Range.getStartTime() + "\",\"endDate\":\""
                        + month1Range.getEndTime() + "\"},{\"label\":\"" + month2Label + "\",\"startDate\":\"" + month2Range.getStartTime()
                        + "\",\"endDate\":\"" + month2Range.getEndTime() + "\"}]}", outputStream.toString());
    }

    @Test
    public void shouldDestroyProperly() throws Exception {
        createMonthNavigationServlet();
        classUnderTest.destroy();
    }

    private ResourceBundle createResourceBundleForEnglish() {
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(MonthMessageKey.JANUARY.getMessageKey(), "JAN");
        dataMap.put(MonthMessageKey.FEBRUARY.getMessageKey(), "FEB");
        dataMap.put(MonthMessageKey.MARCH.getMessageKey(), "MAR");
        dataMap.put(MonthMessageKey.APRIL.getMessageKey(), "APR");
        dataMap.put(MonthMessageKey.MAY.getMessageKey(), "MAY");
        dataMap.put(MonthMessageKey.JUNE.getMessageKey(), "JUN");
        dataMap.put(MonthMessageKey.JULY.getMessageKey(), "JUL");
        dataMap.put(MonthMessageKey.AUGUST.getMessageKey(), "AUG");
        dataMap.put(MonthMessageKey.SEPTEMBER.getMessageKey(), "SEP");
        dataMap.put(MonthMessageKey.OCTOBER.getMessageKey(), "OCT");
        dataMap.put(MonthMessageKey.NOVEMBER.getMessageKey(), "NOV");
        dataMap.put(MonthMessageKey.DECEMBER.getMessageKey(), "DEC");

        final DummyResouseBundle resourceBundle = new DummyResouseBundle();
        resourceBundle.setDataMap(dataMap);
        return resourceBundle;
    }

    private Page mockParentPage(final Page page, final GregorianCalendar calendar) throws Exception {
        Page parentPage = Mockito.mock(Page.class);
        Node parentNode = Mockito.mock(Node.class);
        Property property = Mockito.mock(Property.class);
        when(page.getAbsoluteParent(CategoryConstants.ABS_LEVEL)).thenReturn(parentPage);
        when(parentPage.adaptTo(Node.class)).thenReturn(parentNode);
        when(parentNode.getProperty(JcrConstants.JCR_CREATED)).thenReturn(property);
        when(property.getDate()).thenReturn(calendar);

        ValueMap valueMap = new ValueMapDecorator(new HashMap<String, Object>());
        when(parentPage.getProperties()).thenReturn(valueMap);
        return parentPage;
    }

    private void createMonthNavigationServlet() throws Exception {
        classUnderTest = new MonthNavigationServlet();
        classUnderTest.init();
    }

    private class DummyResouseBundle extends ResourceBundle {

        private Map<String, Object> dataMap = new HashMap<String, Object>();

        @Override
        protected Object handleGetObject(String key) {
            return dataMap.get(key);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Enumeration<String> getKeys() {
            return new IteratorEnumeration(dataMap.keySet().iterator());
        }

        public void setDataMap(Map<String, Object> dataMap) {
            this.dataMap = dataMap;
        }

    }

}
