<%--

  Image Caroulsel component.

  component to display multiple images

--%>
<link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/fancybox/2.1.5/jquery.fancybox.css" type="text/css" media="screen" />
<%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<%@page contentType="text/html; charset=utf-8" import="com.day.cq.wcm.foundation.Placeholder,
                 org.strut.amway.core.util.AEMUtils,
                 org.strut.amway.core.services.ArticleService,
                 java.util.ArrayList,
                 java.util.HashMap,
                 java.util.Map,
                 org.apache.sling.settings.SlingSettingsService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<style>
	.gallery-container{
		overflow: auto;
	}
	
	@media screen and (max-width: 600px){
		.gallery-container{
			overflow: auto;
			height:auto;
		}
	}
	
	a.lightbox-link{
	    display: inline-block;
    	float: left;
    	padding: 10px;
    }
    
    a.fancybox{
		float: left;
	}

	.img-square{
		position: relative;
		width: 150px;
		height: 150px;
		overflow: hidden;
		margin: 10px 10px 10px 0;
	}

	.img-square img{
		position: absolute;
		max-width: initial;
	    height: inherit;
	}
    

</style>
<% 
String url ="";
	Locale pageLocale = currentPage.getLanguage(false);
	//If above bool is set to true. CQ looks in to page path rather than jcr:language property.
	ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);


     final ArticleService articleService = sling.getService(ArticleService.class);
    
     
