/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.utils;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import java.io.StringWriter;

/**
 *
 * @author abhishek
 */
public class FreeMarker {
    
    public static String getContent(String templateName, Map<String, Object> input) throws Exception {
        Configuration cfg = new Configuration(new Version(2, 3, 26));
        cfg.setClassForTemplateLoading(FreeMarker.class, "/request");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Template template = cfg.getTemplate(templateName);
        Writer out = new StringWriter();
        template.process(input, out);

        return out.toString();
    }
}
