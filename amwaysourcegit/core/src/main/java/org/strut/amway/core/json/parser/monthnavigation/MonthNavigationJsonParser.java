package org.strut.amway.core.json.parser.monthnavigation;

import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.model.DateTimeRange;
import org.strut.amway.core.model.MonthNavigationModel;

import com.day.cq.wcm.api.WCMException;

public class MonthNavigationJsonParser implements JsonParser<List<MonthNavigationModel>> {

    @Override
    public String parse(final ResourceResolver resourceResolver, final List<MonthNavigationModel> monthNavigationModels) throws JSONException, WCMException {
        final JSONObject json = new JSONObject();
        final JSONArray jsonMonths = new JSONArray();
        for (MonthNavigationModel monthNavigationModel : monthNavigationModels) {
            jsonMonths.put(createJsonForMonthNavigationModel(monthNavigationModel));
        }
        json.put("months", jsonMonths);
        return json.toString();
    }

    private JSONObject createJsonForMonthNavigationModel(final MonthNavigationModel monthNavigationModel) throws JSONException, WCMException {
        final JSONObject json = new JSONObject();
        json.put("label", monthNavigationModel.getLabel());

        final DateTimeRange datetimeRange = monthNavigationModel.getDateTimeRange();
        if (datetimeRange != null) {
            json.put("startDate", datetimeRange.getStartTime());
            json.put("endDate", datetimeRange.getEndTime());
        }
        return json;
    }

}
