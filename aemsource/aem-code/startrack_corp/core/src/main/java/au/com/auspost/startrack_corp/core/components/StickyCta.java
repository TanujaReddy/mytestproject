package au.com.auspost.startrack_corp.core.components;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

public class StickyCta extends WCMUsePojo {
    private Logger log;

    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(StickyCta.class);

        final Page page = this.getCurrentPage();
        if (page == null) {
            log.error("Current page isn't found");
            return;
        }

		final Page pp = page.getParent();
	      
		Resource mt = pp.getContentResource().getChild("jcr:menutitle");
		if (mt != null) {
		    title = mt.adaptTo(String.class);
		}
    }

    String title;
    public String getTitle() {
        return title;
    }
}
