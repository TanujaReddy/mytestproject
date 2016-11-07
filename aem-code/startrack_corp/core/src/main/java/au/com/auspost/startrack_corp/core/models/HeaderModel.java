package au.com.auspost.startrack_corp.core.models;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.api.resource.Resource;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables=Resource.class)
public class HeaderModel {
    @Inject @Optional @Named("jcr:title")
    public String title;

    @Inject @Optional @Named("jcr:menutitle")
    public String menutitle;

    @Inject @Optional @Named("externalLink")
    public String externalLink;

    @Inject @Optional @Named("thumbnailImagePath")
    public String thumbnailImagePath;
}
