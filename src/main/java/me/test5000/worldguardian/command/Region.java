package me.test5000.worldguardian.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.ChunkVector2;
import cn.nukkit.math.Vector2f;
import cn.nukkit.utils.ConfigSection;
import me.test5000.worldguardian.WorldGuardian;
import me.test5000.worldguardian.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Region extends PluginCommand<WorldGuardian> {

    private static HashMap<UUID, TempPlayerSel> selHashMap = new HashMap<>();

    public Region(String name, WorldGuardian owner) {
        super("rg", "Protect your region.", WorldGuardian.getInstance());

        this.commandParameters.clear();

        this.commandParameters.put("position1", new CommandParameter[]{
                CommandParameter.newEnum("pos1", new CommandEnum("method", "pos1"))
        });
        this.commandParameters.put("position2", new CommandParameter[]{
                CommandParameter.newEnum("pos2", new CommandEnum("method", "pos2"))
        });

        // TODO : REMOVE
        this.commandParameters.put("debug", new CommandParameter[]{
                CommandParameter.newEnum("debug", new CommandEnum("method", "debug"))
        });

        this.commandParameters.put("claim", new CommandParameter[]{
                CommandParameter.newEnum("claim", new CommandEnum("method", "claim")),
                CommandParameter.newType("name", false, CommandParamType.STRING)
        });

        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        String exec = result.getKey();

        sender.sendMessage(exec);

        if (sender.asPlayer() == null) return 0;

        switch (exec) {

            case "position1" -> {
                if (selHashMap.get(sender.asPlayer().getUniqueId()) != null){
                    selHashMap.get(sender.asPlayer().getUniqueId()).setPos1(sender.asPlayer().getPosition().asVector3f());
                    selHashMap.get(sender.asPlayer().getUniqueId()).setLevel(sender.asPlayer().getLevel());
                    return 0;
                }
                sender.sendMessage("Position 1 was set!");

                selHashMap.put(sender.asPlayer().getUniqueId(), new TempPlayerSel());
                selHashMap.get(sender.asPlayer().getUniqueId()).setPos1(sender.asPlayer().asVector3f());
                selHashMap.get(sender.asPlayer().getUniqueId()).setLevel(sender.asPlayer().getLevel());
                sender.sendMessage("Position 1 was set!");

            }
            case "position2" -> {
                if (selHashMap.get(sender.asPlayer().getUniqueId()) != null){
                    selHashMap.get(sender.asPlayer().getUniqueId()).setPos2(sender.asPlayer().getPosition().asVector3f());
                    selHashMap.get(sender.asPlayer().getUniqueId()).setLevel(sender.asPlayer().getLevel());
                    sender.sendMessage("Position 2 was set!");
                    return 0;
                }
                selHashMap.put(sender.asPlayer().getUniqueId(), new TempPlayerSel());
                selHashMap.get(sender.asPlayer().getUniqueId()).setPos2(sender.asPlayer().asVector3f());
                selHashMap.get(sender.asPlayer().getUniqueId()).setLevel(sender.asPlayer().getLevel());
                sender.sendMessage("Position 2 was set!");

            }
            case "claim" -> {
                // TODO : Make the claim of land.

                String name = result.getValue().getResult(1);
                if (WorldGuardian.getRegions()
                        .getSection("regions")
                        .exists(name)){
                    sender.sendMessage("Region with that name exist.");
                    return 1;
                }

                if(name.contains(" ")) {
                    sender.sendMessage("Region cannot have spaces!");
                    return 1;
                }

                if (name.length() > 32){
                    sender.sendMessage("Region cannot have name more than 32");
                    return 1;
                }

                TempPlayerSel sel = selHashMap.get(sender.asPlayer().getUniqueId());

                if (sel.getPos1() == null || sel.getPos2() == null) {
                    sender.sendMessage("Land not selected.");
                    return 1;
                }


                ConfigSection sectionReg = WorldGuardian.getRegions().getSection("regions")
                        .getSection(name);

                sectionReg.set("pos1", new ArrayList<>() {{
                    add(sel.getPos1().x);
                    add(sel.getPos1().y);
                    add(sel.getPos1().z);
                }});
                sectionReg.set("pos2", new ArrayList<>() {{
                    add(sel.getPos2().x);
                    add(sel.getPos2().y);
                    add(sel.getPos2().z);
                }});
                sectionReg.set("priority", 0);

                // Calc

                ArrayList<String> chunkAlloc = new ArrayList<>();

                int maxX = Math.max(new Location( sel.getPos2().x, sel.getPos2().y, sel.getPos2().z ).getChunkX(),
                        new Location( sel.getPos1().x, sel.getPos1().y, sel.getPos1().z ).getChunkX());

                int minX = Math.min(new Location( sel.getPos2().x, sel.getPos2().y, sel.getPos2().z ).getChunkX(),
                        new Location( sel.getPos1().x, sel.getPos1().y, sel.getPos1().z ).getChunkX());

                int maxY = Math.max(new Location( sel.getPos2().x, sel.getPos2().y, sel.getPos2().z ).getChunkZ(),
                        new Location( sel.getPos1().x, sel.getPos1().y, sel.getPos1().z ).getChunkZ());

                int minY = Math.min(new Location( sel.getPos2().x, sel.getPos2().y, sel.getPos2().z ).getChunkZ(),
                        new Location( sel.getPos1().x, sel.getPos1().y, sel.getPos1().z ).getChunkZ());


                for (int i = minX; i <= maxX; i++) {
                    for(int j = minY; j<=maxY; j++) {
                        chunkAlloc.add(Utils.chunkToRaw(
                                new Vector2f(i,j)
                        ));
                    }
                }

                for (String chunkRaw: chunkAlloc) {
                    ConfigSection section = WorldGuardian.getChunkTableConfig()
                            .getSection( sender.asPlayer().getLevel().getName() )
                            .getSection(chunkRaw);
                    section.set(name, name);
                }
                sectionReg.set("chunkAlloc", chunkAlloc);

                WorldGuardian.getRegions().save(true);
                WorldGuardian.getChunkTableConfig().save(true);
                WorldGuardian.getRegions().reload();
                WorldGuardian.getChunkTableConfig().reload();
                sender.sendMessage("Region calimed");
                return 0;
            }
            case "debug" -> {

                sender.sendMessage(String.valueOf(WorldGuardian.getChunkTableConfig()
                        .exists(Utils.chunkToRaw(sender.asPlayer().getChunk()))));

            }
            default -> {
                return 1;
            }


        }

        return 1;
    }
}
