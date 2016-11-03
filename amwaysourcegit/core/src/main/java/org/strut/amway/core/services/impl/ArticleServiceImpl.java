package org.strut.amway.core.services.impl;

import java.io.BufferedInputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.model.AuthorUserDetails;
import org.strut.amway.core.services.ArticleService;
import org.strut.amway.core.util.ArticleConstants;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

@Service(value = ArticleService.class)
@Component(immediate = true)
public class ArticleServiceImpl implements ArticleService {

    private static Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Reference
    SlingRepository repository;
    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public int getLikeNumberByArticleName(String articleName) {
        int result = 0;
        Session session = null;
        try {
            StringBuilder articlePath = new StringBuilder();
            articlePath.append(ArticleConstants.PATH_STORED_NODE).append(ArticleConstants.NODE_REGEX).append(articleName);
            session = repository.loginAdministrative(null);
            Node root = session.getRootNode();
            if (root.hasNode(articlePath.toString())) {
            	
                Node content = root.getNode(articlePath.toString());
                String num = content.getProperty(ArticleConstants.NUMBER_OF_LIKE).getString();
                if (num == null) {
                    return result;
                }

                try {
                    result = Integer.valueOf(num);
                } catch (NumberFormatException ne) {
                    return result;
                }
            } else {
                return 0;
            }
        } catch (RepositoryException e) {
            LOGGER.error("Could not save like number into article content page: Repository exception" + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Could not save like number into article content page " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
        return result;
    }

    public int updateLikeNumberByArticleName(String articleName) {
        int numberOfLike = -1;

        Session session = null;
        try {
            String[] pathStoredNode = (ArticleConstants.PATH_STORED_NODE + ArticleConstants.NODE_REGEX + articleName).split(ArticleConstants.NODE_REGEX);
            
            session = repository.loginAdministrative(null);

            // check start node
            Node root = session.getRootNode();
            Node content = root.getNode(pathStoredNode[0]);
            if (content == null) {
                return numberOfLike;
            }

            // loop each node, if not exist, create new
            for (int i = 1; i < pathStoredNode.length; i++) {
                String path = pathStoredNode[i];
                Node node = null;
                if (content.hasNode(path)) {
                    node = content.getNode(path);
                } else {
                    node = content.addNode(pathStoredNode[i], JcrConstants.NT_UNSTRUCTURED);
                }
                content = node;
            }

            if (content.hasProperty(ArticleConstants.NUMBER_OF_LIKE)) {
                content.getProperty(ArticleConstants.NUMBER_OF_LIKE).remove();
            }

            // get current number of likes
            numberOfLike = getLikeNumberByArticleName(articleName);

            // increase like by 1
            content.setProperty(ArticleConstants.NUMBER_OF_LIKE, numberOfLike + 1);
            session.save();

            // get number of likes again in case others update (concurrency)
            numberOfLike = getLikeNumberByArticleName(articleName);
        } catch (Exception e) {
            LOGGER.error("Exception: " + ExceptionUtils.getStackTrace(e));
            return -1;
        } finally {
            if (session != null) {
                session.logout();
            }
        }

        return numberOfLike;
    }
    
    @Override
  	public Map<String, String> getImageString(SlingHttpServletRequest request,String url, Page currentPage) {

		Session session = null;
		Map<String, String> imageGallery = new HashMap<String, String>();
		String description = "";
		String urlResult = "";
		String imageDataString ="";
		BufferedInputStream bis = null;
		
				
		try {

			session = repository.loginAdministrative(null);

			Node root = session.getNode(url);
	
			if (root != null) {
				ResourceResolver resourceResolver = request.getResourceResolver();
				Resource resource = resourceResolver.getResource(url);		
				  

				if (resource != null) {
					Node node = resource.adaptTo(Node.class);
					StringWriter stringWriter = new StringWriter();
					JsonItemWriter jsonWriter = new JsonItemWriter(null);

					try {
						jsonWriter.dump(node, stringWriter, -1);
						JSONObject jsonObject = new JSONObject(stringWriter.toString());
						Iterator<String> iterator = jsonObject.keys();

						while (iterator.hasNext()) {
							String result = iterator.next();

							if (result.contains(".jpg")) {
								
								//LOGGER.info("imageDataString : " + "JPG JPG");

								JSONObject imageObject = (JSONObject) jsonObject.get(result);
								JSONObject imageObject1 = new JSONObject(imageObject.toString());
								Iterator<?> keysIterator = imageObject1.keys();
								while (keysIterator.hasNext()) {
									String key = (String) keysIterator.next();
									if (imageObject1.get(key) instanceof JSONObject) {
										JSONObject metaData = new JSONObject(imageObject1.get(key).toString());
										JSONObject metaDataObject = new JSONObject(metaData.toString());
										Iterator<?> keysiter = metaDataObject.keys();
										while (keysiter.hasNext()) {
											String keyiter = (String) keysiter.next();
											if (metaDataObject.get(keyiter) instanceof JSONObject) {
												String desc = metaDataObject.getString("metadata");
												if (desc.contains("dc:description")) {
													JSONObject descObject = new JSONObject(desc);													
													description = descObject.getString("dc:description");													
												} else {
													String image = result.substring(0, result.lastIndexOf('.'));
													description = image;
												}
											}

										}
									}
								}
								
								urlResult = url + "/" + result;	
																
								
								String scheme = request.getScheme();
								String serverName = request.getServerName();
								int portNumber = request.getServerPort();
								String contextPath = request.getContextPath();
								
								String baseUrl = scheme + "://"  + serverName + ":" +portNumber + contextPath + urlResult;
								
								LOGGER.info("getImageString1---->" + baseUrl);
								
								URL urlImage = new URL(baseUrl);
								HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();

								connection.setRequestMethod("GET");
								connection.setRequestProperty("user-agent", "Mozilla/5.0");
								connection.setRequestProperty("Content-Type", "application/json");
														
						    	 bis = new BufferedInputStream(connection.getInputStream());

						   //     FileInputStream imageInFile = new FileInputStream(is.toString());
						        byte imageData[] = new byte[1024];
						        bis.read(imageData);

						        // Converting Image byte array into Base64 String
						         imageDataString = encodeImage(imageData);
						       
						        LOGGER.info("imageDataString-->" + baseUrl + "----->" + imageDataString);
								
						        imageGallery.put(urlResult, imageDataString);
								
							}

						}

					} catch (Exception e) {
						LOGGER.error("Could not create JSON", e);
					} 
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return imageGallery;
	}
    
    /**
	 * Encodes the byte array into base64 string
	 *
	 * @param imageByteArray - byte array
	 * @return String a {@link java.lang.String}
	 */
	public static String encodeImage(byte[] imageByteArray) {
		return Base64.encodeBase64URLSafeString(imageByteArray);
	}

	@Override
	public Map<String, String> getImages(SlingHttpServletRequest request,String url, Page currentPage) {
		
		
		Session session = null;
		Map<String,String> imageGallery = new HashMap<String,String>();
		String description ="";
		String urlResult ="";
		try {

			session = repository.loginAdministrative(null);

			String baseUrl = FilenameUtils.getPath(url);
			String myFile = FilenameUtils.getBaseName(url) + "." + FilenameUtils.getExtension(url);

			if (baseUrl.endsWith("/")) {
				baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
				baseUrl = "/" + baseUrl;
			}
			Node root = session.getNode(baseUrl);

			if (root != null) {
				ResourceResolver resourceResolver = request.getResourceResolver();
				Resource resource = resourceResolver.getResource(baseUrl);

				if (resource != null) {
					Node node = resource.adaptTo(Node.class);
					StringWriter stringWriter = new StringWriter();
					JsonItemWriter jsonWriter = new JsonItemWriter(null);

					try {
						jsonWriter.dump(node, stringWriter, -1);
						JSONObject jsonObject = new JSONObject(stringWriter.toString());
						
						Iterator<String> iterator = jsonObject.keys();

						while (iterator.hasNext()) {
							String result = iterator.next();

							if (result.contains(myFile)) {

								JSONObject imageObject = (JSONObject) jsonObject.get(result);
								JSONObject imageObject1 = new JSONObject(imageObject.toString());
								Iterator<?> keysIterator = imageObject1.keys();
								while (keysIterator.hasNext()) {
									String key = (String) keysIterator.next();
									if (imageObject1.get(key) instanceof JSONObject) {
										JSONObject metaData = new JSONObject(imageObject1.get(key).toString());
										JSONObject metaDataObject = new JSONObject(metaData.toString());
										Iterator<?> keysiter = metaDataObject.keys();
										while (keysiter.hasNext()) {
											String keyiter = (String) keysiter.next();
											if (metaDataObject.get(keyiter) instanceof JSONObject) {
												String desc = metaDataObject.getString("metadata");
												if (desc.contains("dc:description")) {
													JSONObject descObject = new JSONObject(desc);													
													description = descObject.getString("dc:description");
												} else {
													String image = result.substring(0, result.lastIndexOf('.'));
													description = image;
												}
											}

										}
									}
								}							
								urlResult = baseUrl + "/" + result;
								
								LOGGER.info("getImages");
							
								imageGallery.put(urlResult, description);
							}

						}

					} catch (RepositoryException | JSONException e) {
						LOGGER.error("Could not create JSON", e);
					}
				}

			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// TODO Auto-generated method stub
		return imageGallery;
	}
	
	
	
	@Override
	public Map<String, String> getImageDescriptions(SlingHttpServletRequest request, String url, Page currentPage) {

		Session session = null;
		Map<String, String> imageGallery = new HashMap<String, String>();
		String description = "";
		String urlResult = "";
		try {

			session = repository.loginAdministrative(null);

			Node root = session.getNode(url);

			if (root != null) {
				ResourceResolver resourceResolver = request.getResourceResolver();
				Resource resource = resourceResolver.getResource(url);

				if (resource != null) {
					Node node = resource.adaptTo(Node.class);
					StringWriter stringWriter = new StringWriter();
					JsonItemWriter jsonWriter = new JsonItemWriter(null);

					try {
						jsonWriter.dump(node, stringWriter, -1);
						JSONObject jsonObject = new JSONObject(stringWriter.toString());
						Iterator<String> iterator = jsonObject.keys();

						while (iterator.hasNext()) {
							String result = iterator.next();

							if (result.contains(".jpg")) {

								JSONObject imageObject = (JSONObject) jsonObject.get(result);
								JSONObject imageObject1 = new JSONObject(imageObject.toString());
								Iterator<?> keysIterator = imageObject1.keys();
								while (keysIterator.hasNext()) {
									String key = (String) keysIterator.next();
									if (imageObject1.get(key) instanceof JSONObject) {
										JSONObject metaData = new JSONObject(imageObject1.get(key).toString());
										JSONObject metaDataObject = new JSONObject(metaData.toString());
										Iterator<?> keysiter = metaDataObject.keys();
										while (keysiter.hasNext()) {
											String keyiter = (String) keysiter.next();
											if (metaDataObject.get(keyiter) instanceof JSONObject) {
												String desc = metaDataObject.getString("metadata");
												if (desc.contains("dc:description")) {
													JSONObject descObject = new JSONObject(desc);													
													description = descObject.getString("dc:description");													
												} else {
													String image = result.substring(0, result.lastIndexOf('.'));
													description = image;
												}
											}

										}
									}
								}
								
								urlResult = url + "/" + result;								
								imageGallery.put(urlResult, description);
								LOGGER.info("getImageDescriptions");
							}

						}

					} catch (RepositoryException | JSONException e) {
						LOGGER.error("Could not create JSON", e);
					}
				}

			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return imageGallery;
	}
	
	
	@Override
	public ArrayList<AuthorUserDetails> getUserDetails(SlingHttpServletRequest request) {
    	
    	ArrayList<AuthorUserDetails> users = new ArrayList<>();    
    	
    	try {
            
    	//	 LOGGER.info(" getUserDetails ----" );
    		 
             ResourceResolver resourceResolver = null;
         	        
              
           	resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
 			//Session session = resourceResolver.adaptTo(Session.class);
     		
     		UserManager userManager = resourceResolver.adaptTo(UserManager.class);
     		Authorizable authorizable= userManager.getAuthorizable("article-contributor");
     		
     		org.apache.jackrabbit.api.security.user.Group group = (org.apache.jackrabbit.api.security.user.Group) authorizable;
             Iterator<Authorizable> itr = group.getMembers();
                        
            // LOGGER.info(" TEST " + itr.hasNext());
             
             String scheme = request.getScheme();
				String serverName = request.getServerName();
				int portNumber = request.getServerPort();
				String contextPath = request.getContextPath();
				
				String baseUrl = scheme + "://"  + serverName + ":" +portNumber;
             
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
					
						image = baseUrl  +user.getPath() + "/profile/photos/primary/image";
						
						
					
 				
 					users.add(new AuthorUserDetails(userId, firstName, lastName, email, aboutMe, image));
 			                   
                 }
             }
             
         } 
         catch (Exception e) {
             LOGGER.error("UserServlet getUserDetails ", e);
         }
       
        return users;
    }
	
}
