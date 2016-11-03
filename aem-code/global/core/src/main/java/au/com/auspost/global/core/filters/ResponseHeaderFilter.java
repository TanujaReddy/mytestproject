package au.com.auspost.global.core.filters;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.auspost.global.utils.Constants;

@SlingFilter(scope = SlingFilterScope.COMPONENT, generateComponent = false, order = -10000, metatype = true, 
label = "Response headers for caching", description = "Add response headers for catching the requests")
@Component(immediate = true)
public class ResponseHeaderFilter implements Filter{
	
	String LONG_MAX_AGE_DEFAULT = "max-age=86400";

	@Property(value = "publish")
	static final String RUN_MODE = "runmode";

	private String runmode;

	private boolean isValidRunmode; 

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY)
	protected SlingSettingsService slingSettings;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHeaderFilter.class);

	@Activate
	protected void activate(final ComponentContext componentContext) {
		Set<String> runmodes = slingSettings.getRunModes();
		LOGGER.info("Run modes in activate are {}", runmodes);
		Dictionary<String, String> props = componentContext.getProperties();
		runmode = props.get(RUN_MODE);
		LOGGER.info("Checking whether runmode: {} exist in runmodes: {}", runmode, runmodes);
		if (runmodes.contains(runmode)) {
			LOGGER.info("Runmode {} is valid", runmode);
			isValidRunmode = true;
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!isValidRunmode) {
			chain.doFilter(request, response);
			return;
		}

		if (!(request instanceof SlingHttpServletRequest && response instanceof SlingHttpServletResponse)) {
			chain.doFilter(request, response);
			return;
		}

		SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
		SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;

		String selectors[] = slingRequest.getRequestPathInfo().getSelectors();
		
		boolean adaptiveImageRequest = false;
		for(String selector:selectors){
			if(StringUtils.isNotBlank(selector) && selector.equals("auspostimage")){
				adaptiveImageRequest = true;
			}
		}
		
		if(adaptiveImageRequest){
			slingResponse.setHeader(Constants.CACHE_CONTROL_HEADER_KEY, LONG_MAX_AGE_DEFAULT);
		}
		
		chain.doFilter(slingRequest, slingResponse);
	}

	public void destroy() {

	}

}
