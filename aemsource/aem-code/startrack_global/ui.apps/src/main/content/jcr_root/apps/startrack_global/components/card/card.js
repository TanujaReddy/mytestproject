"use strict";

use(function () {
    var cardObj = {};

    if(this.type == 'tile'){
        cardObj.className = 'tile';
    }

    if(this.type == 'card'){
        cardObj.className = 'tile tile--card';
    }

    switch(this.imageLocation){
        case 'top':
            cardObj.className += ' tile--image-top';
            break;
        case 'left':
            cardObj.className += ' tile--image-left';
            break;
        case 'bottom':
            cardObj.className += ' tile--image-bottom';
            break;
        case 'right':
            cardObj.className += ' tile--image-right';
            break;
        default:
    }


    if(this.metaText != null){
        if(this.metaLocation == 'belowHeading'){
            cardObj.className += ' tile--meta-below'
        }

        if(this.metaLocation == 'aboveHeading'){
            cardObj.className += ' tile--meta-above'
        }
    }


    cardObj.editing =  this.wcmmode == true || this.touch == true || this.preview == true;

    if(this.hideCard == 'true' && cardObj.editing == true){
        cardObj.className += ' card-hidden'
    }


    return cardObj;
});


