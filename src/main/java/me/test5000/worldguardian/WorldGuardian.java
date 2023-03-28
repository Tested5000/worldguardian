package me.test5000.worldguardian;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import me.test5000.worldguardian.command.Region;
import me.test5000.worldguardian.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class WorldGuardian extends PluginBase {

    private  static Config regions;
    private static Config chunkTableConfig;

    public static Config getRegions() {
        return regions;
    }

    public static Config getChunkTableConfig() {
        return chunkTableConfig;
    }

    public static WorldGuardian getInstance() {
        return instance;
    }

    public static WorldGuardian instance;

    public void log(String... message) {

        for (String msg: message
             ) {
            getLogger().info(TextFormat.colorize('&', msg));
        }

    }

    @Override
    public void onEnable() {
        instance = this;

        log("&aPlugin enabled.");

        regions = new Config(
                new File(getDataFolder(), "regions.json"),
                Config.YAML, // TODO: Change to json when json parser will be fixed.
                new LinkedHashMap<>() {{
                    put("regions", new LinkedHashMap<>() {{
                        put("helloworld", new LinkedHashMap<>() {{
                            put("pos1", List.of(0,0,0));
                            put("pos2", List.of(0,0,0));
                            put("chunkAlloc", List.of("0:0", "0:0"));
                            put("priority", 0);
                        }});
                    }});
                }}
        );
        chunkTableConfig = regions = new Config(
                new File(getDataFolder(), "regionAlloc.json"),
                Config.YAML, // TODO: Change to json when json parser will be fixed.
                new LinkedHashMap<>(){{
                    put("world", new LinkedHashMap<>() {{
                        put("0:0", new LinkedHashMap<>() {{
                            put("helloworld", "helloworld");
                        }});
                    }});
                }}
        );

        getServer().getCommandMap().register("wguar", new Region("region" ,this));


    }
}
