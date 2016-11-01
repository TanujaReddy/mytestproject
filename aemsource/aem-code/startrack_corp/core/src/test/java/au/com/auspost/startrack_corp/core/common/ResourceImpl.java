package au.com.auspost.startrack_corp.core.common;

import java.util.Iterator;

import javax.jcr.Node;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class ResourceImpl implements org.apache.sling.api.resource.Resource {
    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> cls) {
        if (cls == ValueMap.class ) {
            return (AdapterType)new ValueMapImpl();            
        } else if (cls == Node.class ) {
            return (AdapterType)new NodeImpl();
        } else {
            return null;
        }
    }
    @Override
    public Resource getChild(String arg0) { return null; }
    @Override
    public Iterable<Resource> getChildren() { return null; }
    @Override
    public String getName() { return StringUtils.EMPTY; }
    @Override
    public Resource getParent() { return null; }
    @Override
    public String getPath() { return null; }
    @Override
    public ResourceMetadata getResourceMetadata() { return null; }
    @Override
    public ResourceResolver getResourceResolver() { return null; }
    @Override
    public String getResourceSuperType() { return null; }
    @Override
    public String getResourceType() { return null; }
    @Override
    public ValueMap getValueMap() { return null; }
    @Override
    public boolean hasChildren() { return false; }
    @Override
    public boolean isResourceType(String arg0) { return false; }
    @Override
    public Iterator<Resource> listChildren() { return null; }

}
