package org.strut.amway.core.servlets.monthnavigation;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.enumeration.MonthMessageKey;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.json.parser.monthnavigation.MonthNavigationJsonParser;
import org.strut.amway.core.model.DateTimeRange;
import org.strut.amway.core.model.MonthNavigationModel;
import org.strut.amway.core.servlets.AbstractJsonServlet;
import org.strut.amway.core.util.CategoryConstants;
import org.strut.amway.core.util.Constants;
import org.strut.amway.core.util.DateTimeUtils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;

@SuppressWarnings("serial")
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "amway.month.navigation", extensions = "json", methods = "GET")
public class MonthNavigationServlet extends AbstractJsonServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonthNavigationServlet.class);
    private static final String CLIENT_TIME_PARAM = "clientTime";
    private static final int MAX_MONTH_DISPLAY = 6;

    private JsonParser<List<MonthNavigationModel>> jsonParser;

    @Override
    public void init() throws ServletException {
        super.init();
        jsonParser = new MonthNavigationJsonParser();
    }

    @Override
    public void destroy() {
        super.destroy();
        jsonParser = null;
    }

    @Override
    protected void handle(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws Exception {
        final DateTime clientTime = getClientTime(request);
        final Page currentPage = getPage(request);
        final I18n i18n = getI18n(request, currentPage.getLanguage(false));
        final List<MonthNavigationModel> datas = buildMonthNavigationData(clientTime, currentPage, i18n);
        writeJsonToResponse(response, jsonParser.parse(request.getResourceResolver(), datas));
    }

    private DateTime getClientTime(final SlingHttpServletRequest request) {
        final String clientTimeParam = request.getParameter(CLIENT_TIME_PARAM);
        LOGGER.info("request with clientTimeParam:{}" + clientTimeParam);
        if (clientTimeParam == null) {
            throw new IllegalStateException("clientTime Param can not be null");
        }
        return DateTimeUtils.parseClientTimeWithClientTZ(clientTimeParam);
    }

    private Page getPage(final SlingHttpServletRequest request) throws IllegalStateException {
        final Page page = request.getResource().adaptTo(Page.class);
        if (page == null) {
            String err = "Can not resolve Sling Resource to Page from path " + request.getResource().getPath();
            LOGGER.error(err);
            throw new IllegalStateException(err);
        }
        return page;
    }

    private List<MonthNavigationModel> buildMonthNavigationData(final DateTime clientTime, final Page currentPage, final I18n i18n) throws Exception {
        final List<MonthNavigationModel> datas = new ArrayList<MonthNavigationModel>();
        final DateTimeZone clientTimeZone = clientTime.getZone();
        final DateTime createdDateOfHomePageClientTz = getCreatedDateOfHomePageByClientTimeZone(currentPage, clientTimeZone);
        final DateTime currentServerTimeByClientTz = new DateTime(clientTimeZone);

        DateTime month = currentServerTimeByClientTz;
        for (int i = MAX_MONTH_DISPLAY; i > 0; i--) {
            String label = i18n.get(MonthMessageKey.resolve(month.getMonthOfYear()).getMessageKey());
            DateTimeRange datetimeRange = DateTimeUtils.getDateTimeRangeByServerTimeOf(month);
            datas.add(new MonthNavigationModel(label, datetimeRange));
            month = month.minusMonths(1);
            if (month.isBefore((createdDateOfHomePageClientTz)) && !DateTimeUtils.isInAMonth(createdDateOfHomePageClientTz, month)) {
                break;
            }
        }
        return datas;
    }

    private DateTime getCreatedDateOfHomePageByClientTimeZone(final Page currentPage, final DateTimeZone clientTimeZone) throws Exception {
        final Page homePage = currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL);
        final Object installationDate = homePage.getProperties().get(Constants.INSTALLATION_DATE_PROPERTY);
        if (installationDate != null) {
            return new DateTime((GregorianCalendar) installationDate, clientTimeZone);
        } else {
            LOGGER.warn("installationDate is null. Use jcr:created property of homepage node");
            final Node node = homePage.adaptTo(Node.class);
            return new DateTime(node.getProperty(JcrConstants.JCR_CREATED).getDate(), clientTimeZone);
        }
    }

    private I18n getI18n(final SlingHttpServletRequest request, Locale pageLocale) {
        final ResourceBundle resourceBundle = request.getResourceBundle(pageLocale);
        I18n i18n = new I18n(resourceBundle);
        return i18n;
    }

}
