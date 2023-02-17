package com.trs.gb.tools;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 苏亚青 on 2019/1/15.
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 所有异常报错
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value=Exception.class)
    public String allExceptionHandler(HttpServletRequest request,
                                      Exception exception) throws Exception
    {
        exception.printStackTrace();
        System.out.println("我报错了："+exception.getLocalizedMessage());
        System.out.println("我报错了："+exception.getCause());
        System.out.println("我报错了："+exception.getSuppressed());
        System.out.println("我报错了："+exception.getMessage());
        System.out.println("我报错了："+exception.getStackTrace());
        return (""+exception.getCause()).split(" ")[1];
    }

}
