package au.com.auspost.startrack_corp.core.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

// Code behind for solutions-builder
public class SolutionsBuilder extends WCMUsePojo {
    private List<HashMap<String, Object>> items;
    private Logger log;

    @Override
    public void activate() throws Exception {
        log = LoggerFactory.getLogger(SolutionsBuilder.class);
        log.debug("SolutionsBuilder::activate is called");
        
        Resource resource = getResource();
        if (resource == null) {
            log.error("Current resource isn't found");
            return;
        }

        Node currentNode = resource.adaptTo(Node.class);
        
        try {
            @SuppressWarnings("unchecked")
            Iterator<Property> pit = (Iterator<Property>)currentNode.getProperties();
            Iterable<Property> pi = () -> pit;
            Integer c = StreamSupport.stream(pi.spliterator(), false).map(o -> {
                try {
                    return (o.getName().equals("heroTitle1"))? 1: 0;
                } catch (RepositoryException re) {
                    log.error("RepositoryException is happened");
                }
                return 0;
            }).reduce(0, (x, y) -> x + y);
            if (c == 0) {
                currentNode.setProperty("heroImage1Ref", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("heroTitle1", "How much do you currently spend on delivery?");
                currentNode.setProperty("heroText1", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("questionsTitle1", "How much do you currently spend on delivery?");
                currentNode.setProperty("question11ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question11Title", "Under $500 per month");
                currentNode.setProperty("question11Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question12ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question12Title", "Over $500 per month");
                currentNode.setProperty("question12Text", "Lorem ipsum dolor sit amet");

                currentNode.setProperty("heroImage2Ref", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("heroTitle2", "Who are your primary customers?");
                currentNode.setProperty("heroText2", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("questionsTitle2", "Who are your primary customers?");
                currentNode.setProperty("question21ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question21Title", "Businesses");
                currentNode.setProperty("question21Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question22ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question22Title", "Consumers");
                currentNode.setProperty("question22Text", "Lorem ipsum dolor sit amet");

                currentNode.setProperty("heroImage3Ref", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("heroTitle3", "What type of delivery do you need?");
                currentNode.setProperty("heroText3", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("questionsTitle3", "What type of delivery do you need?");
                currentNode.setProperty("question31ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question31Title", "Same day");
                currentNode.setProperty("question31Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question32ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question32Title", "Next day");
                currentNode.setProperty("question32Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question33ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question33Title", "Regular");
                currentNode.setProperty("question33Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question34ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question34Title", "Oversized or sensitive items");
                currentNode.setProperty("question34Text", "Lorem ipsum dolor sit amet");

                currentNode.setProperty("heroImage4Ref", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("heroTitle4", "Do you deliver overseas?");
                currentNode.setProperty("heroText4", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("questionsTitle4", "Do you deliver overseas?");
                currentNode.setProperty("question41ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question41Title", "Yes");
                currentNode.setProperty("question41Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question42ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question42Title", "No");
                currentNode.setProperty("question42Text", "Lorem ipsum dolor sit amet");

                currentNode.setProperty("heroImage5Ref", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("heroTitle5", "Are you looking for eCommerce solutions?");
                currentNode.setProperty("heroText5", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("questionsTitle5", "Are you looking for eCommerce solutions?");
                currentNode.setProperty("question51ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question51Title", "End to end");
                currentNode.setProperty("question51Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question52ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question52Title", "Online marketplaces");
                currentNode.setProperty("question52Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question53ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question53Title", "API integration");
                currentNode.setProperty("question53Text", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("question54ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("question54Title", "Data marketing services");
                currentNode.setProperty("question54Text", "Lorem ipsum dolor sit amet");

                currentNode.setProperty("recomendationsImageRef1", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle1", "Click & Send");
                currentNode.setProperty("recomendationsText1", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading1", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText1", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl1", "http://google.com");
                currentNode.setProperty("recomendation11ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation11Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation11Url", "http://google.com");
                currentNode.setProperty("recomendation12ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation12Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation12Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef2", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle2", "Red Domestic");
                currentNode.setProperty("recomendationsText2", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading2", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText2", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl2", "http://google.com");
                currentNode.setProperty("recomendation21ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation21Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation21Url", "http://google.com");
                currentNode.setProperty("recomendation22ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation22Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation22Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef3", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle3", "Red International");
                currentNode.setProperty("recomendationsText3", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading3", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText3", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl3", "http://google.com");
                currentNode.setProperty("recomendation31ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation31Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation31Url", "http://google.com");
                currentNode.setProperty("recomendation32ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation32Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation32Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef4", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle4", "B2B Logistics");
                currentNode.setProperty("recomendationsText4", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading4", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText4", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl4", "http://google.com");
                currentNode.setProperty("recomendation41ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation41Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation41Url", "http://google.com");
                currentNode.setProperty("recomendation42ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation42Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation42Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef5", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle5", "B2C Logistics");
                currentNode.setProperty("recomendationsText5", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading5", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText5", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl5", "http://google.com");
                currentNode.setProperty("recomendation51ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation51Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation51Url", "http://google.com");
                currentNode.setProperty("recomendation52ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation52Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation52Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef6", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle6", "StarTrack Courier");
                currentNode.setProperty("recomendationsText6", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading6", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText6", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl6", "http://google.com");
                currentNode.setProperty("recomendation61ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation61Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation61Url", "http://google.com");
                currentNode.setProperty("recomendation62ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation62Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation62Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef7", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle7", "Next Flight");
                currentNode.setProperty("recomendationsText7", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading7", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText7", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl7", "http://google.com");
                currentNode.setProperty("recomendation71ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation71Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation71Url", "http://google.com");
                currentNode.setProperty("recomendation72ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation72Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation72Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef8", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle8", "Premium");
                currentNode.setProperty("recomendationsText8", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading8", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText8", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl8", "http://google.com");
                currentNode.setProperty("recomendation81ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation81Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation81Url", "http://google.com");
                currentNode.setProperty("recomendation82ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation82Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation82Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef9", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle9", "StarTrack Express");
                currentNode.setProperty("recomendationsText9", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading9", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText9", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl9", "http://google.com");
                currentNode.setProperty("recomendation91ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation91Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation91Url", "http://google.com");
                currentNode.setProperty("recomendation92ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation92Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation92Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef10", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle10", "Specialty Services");
                currentNode.setProperty("recomendationsText10", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading10", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText10", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl10", "http://google.com");
                currentNode.setProperty("recomendation101ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation101Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation101Url", "http://google.com");
                currentNode.setProperty("recomendation102ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation102Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation102Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef11", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle11", "International");
                currentNode.setProperty("recomendationsText11", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading11", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText11", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl11", "http://google.com");
                currentNode.setProperty("recomendation111ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation111Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation111Url", "http://google.com");
                currentNode.setProperty("recomendation112ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation112Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation112Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef12", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle12", "Platforms");
                currentNode.setProperty("recomendationsText12", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading12", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText12", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl12", "http://google.com");
                currentNode.setProperty("recomendation121ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation121Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation121Url", "http://google.com");
                currentNode.setProperty("recomendation122ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation122Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation122Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef13", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle13", "Marketplaces");
                currentNode.setProperty("recomendationsText13", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading13", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText13", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl13", "http://google.com");
                currentNode.setProperty("recomendation131ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation131Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation131Url", "http://google.com");
                currentNode.setProperty("recomendation132ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation132Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation132Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef14", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle14", "API's");
                currentNode.setProperty("recomendationsText14", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading14", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText14", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl14", "http://google.com");
                currentNode.setProperty("recomendation141ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation141Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation141Url", "http://google.com");
                currentNode.setProperty("recomendation142ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation142Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation142Url", "http://google.com");

                currentNode.setProperty("recomendationsImageRef15", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendationsTitle15", "Data Marketig Services");
                currentNode.setProperty("recomendationsText15", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labo.");
                currentNode.setProperty("recomendationsHeading15", "Recommended for you");
                currentNode.setProperty("recomendationsLinkText15", "Get started today");
                currentNode.setProperty("recomendationsLinkUrl15", "http://google.com");
                currentNode.setProperty("recomendation151ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation151Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation151Url", "http://google.com");
                currentNode.setProperty("recomendation152ImageRef", "/etc/designs/startrack_corp/clientlib/img/test-rectangle-4-3.png");
                currentNode.setProperty("recomendation152Title", "Lorem ipsum dolor sit amet");
                currentNode.setProperty("recomendation152Url", "http://google.com");
            }
        } catch (RepositoryException re) {
            log.error("RepositoryException is happened");
        }
    }

    public List<HashMap<String, Object>> getItems() {
        return items;
    }
}
