package com.trs.gb.tools;

import com.trs.commons.hybase.Extractor;
import com.trs.commons.lang.utils.StringUtils;
import com.trs.gb.bean.JavaHitPoint;
import com.trs.hybase.client.TRSConnection;
import com.trs.hybase.client.TRSException;
import com.trs.hybase.client.TRSRecord;
import com.trs.hybase.client.TRSResultSet;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抽取带命中点的摘要.
 */
public class HybaseHitAbstractExtractor implements Extractor {

    // fields ---------------------------------------------------------------
    private int maxTotalLength;
    private int maxSpanCount;
    private String suffix = "";

    private static final Set<Character> SENTENCE_ENDS = new HashSet<Character>();

    static {
        SENTENCE_ENDS.add('.');
        SENTENCE_ENDS.add('。');
        SENTENCE_ENDS.add('!');
        SENTENCE_ENDS.add('！');
        SENTENCE_ENDS.add('?');
        SENTENCE_ENDS.add('？');
        SENTENCE_ENDS.add('\r');
        SENTENCE_ENDS.add('\n');
        SENTENCE_ENDS.add(';');
        SENTENCE_ENDS.add('；');
    }

    // methods --------------------------------------------------------------

    /**
     * 使用params中的关键词参数对字段值进行人工描红.
     * 关键词参数的名字由keywordName属性指定,默认是"name"。
     */
    @Override
    public Object extractValue(TRSConnection tconn, TRSResultSet trsRs, TRSRecord trsRecord, String fieldName, Map<String, String> params) throws TRSException {
        String colorString = trsRecord.getString(fieldName);
        if (colorString == null)
            return null;
        colorString = StringUtils.replace(colorString, "&nbsp;", " ");
        List<JavaHitPoint> hits = new ArrayList<>();
        String content = getHitPoints(colorString, hits);
        if (hits.isEmpty())
            return null;
        try {
            return getHitAbstract(content, hits.toArray(new JavaHitPoint[hits.size()]), maxTotalLength, maxSpanCount, suffix);
        } catch (UnsupportedEncodingException e) {
            throw new TRSOMException(e);
        }
    }

    private static String getHitPoints(String colorString, List<JavaHitPoint> hits) {
        StringBuilder contentBuilder = new StringBuilder();
        String regxExpr = "(<font color=red>)|(</font>)";
        Pattern pattern = Pattern.compile(regxExpr);
        Matcher matcher = pattern.matcher(colorString);
        int prevEnd = 0;
        JavaHitPoint hit = null;
        while (matcher.find()) {
            contentBuilder.append(colorString.substring(prevEnd, matcher.start()));
            if (matcher.group(1) != null) {
                hit = new JavaHitPoint();
                hit.iStart = contentBuilder.length();
            } else if (hit != null) {
                hit.iLength = contentBuilder.length() - hit.iStart;
                hits.add(hit);
                hit = null;
            }
            prevEnd = matcher.end();
        }
        if (prevEnd < colorString.length())
            contentBuilder.append(colorString.substring(prevEnd));
        return contentBuilder.toString();
    }

    public static String getHitAbstract(String content, JavaHitPoint[] javaHitPoints, int maxTotalLength, int maxSpanCount, String suffix) throws UnsupportedEncodingException {
        // 算法一：确保描红的结果包含最多的命中点
//		ArrayList<ArrayList<Integer>> spans=getBestSpans(javaHitPoints,0,javaHitPoints.length-1,maxTotalLength,maxSpanCount,suffix.length());
//		String abs="";
//		int totalSpansLength=getSpansLength(javaHitPoints,spans,0,spans.size()-1,suffix);
//		int restExtendLength=Math.max((maxTotalLength-totalSpansLength),0);
//		for(int i=0;i<spans.size();i++){
//			int perExtendLength=Math.max((restExtendLength/(spans.size()-i)),0);
//			RedString spanString=getHitStringWithRed(content,javaHitPoints,spans.get(i),perExtendLength);
//			restExtendLength-=(spanString.getHead()+spanString.getTail());
//			abs=abs+spanString.getRedString()+suffix;
//		}
//		return abs;
        // 算法二：确保描红的结果都是由完整的句子构成的
        int[] sentenceBound = new int[2];
        int prevEnd = 0;
        int prevPointEnd = 0;
        int restTotalLength = maxTotalLength;
        StringBuilder builder = new StringBuilder();
        do {
            ArrayList<Integer> bestSpan = findBestSpan(javaHitPoints, prevPointEnd, javaHitPoints.length - 1, restTotalLength);
            if (builder.length() > 0)
                builder.append(suffix);
            builder.append(buildSpanString(content, prevEnd, javaHitPoints, bestSpan, sentenceBound));
            prevEnd = sentenceBound[1];
            prevPointEnd = bestSpan.get(bestSpan.size() - 1) + 1;
            restTotalLength -= sentenceBound[1] - sentenceBound[0] + suffix.length();
        } while (restTotalLength > 0 && prevPointEnd < javaHitPoints.length - 1);
        return builder.toString();
    }

