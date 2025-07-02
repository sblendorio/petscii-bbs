package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

@Hidden
public class FactaNewsAscii extends WordpressProxyAscii {

    public FactaNewsAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://facta.news";
        this.showAuthor = false;
        this.pageSize = 5;
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

    private static final byte[] LOGO_BYTES = "Facta.news".getBytes(StandardCharsets.ISO_8859_1);

}
