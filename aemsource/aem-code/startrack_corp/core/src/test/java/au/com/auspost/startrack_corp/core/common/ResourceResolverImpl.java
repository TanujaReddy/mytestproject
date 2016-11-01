package au.com.auspost.startrack_corp.core.common;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;

public class ResourceResolverImpl implements org.apache.sling.api.resource.ResourceResolver {
    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> arg0) { return null; }
    @Override
    public void revert() {}
    @Override
    public Resource resolve(HttpServletRequest arg0, String arg1) { return null; }
    @Override
    public Resource resolve(HttpServletRequest arg0) { return null; }
    @Override
    public Resource resolve(String arg0) { return null; }
    @Override
    public void refresh() {}
    @Override
    public Iterator<Map<String, Object>> queryResources(String arg0, String arg1) { return null; }
    @Override
    public String map(HttpServletRequest arg0, String arg1) { return null; }
    @Override
    public String map(String arg0) { return null; }
    @Override
    public Iterator<Resource> listChildren(Resource arg0) { return null; }
    @Override
    public boolean isResourceType(Resource arg0, String arg1) { return false; }
    @Override
    public boolean isLive() { return false; }
    @Override
    public boolean hasChildren(Resource arg0) { return false; }
    @Override
    public boolean hasChanges() { return false; }
    @Override
    public String getUserID() { return null; }
    @Override
    public String[] getSearchPath() { return null; }
    @Override
    public Resource getResource(Resource arg0, String arg1) { return null; }
    @Override
    public Resource getResource(String arg0) {
        return new ResourceImpl();
    }
    @Override
    public String getParentResourceType(String arg0) { return null; }
    @Override
    public String getParentResourceType(Resource arg0) { return null; }
    @Override
    public Iterable<Resource> getChildren(Resource arg0) { return null; }
    @Override
    public Iterator<String> getAttributeNames() { return null; }
    @Override
    public Object getAttribute(String arg0) { return null; }
    @Override
    public Iterator<Resource> findResources(String arg0, String arg1) { return null; }
    @Override
    public void delete(Resource arg0) throws PersistenceException {}
    @Override
    public Resource create(Resource arg0, String arg1, Map<String, Object> arg2) throws PersistenceException { return null; }
    @Override
    public void commit() throws PersistenceException {}
    @Override
    public void close() {}
    @Override
    public ResourceResolver clone(Map<String, Object> arg0) throws LoginException { return null; }
}
