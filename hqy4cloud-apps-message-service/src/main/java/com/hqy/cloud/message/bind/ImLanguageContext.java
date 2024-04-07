package com.hqy.cloud.message.bind;

import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.config.ConfigurationContext;

import java.util.Properties;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/3/12
 */
public class ImLanguageContext {

    public static String getValue(String key) {
        return getValue(key, Language.ZH_CH);
    }

    public static String getValue(String key, Language language) {
        AssertUtil.notEmpty(key, "request key should not be empty.");
        AssertUtil.notNull(language, "Language should not be null.");
        String name = language.name;
        Properties properties = ConfigurationContext.getProperties(name);
        return properties.getProperty(key);
    }

    public static String getYouValue(Language language) {
        return getValue(PropertiesConstants.YOUR_KEY, language);
    }

    public static String getTargetValue(Language language) {
        return getValue(PropertiesConstants.TARGET_KEY, language);
    }



    public enum Language {

        /**
         * 中文
         */
        ZH_CH("properties/zh_cn.properties"),

        ;

        public final String name;

        Language(String name) {
            this.name = name;
        }
    }








}
