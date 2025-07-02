package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

@Hidden
public class FactaNews extends WordpressProxy {

    public FactaNews() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://facta.news";
        this.pageSize = 5;
        this.screenLines = 18;
        this.showAuthor = false;
    }

    @Override
    public String extractContent(JSONObject postJ) {
        JSONObject acf = (JSONObject) postJ.get("acf");
        JSONArray composer = (JSONArray) acf.get("composer");
        String text = "";
        for (int i=0; i<composer.size(); ++i) {
            JSONObject item = (JSONObject) composer.get(i);
            String acf_fc_layout = (String) item.get("acf_fc_layout");
            if ("testo".equals(acf_fc_layout)) {
                text += (String) item.get("testo");
                text += "<br>";
            }
        }
        return text.replaceAll("(?is)(\\[/?vc_[^]]*\\])*", EMPTY);
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        18, 5, 32, 32, 32, -110, -95, -84, 18, 32, -68, -110, 32, 32, -84, 18,
        32, 32, 32, -110, -69, 18, -95, 32, 32, 32, 32, -110, 32, 18, -98, -69,
        -68, -110, 13, 18, 5, 32, -110, -95, 32, 32, 18, -66, -110, -66, 18, -69,
        -110, -69, 32, 18, 32, -84, -110, 32, -68, 18, -94, -110, 32, 32, 18, 32,
        -110, -95, 32, 32, -98, -68, 18, 32, -110, -69, 13, 18, 5, 32, 32, 32,
        -110, -84, 18, 32, -110, -94, 18, -66, -68, -110, 32, 18, 32, -110, -95, 32,
        32, 32, 32, 32, 18, 32, -110, -95, 32, -98, -94, 32, 18, -69, -68, -110,
        32, 32, 32, 32, 32, 18, -101, -68, -110, 32, -95, 18, -84, -94, -95, -110,
        32, 18, -95, -110, -84, 18, -94, -110, -66, 13, 18, 5, 32, -110, -95, 32,
        18, -66, -84, -94, -94, 32, -110, -69, 18, -69, 32, -110, -94, 18, -66, -84,
        -110, 32, 32, 18, 32, -110, -95, 18, -98, -95, 32, -110, -95, -68, 18, 32,
        -110, -69, 32, 32, -101, -84, -69, -95, -65, -95, 18, -84, -110, -66, 18, -95,
        -65, -66, -110, 32, 18, -94, -110, -69, 13, 18, 5, -94, -110, -66, 32, 18,
        -94, -110, -66, 32, 32, 18, -94, -110, -66, 32, 18, -94, -94, -94, -110, 32,
        32, 32, 18, -94, -110, -66, 32, 18, -98, -94, -110, 32, 32, 18, -94, -110,
        -66, 32, 32, -101, -68, -66, -66, 32, -66, 18, -94, -94, -110, -68, 32, -68,
        -68, 18, -94, -110, 13
    };

}
