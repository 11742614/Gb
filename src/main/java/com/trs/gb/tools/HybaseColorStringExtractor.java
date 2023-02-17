package com.trs.gb.tools;

import com.trs.commons.hybase.Extractor;
import com.trs.commons.lang.utils.StringUtils;
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSException;
import com.trs.hybase.client.TRSRecord;
import com.trs.hybase.client.TRSResultSet;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Map;

public class HybaseColorStringExtractor implements Extractor {
    @Override
    public Object extractValue(TRSConnection tconn, TRSResultSet trsRs, TRSRecord trsRecord, String fieldName, Map<String, String> params) throws TRSException {
        String colorString = trsRecord.getString(fieldName);
        if (colorString == null)
            return null;
        return StringUtils.replaceEach(StringEscapeUtils.unescapeHtml4(StringUtils.replaceEach(colorString,new String[]{"&lt;", "&quot;","&nbsp;","/&gt;","&gt;"}, new String[]{"<", "\""," ","/>","/>"})) , new String[]{"<font color=red>", "</font>"}, new String[]{"<em class=\"hit\">", "</em>",});
    }
}
