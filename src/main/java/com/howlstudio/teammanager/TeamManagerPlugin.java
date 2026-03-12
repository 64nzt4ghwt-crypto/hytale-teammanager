package com.howlstudio.teammanager;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/** TeamManager — Create and manage PvP teams with colors, friendly fire toggle, and team chat. */
public final class TeamManagerPlugin extends JavaPlugin {
    private TeamManager mgr;
    public TeamManagerPlugin(JavaPluginInit init){super(init);}
    @Override protected void setup(){
        System.out.println("[Teams] Loading...");
        mgr=new TeamManager(getDataDirectory());
        CommandManager cmd=CommandManager.get();
        cmd.register(mgr.getTeamCommand());
        System.out.println("[Teams] Ready. "+mgr.getTeamCount()+" teams.");
    }
    @Override protected void shutdown(){if(mgr!=null)mgr.save();System.out.println("[Teams] Stopped.");}
}
