"use strict";

use(function () {
    var cardObj = {};

    if (this.type == 'marketingtile') {
        cardObj.className = 'tile tile--card';
    }

    if (this.type === 'tile') {
        cardObj.className = 'tile tile--card tile--clickable';
    }

    if (this.type === 'hero') {
        cardObj.className = 'tile tile--image-side text-center';
    }

    if (this.type === 'oneup' || this.type == 'oneup1') {
        cardObj.parentClassName = 'card one-up';
        cardObj.className = 'tile tile--card tile--image-side';
    }

    if (this.type === 'card') {
        cardObj.className = 'tile tile--card';
    }

    if (this.type != 'tile') {
        switch(this.imageLocation) {
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
    }

    if (this.type === 'oneup' && this.theme) {
        cardObj.className += " " + this.theme;     
    }

    if (this.metaText != null && this.type !== 'marketingtile' && this.type !== 'tile' && this.type !== 'oneup' && this.type !== 'oneup1') {
        if (this.metaLocation == 'belowHeading') {
            cardObj.className += ' tile--meta-below'
        }
        if (this.metaLocation == 'aboveHeading') {
            cardObj.className += ' tile--meta-above'
        }
    }

    cardObj.editing =  this.wcmmode == true || this.touch == true || this.preview == true;

    if (this.hideCard == 'true' && cardObj.editing == true) {
        cardObj.className += ' card-hidden'
    }

    return cardObj;
});
