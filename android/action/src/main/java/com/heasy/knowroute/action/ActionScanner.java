package com.heasy.knowroute.action;

import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.configuration.AbstractComponentScanner;
import com.heasy.knowroute.core.utils.ClassUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.PathClassLoader;

/**
 * 扫描、加载Action类到内存
 */
public class ActionScanner extends AbstractComponentScanner<Map<String, Action>> {
    private static final Logger logger = LoggerFactory.getLogger(ActionScanner.class);

    @Override
    public Map<String, Action> scan(){
        Map<String, Action> actionMap = new HashMap<String, Action>();

        if(StringUtil.isNotEmpty(getBasePackages())){
            String[] packagesArray = getBasePackages().split(";");
            logger.info("packagesArray = " + packagesArray);

            try {
                PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();
                List<String> classNameList = ClassUtil.getClassFiles(getContext(), packagesArray);
                for(String className : classNameList){
                    Class<?> entryClass = Class.forName(className, true, classLoader);
                    JSActionAnnotation annotation = entryClass.getAnnotation(JSActionAnnotation.class);
                    if (annotation != null) {
                        String name = annotation.name();
                        if(StringUtil.isEmpty(name)){
                            name = entryClass.getSimpleName().replace("Action", "");
                        }

                        Action action = (Action)entryClass.newInstance();
                        actionMap.put(name, action);
                        logger.info(name + "=" + className);
                    }
                }

            }catch (Exception ex){
                actionMap.clear();
                logger.error("", ex);
            }
        }

        return actionMap;
    }

}
