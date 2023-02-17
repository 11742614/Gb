package com.trs.gb.tools;

/**
 * Created by 苏亚青 on 2019/1/13.
 */
public class RequestLimitException extends Exception {
    private static final long serialVersionUID = 1364225358754654702L;

    public String RequestLimitException(String ex) {
        //super("HTTP请求超出设定的限制");
        return ex;
    }

    public RequestLimitException(String message) {
        super(message);
    }
}
