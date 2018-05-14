package com.duowan.mobile.main.kinds;

import com.duowan.mobile.main.kinds.wrapper.KindWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ericwu on 2017/10/6.
 */

public class KindSyringeService {
    private static final String PACKAGE = "com.duowan.mobile.setting.";

    private LruCache<String, KindMapSyringe> classCache;
    private List<String> blackList;

    public void init() {
        classCache = new LruCache<>(6);
        blackList = new ArrayList<>();
    }

    public boolean inject(String moduleName, Map<String, Class<? extends Kind>> feature, Map<Class<? extends Kind>, Class<? extends KindWrapper>> wrappers) {
        String className = PACKAGE + KindMapSyringe.class.getSimpleName() + "$$" + moduleName;
        try {
            if (!blackList.contains(className)) {
                KindMapSyringe autowiredHelper = classCache.get(className);
                if (null == autowiredHelper) {  // No cache.
                    autowiredHelper = (KindMapSyringe) Class.forName(className).getConstructor().newInstance();
                }
                autowiredHelper.injectFeatureInto(feature, wrappers);
                classCache.put(className, autowiredHelper);
                return true;
            }
            return false;
        } catch (Exception ex) {
            blackList.add(className);    // This instance need not autowired.
            return false;
        }
    }
}
