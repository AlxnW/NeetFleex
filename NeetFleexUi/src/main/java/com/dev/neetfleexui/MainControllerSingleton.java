package com.dev.neetfleexui;


import com.dev.neetfleexui.controllers.MainController;

public class MainControllerSingleton {


    private static volatile MainController instance;

    private MainControllerSingleton() {
        // private constructor to prevent instantiation
    }

    public static MainController getInstance() {
        if (instance == null) {
            synchronized (MainControllerSingleton.class) {
                if (instance == null) {
                    instance = new MainController();
                }
            }
        }
        return instance;
    }




}
