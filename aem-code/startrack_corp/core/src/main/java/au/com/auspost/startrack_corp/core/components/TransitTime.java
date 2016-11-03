package au.com.auspost.startrack_corp.core.components;

import javax.jcr.RepositoryException;

import javax.jcr.Node;


import java.util.stream.StreamSupport;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.startrack_corp.core.Constants;

// Code behind for footer
public class TransitTime extends WCMUse {
    private Logger log;

    private class StepSupport {
        Integer i = 0;
    }

    private class Recursive<T> {
        public T func;
    }

    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(TransitTime.class);
        log.debug("Transit Time::activate is called");

        items = new ArrayList<Map<String, String>>();        
        testString = getTestSet().stream().sorted((x, y) -> {
            return y.compareTo(x);
        }).map(o -> o.toString()).reduce(StringUtils.EMPTY, (x, y) -> x + y);
        
        final Resource cr = getResource();
        if (cr == null) {
            log.error("Current resource isn't found");
            return;
        }
        
        Node n = cr.adaptTo(Node.class);
       
     
        ResourceResolver resolver = getResourceResolver();
        if (resolver == null) {
            log.error("ResourceResolver isn't found");
         
            return;
        }
        Resource resource = resolver.getResource(Constants.STARTRACK_BASE_PATH2);
        if (resource == null) {
            log.error("Startrack base resource isn't found");
         
            return;
        }
        
       
        
      
        
        Supplier<List<String>> getTags = () -> {
            List<String> tags = new ArrayList<String>();
            Resource page = cr;
            while(page != null && !page.getName().equals(Constants.JCR_CONTENT)) {
                page = page.getParent();
            }
            if (page != null) {
                Node pn = page.adaptTo(Node.class); 
               try {
                    if (pn.hasProperty(Constants.CQ_TAGS)) {
                      
                        tags = Arrays.stream(pn.getProperty(Constants.CQ_TAGS).getValues()).map(ov -> {
                            try {
                            	
                                return ov.getString();
                            } catch (RepositoryException e) {
                                log.error("RepositoryException: " + e);
                              
                            }
                            return StringUtils.EMPTY;
                        }).collect(Collectors.toList());
                    }
                } catch (RepositoryException e) {
                    log.error("RepositoryException: " + e);
                }
            }
            return tags;
        };

        final List<String> tags = getTags.get();
       
