package au.com.auspost.startrack_corp.core.services;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.sling.testing.tools.http.RequestBuilder;
import org.apache.sling.testing.tools.http.RequestExecutor;
import org.apache.sling.testing.tools.sling.SlingClient;

public class SearchServiceIntegrationTest extends SlingClient {

  private RequestBuilder builder;
  private RequestExecutor executor;
  private String username;
  private String password;

  public SearchServiceIntegrationTest(String slingServerUrl, String username, String password) {
    super(slingServerUrl, username, password);
    builder = new RequestBuilder(slingServerUrl);
    executor = new RequestExecutor(new DefaultHttpClient());
    this.username = username;
    this.password = password;
  }

  /**
   * Upload using a PUT request.
   * 
   * @param path
   *      the path of the uploaded file
   * @param data
   *      the content
   * @param length
   *      Use -1 if unknown
   * @param createFolders
   *      if true, intermediate folders are created via mkdirs
   */
  public void upload(String path, InputStream data, int length, boolean createFolders) throws IOException {
    final HttpEntity e = new InputStreamEntity(data, length);
    if (createFolders) {
      mkdirs(getParentPath(path));
    }
    HttpResponse response = executor.execute(
      builder.buildOtherRequest(new HttpPut(builder.buildUrl(path)))
          .withEntity(e).withCredentials(username, password))
      .getResponse();
    int status = response.getStatusLine().getStatusCode();
    Assert.assertTrue("Expected status code 201 or 202, received: "+status,status == 201 || status == 200);
  }
}