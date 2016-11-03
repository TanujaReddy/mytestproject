package au.com.auspost.startrack_corp.core.sightly;

import com.adobe.cq.sightly.WCMUse;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is used to dynamically map rgba color code to color class name for CustomColorPicker component.
 *
 eg :
 * <div data-sly-use.colorMap="${'au.com.auspost.startrack_global.core.sightly.ColorMappingReader'}" data-sly-unwrap>
 *   <section class="container bg-colour--${colorMap.colorClass}">
 */

public class StartrackColorMappingReader extends WCMUse {

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String TRANSPARENT_BACKGROUND = "transparentBg";
    private static final String BOOLEAN_TRUE = "true";
    private static final String TRANSPARENT_COLOR = "transparent";

    private String colorClass;
    private static HashMap<String, String> colorMaps;
        static
        {
            colorMaps = new HashMap<String, String>();
                       
            /*startrack - colors
            colorMaps.put("--mid-blue", "rgba(0,159,218,1)"); //--blue
            colorMaps.put("--dark-navy", "rgba(0,51,89,1)"); //--dark-blue
            colorMaps.put("--dark-charcoal", "rgba(24,10,17,1)");
            colorMaps.put("--mid-charcoal", "rgba(102,102,102,1)");
            colorMaps.put("--light-charcoal", "rgba(224,224,224,1)");
            colorMaps.put("--light-navy", "rgba(0,143,194,1)");
            colorMaps.put("--mid-tan", "rgba(242,240,237,1)");
            colorMaps.put("--light-tan", "rgba(248,247,246,1)");
            colorMaps.put("--dark-slate", "rgba(208,212,216,1)");
            colorMaps.put("--light-slate", "rgba(243,244,245,1)"); //--cool-slate
            colorMaps.put("--red", "rgba(220,25,40,1)"); //--parcel-post
            colorMaps.put("--green", "rgba(6,122,61,1)");
            colorMaps.put("--light-blue", "rgba(153,217,240,1)");
            colorMaps.put("--dark-yellow", "rgba(255,218,117,1)");
            colorMaps.put("--light-yellow", "rgba(255,245,222,1)");
            colorMaps.put("--mid-teal", "rgba(138,237,226,1)");
            colorMaps.put("--dark-blue", "rgba(10,69,113,1)");*/
            colorMaps.put("--mid-tan", "rgba(242,240,237,1)");
            colorMaps.put("--mid-blue", "rgba(0,159,218,1)");
            colorMaps.put("", "rgba(255,255,255,1)");
        }

    @Override
    public void activate() throws Exception {
        String colorRgba = getProperties().get(BACKGROUND_COLOR, StringUtils.EMPTY);
        String transparentBg = getProperties().get(TRANSPARENT_BACKGROUND, StringUtils.EMPTY);
        colorClass = getColorMapping(colorRgba, transparentBg);
    }

    public String getColorClass() {
        return colorClass;
    }

/*
    This method returns a color class name for the color selected in customcolorpicker component
    * @param String  parameter
    * @return  String with colorClass
*/
    public String getColorMapping(String colorRgba, String transparentBg) {
        String colorClassName = StringUtils.EMPTY;
        if(!colorMaps.isEmpty() && colorMaps.size()>0 && !transparentBg.equals(BOOLEAN_TRUE)) {
            for (Map.Entry<String, String> colorMap : colorMaps.entrySet()) {
                String colorMapKey = colorMap.getKey();
                String colorMapValue = colorMap.getValue();
                if (colorRgba.equals(colorMapValue)) {
                    colorClassName = colorMapKey;
                }
            }
        } else {
            colorClassName = TRANSPARENT_COLOR;
        }
        return colorClassName;
    }


}