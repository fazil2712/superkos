package com.superkos.app.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class ErrorLoggerAdvice {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        ModelAndView mav = new ModelAndView("error_debug");
        mav.addObject("message", ex.getMessage());
        mav.addObject("trace", sw.toString());
        return mav;
    }
}
