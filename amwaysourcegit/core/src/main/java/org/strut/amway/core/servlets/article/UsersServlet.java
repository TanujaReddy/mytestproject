package org.strut.amway.core.servlets.article;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@Service(UsersServlet.class)
@SlingServlet(paths = {"/bin/users"}, generateComponent = false)

@Component(label = "Dropdown Movie data provider", description = "This servlet provides launch code names in drop down",
enabled = true, immediate = true, metatype = false)

public class UsersServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;

    protected static final Logger LOGGER = LoggerFactory.getLogger(UsersServlet.class);
	
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            JSONObject eachOption;
            JSONArray optionsArray = new JSONArray();
            String noAuthor = "  --  ";
            
            ResourceResolver resourceResolver = null;
        	ArrayList<String> users = new ArrayList<>();            
             
          	resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			//Session session = resourceResolver.adaptTo(Session.class);
    		
    		UserManager userManager = resourceResolver.adaptTo(UserManager.class);
    		Authorizable authorizable= userManager.getAuthorizable("article-contributor");
    		
    		org.apache.jackrabbit.api.security.user.Group group = (org.apache.jackrabbit.api.security.user.Group) authorizable;
            Iterator<Authorizable> itr = group.getMembers();
                       
           // LOGGER.info(" " + itr.hasNext());
            while(itr.hasNext()) {
            	
                Object obj = itr.next();
                
                            
                if(obj instanceof User) {                
                    User user = (User) obj;        
                    String lastName = "";
                    
                    
					if (user.getProperty("./profile/familyName") != null) {
						lastName = user.getProperty("./profile/familyName")[0].getString();
					//	LOGGER.info("--1NAME---" + user.getProperty("./profile/familyName")[0].getString());

					}
					       
                        users.add(lastName);
                        
                   
                }
            }
            users.add(noAuthor);
            String [] usersArray = users.toArray(new String[users.size()]);
            Arrays.sort(usersArray);
             
            for (int i = 0; i < users.size(); i++) {
                eachOption = new JSONObject();
               // returnData[i] = users(i);
              //  LOGGER.info(usersArray[i] + "-----USERS ARRAY---");
                eachOption.put("text", usersArray[i]);
                eachOption.put("value", usersArray[i]);
               
                optionsArray.put(eachOption);
            }
 
            JSONObject finalJsonResponse = new JSONObject();
            //Adding this finalJsonResponse object to showcase optionsRoot property functionality
            finalJsonResponse.put("root", optionsArray);
 
            response.getWriter().println(finalJsonResponse.toString());
        } catch (JSONException e) {
            LOGGER.error("Json Exception occured while adding data to JSON Object : ", e);
        } catch (IOException e) {
            LOGGER.error("IOException occured while getting Print Writer from SlingServletResponse : ", e);
        }
        catch (Exception e) {
            LOGGER.error("IOException occured while getting Print Writer from SlingServletResponse : ", e);
        }
    }
    
    
    /*public ArrayList<AuthorUserDetails> getUserDetails(SlingHttpServletRequest request) {
    	
    	ArrayList<AuthorUserDetails> users = new ArrayList<>();    
    	
    	try {
            
    		 LOGGER.info(" getUserDetails ----" );
    		 
             ResourceResolver resourceResolver = null;
         	        
              
           	resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
 			//Session session = resourceResolver.adaptTo(Session.class);
     		
     		UserManager userManager = resourceResolver.adaptTo(UserManager.class);
     		Authorizable authorizable= userManager.getAuthorizable("article-contributor");
     		
     		org.apache.jackrabbit.api.security.user.Group group = (org.apache.jackrabbit.api.security.user.Group) authorizable;
             Iterator<Authorizable> itr = group.getMembers();
                        
             LOGGER.info(" TEST " + itr.hasNext());
             while(itr.hasNext()) {
            	 
            		 String userId = "";	
            		 String firstName= "";
            		 String lastName= "";
            		 String email= "";
            		 String aboutMe= "";
            		 String image= "";
             	
                 Object obj = itr.next();
                 
                                
                 if(obj instanceof User) {                
                     User user = (User) obj;    
                     
					if (user.getID() != null) {
						userId = user.getID();
					}
					if (user.getProperty("./profile/aboutMe") != null) {
						aboutMe = user.getProperty("./profile/aboutMe")[0].getString();
					}
					if (user.getProperty("./profile/email") != null) {
						email = user.getProperty("./profile/email")[0].getString();
					}
					if (user.getProperty("./profile/familyName") != null) {
						lastName = user.getProperty("./profile/familyName")[0].getString();
					}
					if (user.getProperty("./profile/givenName") != null) {
						firstName = user.getProperty("./profile/givenName")[0].getString();
					}
					if (user.getProperty("./profile/photos/primary/image") != null) {
						image = user.getPath() + "/profile/photos/primary/image";
					}
 				
 					users.add(new AuthorUserDetails(userId, firstName, lastName, email, aboutMe, image));
 					LOGGER.info("-1-" + userId + "-2-" + firstName +  "-3-" + lastName + "-4-" + email + "-5-" + aboutMe +"-6-" + image);
 					users.add(1, userId);
 					users.add(2, aboutMe);
 					users.add(3, email);
 					users.add(4, lastName);
 					users.add(5, firstName);
 					users.add(6, image);
                    
                 }
             }
         
            
  
             
         } 
         catch (Exception e) {
             LOGGER.error("UserServlet getUserDetails ", e);
         }
       
        return users;
    }*/
}