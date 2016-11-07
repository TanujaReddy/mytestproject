package au.com.auspost.global.core.sightly;

import com.adobe.cq.sightly.WCMUse;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class iterates over etc/design currentStyle. You can pass the property name on whose value need to returned to html.
 *
 * Usage as below:
 * <div data-sly-use.linkListUtils="${'au.com.auspost.global.core.sightly.MultiFieldIterator' @ property='socialnetwork'}" data-sly-unwrap>
    <ul class="footer-list footer-list--social">
        <sly data-sly-list="${linkListUtils.linkListItems}">
            <div data-sly-test="${item.socialClass}" data-sly-unwrap>
                <div data-sly-use.socialStyle="${'au.com.auspost.global.core.sightly.DesignStyleIterator' @ property=item.socialClass}" data-sly-unwrap>
                    <li><a href="${item.linkpath}" class="footer-link footer-link--${item.socialClass}">${socialStyle.socialHtml @context="unsafe"}</a></li>
                </div>
            </div>
        </sly>
    </ul>
 </div>
 *
 * Don't change the parameter @property, you can only change the name of the property.
 *
 */

public class DesignStyleIterator extends WCMUse {
    private String socialHtml;

    public String getSocialHtml() {
        return socialHtml;
    }


    
    @Override
    public void activate() throws Exception {
    	String propertyToExtract = get("property",String.class);

    	if(StringUtils.isNotEmpty(propertyToExtract) && null != getCurrentStyle().get(propertyToExtract)){

                socialHtml = getCurrentStyle().get(propertyToExtract).toString();

    	}

    }


}