    private static ArrayList<Integer> findBestSpan(JavaHitPoint[] javaHitPoints, int begin, int end, int length) throws UnsupportedEncodingException {
        ArrayList<Integer> bestSpan = new ArrayList<Integer>();
        for (int i = begin; i <= end; i++) {
            ArrayList<Integer> curSpan = new ArrayList<Integer>();
            for (int j = i; j <= end; j++) {
                if (curSpan.isEmpty()) {
                    curSpan.add(j);
                } else {
                    JavaHitPoint curHit = javaHitPoints[j];
                    JavaHitPoint spanHeadHit = javaHitPoints[curSpan.get(0)];
                    int spanLength = curHit.iStart - spanHeadHit.iStart + curHit.iLength;
                    if (spanLength > length)
                        break;
                    else {
                        curSpan.add(j);
                    }
                }
            }
            if (curSpan.size() > bestSpan.size())
                bestSpan = curSpan;
            else if (curSpan.size() == bestSpan.size()) {
                int curSpanLength = getSpanLength(javaHitPoints, curSpan);
                int bestSpanLength = getSpanLength(javaHitPoints, bestSpan);
                if (curSpanLength < bestSpanLength)
                    bestSpan = curSpan;
            }

        }
        return bestSpan;
    }

    private static int getSpanLength(JavaHitPoint[] javaHitPoints, ArrayList<Integer> span) throws UnsupportedEncodingException {
        JavaHitPoint first = javaHitPoints[span.get(0)];
        JavaHitPoint last = javaHitPoints[span.get(span.size() - 1)];
        return last.iStart - first.iStart + last.iLength;
    }

    private static String buildSpanString(String content, int prevEnd, JavaHitPoint[] javaHitPoints, ArrayList<Integer> span, int[] sentenceBound) {
        StringBuilder builder = new StringBuilder();
        JavaHitPoint firstHitPoint = javaHitPoints[span.get(0)];
        int sentenceStart = findSenenceStart(content, prevEnd, firstHitPoint.iStart);
        if (sentenceStart < firstHitPoint.iStart)
            builder.append(content.substring(sentenceStart, firstHitPoint.iStart));
        int prePointEnd = firstHitPoint.iStart;
        for (int i = 0; i < span.size(); i++) {
            JavaHitPoint hitPoint = javaHitPoints[span.get(i)];
            if (prePointEnd < hitPoint.iStart)
                builder.append(content, prePointEnd, hitPoint.iStart);
            builder.append("<em class=\"hit\">").append(content.substring(hitPoint.iStart, hitPoint.iStart + hitPoint.iLength)).append("</em>");
            prePointEnd = hitPoint.iStart + hitPoint.iLength;
        }
        JavaHitPoint lastHitPoint = javaHitPoints[span.get(span.size() - 1)];
        int sentenceEnd = findSenenceEnd(content, lastHitPoint.iStart + lastHitPoint.iLength, content.length());
        if (sentenceEnd > lastHitPoint.iStart + lastHitPoint.iLength)
            builder.append(content.substring(lastHitPoint.iStart + lastHitPoint.iLength, sentenceEnd));
        sentenceBound[0] = sentenceStart;
        sentenceBound[1] = sentenceEnd;
        return builder.toString();
    }

    private static int findSenenceStart(String content, int start, int end) {
        // 查询end之前的句子开始的位置，不能超过start
        for (int i = end; i >= start; i--) {
            char ch = content.charAt(i);
            if (SENTENCE_ENDS.contains(ch)) return i + 1;
        }
        return start;
    }

    private static int findSenenceEnd(String content, int start, int end) {
        // 查询start之后的句子句子结束的位置，不能超过end
        for (int i = start; i < end; i++) {
            char ch = content.charAt(i);
            if (SENTENCE_ENDS.contains(ch)) return i + 1;
        }
        return end;
    }

    // accessors ------------------------------------------------------------

    public int getMaxTotalLength() {
        return maxTotalLength;
    }

    public void setMaxTotalLength(int maxTotalLength) {
        this.maxTotalLength = maxTotalLength;
    }

    public int getMaxSpanCount() {
        return maxSpanCount;
    }

    public void setMaxSpanCount(int maxSpanCount) {
        this.maxSpanCount = maxSpanCount;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
