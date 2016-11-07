package org.strut.amway.core.util;

import org.apache.sling.settings.SlingSettingsService;

/**
 * For runmode config see https://docs.adobe.com/docs/en/aem/6-1/deploy/configuring/configure-runmodes.html
 */
public class AEMUtils {
    private AEMUtils() {
    }

    public static final String IS_PUBLISH_INSTANCE = "isPublishInstance";

    public static boolean isAuthor(SlingSettingsService slingSettingsService) {
        return slingSettingsService.getRunModes().contains("author");
    }

    public static boolean isPublish(SlingSettingsService slingSettingsService) {
        return slingSettingsService.getRunModes().contains("publish");
    }

    public static boolean isProduction(SlingSettingsService slingSettingsService) {
        return slingSettingsService.getRunModes().contains("production");
    }
}
