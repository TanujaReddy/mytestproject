package org.strut.amway.core.json.parser.login;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONObject;
import org.strut.amway.core.json.parser.JsonParser;
import org.strut.amway.core.model.LoginResult;

public class LoginJsonParser implements JsonParser<LoginResult> {

    private static int SUCCESS = 1;
    private static int FAIL = 0;

    @Override
    public String parse(final ResourceResolver resourceResolver, final LoginResult loginResult) throws Exception {
        final JSONObject json = new JSONObject();

        if (loginResult != null) {
            if (loginResult.isSuccess()) {
                json.put("success", SUCCESS);
                json.put("username", loginResult.getUsername());
            } else {
                json.put("success", FAIL);
                json.put("error", loginResult.getErrorMsg());
            }
        }
        return json.toString();
    }

}
