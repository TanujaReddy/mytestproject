package au.com.auspost.global.core.sightly;

import com.adobe.cq.sightly.WCMUse;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is used to dynamically map rgba color code to color class name for CustomColorPicker component.
 *
 eg :
 * <div data-sly-use.colorMap="${'au.com.auspost.global.core.sightly.ColorMappingReader'}" data-sly-unwrap>
 *   <section class="container bg-colour--${colorMap.colorClass}">
 */

public class ColorMappingReader extends WCMUse {

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String TRANSPARENT_BACKGROUND = "transparentBg";
    private static final String BOOLEAN_TRUE = "true";
    private static final String TRANSPARENT_COLOR = "transparent";

    private String colorClass;
    private static HashMap<String, String> colorMaps;
        static
        {
            colorMaps = new HashMap<String, String>();
            colorMaps.put("--dark-yellow", "rgba(234,176,42,1)");
            colorMaps.put("--yellow", "rgba(255,213,0,1)");
            colorMaps.put("--light-yellow", "rgba(255,219,118,1)");
            colorMaps.put("--dark-leaf", "rgba(21,87,52,1)");
            colorMaps.put("--leaf", "rgba(0,172,62,1)");
            colorMaps.put("--light-leaf", "rgba(153,222,178,1)");
            colorMaps.put("--dark-purple", "rgba(80,68,222,1)");
            colorMaps.put("--purple", "rgba(129,119,231,1)");
            colorMaps.put("--light-purple", "rgba(205,201,245,1)");
            colorMaps.put("--dark-blue", "rgba(0,51,89,1)");
            colorMaps.put("--blue", "rgba(0,159,218,1)");
            colorMaps.put("--light-blue", "rgba(153,217,240,1)");
            colorMaps.put("--dark-teal", "rgba(0,193,170,1)");
            colorMaps.put("--teal", "rgba(0,229,183,1)");
            colorMaps.put("--light-teal", "rgba(130,239,227,1)");
            colorMaps.put("--dark-orange", "rgba(252,98,46,1)");
            colorMaps.put("--orange", "rgba(255,144,0,1)");
            colorMaps.put("--light-orange", "rgba(254,192,171,1)");
            colorMaps.put("--dark-pink", "rgba(238,46,127,1)");
            colorMaps.put("--pink", "rgba(240,91,125,1)");
            colorMaps.put("--light-pink", "rgba(248,171,204,1)");
            colorMaps.put("--parcel-post", "rgba(220,25,40,1)");
            colorMaps.put("--courier-post", "rgba(81,83,74,1)");
            colorMaps.put("--warm-grey-2", "rgba(226,223,218,1)");
            colorMaps.put("--warm-grey-1", "rgba(243,241,238,1)");
            colorMaps.put("--light-grey", "rgba(249,248,247,1)");
            colorMaps.put("--dark-slate", "rgba(83,95,103,1)");
            colorMaps.put("--slate", "rgba(138,147,157,1)");
            colorMaps.put("--light-slate", "rgba(208,213,216,1)");
            colorMaps.put("--cool-slate", "rgba(243,244,245,1)");
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