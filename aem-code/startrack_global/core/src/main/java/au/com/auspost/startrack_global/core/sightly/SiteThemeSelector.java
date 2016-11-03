package au.com.auspost.startrack_global.core.sightly;

import com.adobe.cq.sightly.WCMUse;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides right theme based on absoluteParent at level1 (e.g:/content/auspost_campaign) of the page.
 *
 * Usage as below:
 * <sly data-sly-use.siteTheme="${'au.com.auspost.startrack_global.core.sightly.SiteThemeSelector'}"></sly>
    <body data-sly-use.body="body.js" class="${siteTheme.theme}" data-sly-include="body.html"></body>
 *
 *
 */

public class SiteThemeSelector extends WCMUse {
    private String theme;

    private static HashMap<String, String> themeMaps;
    static
    {
        themeMaps = new HashMap<String, String>();
        themeMaps.put("auspost_campaign", "theme-campaign");
        themeMaps.put("auspost_corp_microsites", "theme-corporate");
        themeMaps.put("auspost_corp", "theme-auspost");
        themeMaps.put("startrack_corp", "theme-startrack");
    }

    public String getTheme() {
        return theme;
    }

    @Override
    public void activate() throws Exception {
        String siteRoot = getCurrentPage().getAbsoluteParent(1).getName();
        theme = getThemeClass(siteRoot);
    }

    /*
        This method returns a theme class name for the absoluteParent(1) of the Page
        * @param String  parameter
        * @return  String with themeClassName
    */
    public String getThemeClass(String siteRoot) {
        String themeClassName = StringUtils.EMPTY;
        if(!themeMaps.isEmpty() && themeMaps.size()>0) {
            for (Map.Entry<String, String> themeMap : themeMaps.entrySet()) {
                String themeMapKey = themeMap.getKey();
                String themeMapValue = themeMap.getValue();
                if (siteRoot.equals(themeMapKey)) {
                    themeClassName = themeMapValue;
                }
            }
        }
        return themeClassName;
    }


}