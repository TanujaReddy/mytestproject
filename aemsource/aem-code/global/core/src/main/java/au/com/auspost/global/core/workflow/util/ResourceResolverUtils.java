package au.com.auspost.global.core.workflow.util;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceResolverUtils {

    private final Logger log = LoggerFactory.getLogger(ResourceResolverUtils.class);

    private ResourceResolverUtils(){
        //Don't allow instantiation normally.
    }

    /**
     * This method should be used centrally to close the sessions.
     * @param session
     */
    public static void closeSession(Session session){
        try{
            if(null!=session){
                session.logout();
            }
        }catch(RuntimeException e){
            throw new RuntimeException("Exception in closing the session",e);
        }
    }

    /**
     * This method should be used centrally to close the sessions.
     * @param session
     */
    public static void closeResourceResolver(ResourceResolver resolver){
        try{
            if(null!=resolver){
                resolver.close();
            }
        }catch(RuntimeException e){
            throw new RuntimeException("Exception in closing the session",e);
        }
    }
}