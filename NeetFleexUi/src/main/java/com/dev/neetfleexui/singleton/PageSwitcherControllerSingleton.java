package com.dev.neetfleexui.singleton;

import com.dev.neetfleexui.controllers.PageSwitcherController;
import com.dev.neetfleexui.entities.UserSession;

public class PageSwitcherControllerSingleton {
    private static PageSwitcherController instance = null;
    private PageSwitcherControllerSingleton() {}


    public static PageSwitcherController getInstance() {

        if(instance == null) {
            synchronized (PageSwitcherControllerSingleton.class) {
                if (instance == null) {
                    instance = new PageSwitcherController();
                }
            }
        }

        return instance;
    }
}
