(function($, window){
    window.EventPhotoCarousel = function(elm, options){
        this.elm = elm;
        this.imageListContainerElm = this.elm.children(".photo-carousel-list-container")
        this.imageListElm = this.imageListContainerElm.children(".image-carousel-list")
        this.imageDisplayElm = this.elm.children(".current-image-widget").children(".current-image-container")
        this.arrowIndicatorElm = this.elm.children(".bouncing-arrow")
        this.options = options;
    }

    EventPhotoCarousel.prototype.addImage = function(imageAddress){
        var self = this;
        var imageString = '<div class="photo-gallery-image-widget">' +
            				'<div class="photo-gallery-image-wrapper">' +
                                '<div class="backdrop"></div>' +
                                '<img class="photo-gallery-image" src="' + imageAddress +'" style="height:' + this.options.thumbnailHeight +'"/>'+
                            '</div>'
        				  '</div>'
        var bigImageString ='<div >' +
            			  	'<img class="photo-image" src="' + imageAddress +'" style="height:' + this.options.mainPhotoHeight +'"/>'+
        				  	'</div>'
		$(imageString).appendTo(self.imageListElm)
        $(bigImageString).appendTo(self.imageDisplayElm)
    }

    EventPhotoCarousel.prototype.init = function(){
        var self = this;
		$(document).ready(function(){
            self.imageListElm.slick({
                slidesToShow: self.options.thumbnailToShow,
                arrows: true,
                slidesToScroll: 1,
                asNavFor: self.imageDisplayElm,
  				focusOnSelect: true,
                centerMode: true,
            });
            self.imageDisplayElm.slick({
                slidesToShow: 1,
                arrows: false,
                slidesToScroll: 1,
                asNavFor: self.imageListElm
            })

            self.arrowIndicatorElm.click(function(e){
                event.stopPropagation();
                self.imageListContainerElm.removeClass("closed")
                self.arrowIndicatorElm.hide()
            });

            self.imageDisplayElm.click(function(e){
                event.stopPropagation();
                self.imageListContainerElm.addClass("closed")
                self.arrowIndicatorElm.delay(300).fadeIn(300)
            });

            $(document).on('click', function(event) {
                if (!$(event.target).closest(self.imageListElm).length) {
                    self.imageListContainerElm.addClass("closed")
                    self.arrowIndicatorElm.delay(300).fadeIn(300)
                }
            });
        })
    }

}(jQuery, window))