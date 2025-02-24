package org.captnced.main;

import org.captnced.configs.Config;
import org.captnced.discord.JdaMain;
import org.captnced.untis.Untis;

public class Main {

    public static void main(String[] args) {
        Config conf = new Config("config");
        Untis untis = new Untis();
        JdaMain ignored = new JdaMain(conf, untis);
    }
}
