'use strict';
/* Eventually this would be moved outside the application */
function omnitureOnClickTracking(e){
    if(typeof s_account!="undefined"){
        var s=s_gi(s_account);
        s.linkTrackVars="prop28,eVar8";
        s.eVar8=e;
        s.prop28 = s.eVar8;
        s.tl(!0,"o",e);
    }
}
function omnitureEventTracking(e){
    if(typeof s_account!="undefined"){
        var s=s_gi(s_account);
        s.linkTrackVars="events";
        s.linkTrackEvents=e;
        s.events=e;
        s.tl(!0,"o", e);
    }
}