//	 ArrayList imageGall = new ArrayList();
 Map<String,String> imageGallery = new HashMap<String,String>();
 
    
    String[] images = properties.get("images", new String[0]); // default is empty array
    
    int thumbnailHeight = properties.get("thumbnailheight", -1);
	String thumbnailHeightProp = "auto";
	if(thumbnailHeight >= 0)
        thumbnailHeightProp = thumbnailHeight + "px";
    
    for(int i = 0; i < images.length; ++i) {
    	 url = images[i];  	
    	if (url.contains(".jpg")) {
    		imageGallery = articleService.getImages(slingRequest,url, currentPage);
    		
    	} else {
    		imageGallery = articleService.getImageDescriptions(slingRequest,url, currentPage);
    		
    	}
    }
	
	if(imageGallery.size() > 0){

%>

<div class="gallery-container">
       
          
           <% for (Map.Entry<String, String> entry : imageGallery.entrySet()){ %>
		<a class="fancybox lightbox-link" href="<%=entry.getKey() %>"
		rel="gallery" title="<%=entry.getValue() %>">
		<div class="img-square" style="width:<%=thumbnailHeightProp%>;height:<%=thumbnailHeightProp%>;" >
		<img src="/etc/designs/corporate/amway-today/images/blank.gif"
		data-src="<%=entry.getKey() %>">
		</div>
		</a>
	<%} %>
          
          
          
        
      </div>
<% 
    } else{
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
<div class="col-md-12">
      <div class="vertical-toolbar-information hidden-print article-page-button-group">
<div class="btn-group w100pc-fixed btn-group-justified pull-left navigate-button-group" style="width: 100px;">
 <a href="#" data-type="downloadall" class="btn navigates-blog download-all">Download All</a>
</div>
</div>
</div>


<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/fancybox/2.1.5/jquery.fancybox.pack.js"></script>
<script type="text/javascript" src="/apps/corporate/amway-today/components/image-gallery/clientlibs/js/jszip.js"></script>
<script type="text/javascript" src="/apps/corporate/amway-today/components/image-gallery/clientlibs/js/FileSaver.js"></script>

<script type="text/javascript">

jQuery(function($) {
  if(!JSZip.support.blob) {
      $("#demo-not-supported").removeClass("hidden");
      $("#demo").hide();
      return;
  }
  $("#demo").click(function () {
      try {
          eval($("#demo-code").val());
          $("#status")
          .removeClass()
          .addClass("text-success")
          .text("Done!");
      }
      catch (e) {
          $("#status")
          .removeClass()
          .addClass("text-danger")
          .text(e);
      }
  });
});

    $(document).ready(function() {

    	var lightBox = $('.fancybox');

        lightBox.fancybox({
        	afterLoad: function() {
        		var a = document.createElement('a');
        		if (typeof a.download != "undefined") {
		        	this.title = '<a class="download-image-link" href="' + this.href + '" download>'+this.title+'</a> ';
			    } else{
			    	var href = this.href;
			    	this.title = '<a class="download-image-link" href="' + this.href + '" download>'+this.title+'</a> ' ;
			    	
			    }
		    },
		    helpers:{
		    	title:{
		    		type: 'inside'
		    	}
		    }
        });
    });

    function isElementVisible(el){
    	var element = el.getBoundingClientRect();

    	return (
    		element.top >= 0 &&
    		element.left >= 0 &&
    		element.bottom <= (window.innerHeight || document.documentElemen.clientHeight) &&
    		element.right <= (window.innerWidth || document.documentElement.clientWidth)
    		);
    }

    function elementInViewportIE(el) {
	  var top = el.offsetTop;
	  var left = el.offsetLeft;
	  var width = el.offsetWidth;
	  var height = el.offsetHeight;

	  while(el.offsetParent) {
	    el = el.offsetParent;
	    top += el.offsetTop;
	    left += el.offsetLeft;
	  }

	  return (
	    top >= window.pageYOffset &&
	    left >= window.pageXOffset &&
	    (top + height) <= (window.pageYOffset + window.innerHeight) &&
	    (left + width) <= (window.pageXOffset + window.innerWidth)
	  );
	}

    function loadImage(el){
    	el.src = '';
    	var img = new Image();
    	var src = el.getAttribute('data-src');
    	img.onload = function(){
    		if(!! el.parent){
    			el.parent.replaceChild(img, el);
    		} else{
    			el.src = src;
    		}
    	}
    	img.src = src;
    	el.src = src;
    }

    function loadGallery(){
    	var images = document.querySelectorAll('.gallery-container img');
    	if(images.forEach){
	    	images.forEach(function(el){
	    		var isVisible = isElementVisible(el);
	    		if(isVisible){
	    			setTimeout(function(){
	    				loadImage(el);
	    			}, 500);
	    		}
	    	});
	    }else{
	    	for(var i = 0; images.length > i; i++){
	    		var isVisible = elementInViewportIE(images[i]);

	    		if(isVisible){
	    			function triggerLoadImage(param){loadImage(param);}
	    			setTimeout(triggerLoadImage, 3000, images[i]);
	    		}
	    	}
	    }
    }

    document.addEventListener('DOMContentLoaded', loadGallery, false);
    document.addEventListener('scroll', loadGallery, false);
    
    var downloadAll = document.querySelector('.download-all');
    downloadAll.addEventListener('click', function(e){
    
    e.preventDefault();
    serveZipFile();

    }, false);


    function serveZipFile(){

        var images = document.querySelectorAll('.gallery-container a');
    var zip = new JSZip();
    var folder = zip.folder('images');

        if([].forEach){
       images.forEach(function(currentImage){
       var url = currentImage.getAttribute('href');
       var urlShort = url.substring(url.lastIndexOf('/')+1);

       getDataUri(url, function(response){
       folder.file( urlShort, response, {base64: true});
       });
       });

       setTimeout(function(){
       zip.generateAsync({type:"blob"}).then(function (blob){
       console.log('we are here, blob', blob);
    saveAs(blob, "downloadImages.zip");
    });
    }, 1000);
       } else{
       console.log('Your browser does not support downloading zipped images. Please update your browser');
       }
        }

    function getDataUri(url, callback) {
       var image = new Image();

       image.onload = function () {
           var canvas = document.createElement('canvas');
           canvas.width = this.naturalWidth; 
           canvas.height = this.naturalHeight; 

           canvas.getContext('2d').drawImage(this, 0, 0);

           // Get raw image data
           callback(canvas.toDataURL('image/png').replace(/^data:image\/(png);base64,/, ''));
       };

       image.src = url;
    }



</script>
