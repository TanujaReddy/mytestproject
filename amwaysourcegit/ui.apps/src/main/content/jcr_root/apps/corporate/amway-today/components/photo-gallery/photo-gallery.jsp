<%--

  Image Caroulsel component.

  component to display multiple images

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<%@ page import="com.day.cq.wcm.foundation.Placeholder,
                 org.strut.amway.core.util.AEMUtils,
                 org.apache.sling.settings.SlingSettingsService" %>
<% 
	String[] images = properties.get("images", new String[0]); // default is empty array
	int thumbnailHeight = properties.get("thumbnailheight", -1);
	String thumbnailHeightProp = "auto";
	if(thumbnailHeight >= 0)
        thumbnailHeightProp = thumbnailHeight + "px";

	int mainPhotoHeight = properties.get("mainphotoheight", -1);
	int slidesToShow = properties.get("slidesToShow", 3);

	if(slidesToShow > images.length)
	    slidesToShow = images.length;

	String mainPhotoHeightProp = "auto";
	if(mainPhotoHeight >= 0)
		mainPhotoHeightProp = mainPhotoHeight + "px";

	if(images.length > 0){

%>
<div class="photo-carousel-container">
    <div class="current-image-widget">
        <div class="current-image-container" style="height:auto"></div>
    </div>
    <div class="bouncing-arrow animated bounce">
        <div class="arrow-down">
        </div>
    </div>
    <div class="photo-carousel-list-container closed">
        <div class="image-carousel-list">
        </div>
    </div>

       <cq:includeClientLib js="apps.strut-amway.photo-gallery"/>
		<script type="text/javascript">
            var registerImageCarousel = function(){

                var scriptTag = document.scripts[document.scripts.length - 1];
                var parentNode = $(scriptTag.parentNode);

                var options = {
                    thumbnailToShow: <%= slidesToShow %>,
                    thumbnailHeight: "<%= thumbnailHeightProp %>",
                    mainPhotoHeight: "<%= mainPhotoHeightProp %>",
                }

                var eventPhotoGallery = new EventPhotoCarousel(parentNode, options)
                <% for(int i = 0; i < images.length; ++i){ %>
					eventPhotoGallery.addImage("<%= images[i] %>")
                <% } %>
                eventPhotoGallery.init()

            }
            registerImageCarousel()
        </script>
</div>


<% 
    }else{
        boolean isAuthoring = AEMUtils.isAuthor(sling.getService(SlingSettingsService.class));
        if (isAuthoring) {
%>
<div class="current-image-widget" style="border:2px dotted gray">
      <div class="current-image-container" style="height:auto">
          <div style="width:100%;padding:40px 0">
                <h1 class="text-center">Add Images</h1>
          </div>
      </div>
</div>
<div class="bouncing-arrow animated bounce">
  <div class="arrow-down">
  </div>
</div>
<div class="photo-carousel-list-container closed">
      <div class="image-carousel-list">
      </div>
</div>

<%
        }
    }
%>
