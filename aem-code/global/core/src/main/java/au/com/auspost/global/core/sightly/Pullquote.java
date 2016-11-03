package au.com.auspost.global.core.sightly;

import au.com.auspost.global.utils.Constants;
import com.adobe.cq.sightly.WCMUse;
import org.apache.commons.lang3.StringUtils;

public class Pullquote extends WCMUse{

    private String text;
    private String imagePath;
    private String authorName;
    private String meta;

    @Override
    public void activate() throws Exception {
        text = getProperties().get("text", Constants.EMPTY);
        imagePath = getProperties().get("imagePathRef", Constants.EMPTY);
        authorName = getProperties().get("authorName", Constants.EMPTY);
        meta = getProperties().get("meta", Constants.EMPTY);
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(text);
    }

    public boolean isTestimonial() {
        return StringUtils.isNotBlank(text) && StringUtils.isNotBlank(imagePath);
    }

    public boolean isQuote() {
        return StringUtils.isNotBlank(text) && StringUtils.isBlank(imagePath);
    }

    public boolean hasAuthorName() {
        return StringUtils.isNotBlank(authorName);
    }

    public boolean hasMeta() {
        return StringUtils.isNotBlank(meta);
    }

    public boolean hasAuthorDetails() {
        return hasAuthorName() || hasMeta();
    }

}