        Recursive<BiConsumer<Node, StepSupport>> recursive = new Recursive<>();
        recursive.func = (x, y) -> {
            try {
                @SuppressWarnings("unchecked")
                final Iterator<Node> ni = (Iterator<Node>)x.getNodes();
                ni.forEachRemaining(o -> {
                    try {
                    	     	
                    	if (o.hasProperty(Constants.JCR_PRIMARY_TYPE) && o.hasProperty(Constants.JCR_CONTENT_PLUS_TAGS)) {
                    	      	
                            final Boolean isPage = o.getProperty(Constants.JCR_PRIMARY_TYPE).getString().equals(Constants.CQ_PAGE);                         
                          
                            final Boolean isSlide = Arrays.stream(o.getProperty(Constants.JCR_CONTENT_PLUS_TAGS).getValues()).map(ov -> {
                            	try {
                            		
                                    String tag = ov.getString();
                                    
                                    Supplier<List<String>> getTransitTimeTags = () -> {
                                        List<String> transittimetags = new ArrayList<String>();
                                   
                                           try {
                                        	   transittimetags = Arrays.stream(n.getProperty(Constants.TRANSIT_TIME).getValues()).map(ovs -> {
                                                        try {
                                                        	
                                                            return ovs.getString();
                                                        } catch (RepositoryException e) {
                                                            log.error("RepositoryException: " + e);
                                                          
                                                        }
                                                        return StringUtils.EMPTY;
                                                    }).collect(Collectors.toList());
                                                }
                                            catch (RepositoryException e) {
                                                log.error("RepositoryException: " + e);
                                            }
                                       
                                        return transittimetags;
                                    };
                                    
                                    final List<String> tagstransittime = getTransitTimeTags.get();
                                    
                                    tagstransittime.forEach(os-> {
                                   
                                    });
                                    
                                    for(int i=0;i<tagstransittime.size();i++) {
	                                    if(tag.equals(tagstransittime.get(i))) {
	                                    
	                                    return tags.size() > 0? tags.stream().filter(t -> {
	                                       
	                                        return t.equals(tagstransittime);
	                                    }).limit(1).mapToInt(ti -> 1).sum() > 0: true;
	                                    }
                                    }
                                    
                                } catch (RepositoryException e) {
                                    log.error("Transit Time::activate RepositoryException: " + e);
                                }
                                return false;
                            }).reduce(false, (xs, ys) -> xs || ys);
                                                                    
                            if (isPage && isSlide && o.hasNode(Constants.JCR_CONTENT_PLUS_PARSYS)) {
                            	
                           
                            	
                                @SuppressWarnings("unchecked")
                                final Iterator<Node> npi = (Iterator<Node>)o.getNode(Constants.JCR_CONTENT_PLUS_PARSYS).getNodes();
                                npi.forEachRemaining(op -> {
                                    try {
                                    	                            	
                                        if (op.hasProperty(Constants.SLING_RESOURCE_TYPE) && op.hasProperty("layoutContainerParsys/card/containerTypeCard")) {
                                        	
                                            final Boolean isLayout = op.getProperty(Constants.SLING_RESOURCE_TYPE).getString().equals("startrack_corp/components/layout");
                                            final Boolean isHero = op.getProperty("layoutContainerParsys/card/containerTypeCard").getString().equals("hero");
                                            
                                            if (isLayout && isHero) {
                                           
                                            	Map<String, String> map = new HashMap<String, String>();
                                                
                                                final String path= o.getPath().toString();
                                                if(path !=null) {
                                                String finalPath= path.concat(".html");
                                                map.put("finalPath", finalPath);
                                           
                                                }

                                                final String imageStr = op.hasProperty("layoutContainerParsys/card/imagePathRef")?
                                                    op.getProperty("layoutContainerParsys/card/imagePathRef").getString(): StringUtils.EMPTY;
                                                map.put("image", imageStr);
                                                final String titleStr = op.hasProperty("layoutContainerParsys/card/card-heading-text/text")?
                                                    op.getProperty("layoutContainerParsys/card/card-heading-text/text").getString(): StringUtils.EMPTY;
                                                map.put("title", titleStr);
                                                
                                               
                                                final String bodyStr = op.hasProperty("layoutContainerParsys/card/card-body-text/text")?
                                                    op.getProperty("layoutContainerParsys/card/card-body-text/text").getString(): StringUtils.EMPTY;
                                                map.put("body", bodyStr);
                                                final String dateStr = op.hasProperty("layoutContainerParsys/card/jcr:lastModified")?
                                                    op.getProperty("layoutContainerParsys/card/jcr:lastModified").getString(): null;
                                                map.put("date", dateStr);
                                                final String ctaStr = op.hasProperty("layoutContainerParsys/card/ctaTypeConfig")?
                                                    op.getProperty("layoutContainerParsys/card/ctaTypeConfig").getString(): null;
                                                if (ctaStr != null) {
                                                    try {
                                                        JSONObject json = new JSONObject(ctaStr);
                                                        Iterable<String> iterable = () -> json.keys();
                                                        StreamSupport.stream(iterable.spliterator(), false).map(oo -> {
                                                            try {
                                                                if (oo.equals("ctaURL")) {
                                                                	
                                                                	if(json.getString(oo).contains("/content")) {
                                                                		log.debug( "oo " + oo +  "  index " + json.getString(oo));
                                                                		String test = json.getString(oo).toString().concat(".html");
                                                                		 map.put("ctaUrl", test);
                                                                	} else {
                                                                		 map.put("ctaUrl", json.getString(oo));
                                                                	}
                                                                	
                                                                }
                                                                if (oo.equals("ctaTypeText")) {
                                                                    map.put("ctaText", json.getString(oo));
                                                                }
                                                            } catch (JSONException ex) {
                                                                log.error("json exception: " + ex.getMessage());
                                                            }
                                                            return 1;
                                                        }).reduce(0, (xr, yr) -> xr + yr);
                                                    }
                                                    catch (JSONException ex) {
                                                        log.error("json exception: " + ex.getMessage());
                                                    }
                                                }
                                                items.add(map);
                                            }
                                        }
                                    } catch (RepositoryException e) {
                                        log.error("Transit Time::activate RepositoryException: " + e);
                                    }
                                });
                                items = items.stream().sorted((xc, yc) -> yc.get("date")
                                    .compareTo(xc.get("date"))).limit(3).collect(Collectors.toList());
                            }                            
                        }
                        if (o.hasProperty(Constants.JCR_PRIMARY_TYPE)) {
                            final Boolean isPage = o.getProperty(Constants.JCR_PRIMARY_TYPE).getString().equals(Constants.CQ_PAGE);
                            
                            if (isPage) {
                                y.i++;
                                recursive.func.accept(o, y);
                            }
                        }
                    } catch (RepositoryException e) {
                        log.error("RepositoryException: " + e);
                        return;
                    }
                });
            } catch (RepositoryException e) {
                log.error("RepositoryException: " + e);
                return;
            }
        };
        recursive.func.accept(resource.adaptTo(Node.class), new StepSupport());
    }

    private List<Map<String, String>> items;
    public List<Map<String, String>> getItems() {
        return items;
    }

    // set of integer for test reason
    public Set<Integer> getTestSet() {
        return new HashSet<Integer>();
    }

    private String testString = StringUtils.EMPTY;
    public String getTestString() {
        return testString;
    }    
}
