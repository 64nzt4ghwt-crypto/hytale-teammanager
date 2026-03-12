package com.howlstudio.teammanager;
import com.hypixel.hytale.component.Ref; import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.nio.file.*; import java.util.*;
public class TeamManager {
    private final Path dataDir;
    private final Map<String,Team> teams=new LinkedHashMap<>();
    private final Map<UUID,String> memberTeam=new HashMap<>();
    public TeamManager(Path d){this.dataDir=d;try{Files.createDirectories(d);}catch(Exception e){}load();}
    public int getTeamCount(){return teams.size();}
    public Team getTeamOf(UUID u){String n=memberTeam.get(u);return n!=null?teams.get(n):null;}
    public void save(){try{StringBuilder sb=new StringBuilder();for(Team t:teams.values())sb.append(t.toConfig()).append("\n");Files.writeString(dataDir.resolve("teams.txt"),sb.toString());}catch(Exception e){}}
    private void load(){try{Path f=dataDir.resolve("teams.txt");if(!Files.exists(f))return;for(String l:Files.readAllLines(f)){Team t=Team.fromConfig(l);if(t!=null){teams.put(t.getName().toLowerCase(),t);for(UUID u:t.getMembers())memberTeam.put(u,t.getName().toLowerCase());}}}catch(Exception e){}}
    private void broadcast(Team t,String msg){for(PlayerRef p:Universe.get().getPlayers())if(t.isMember(p.getUuid()))p.sendMessage(Message.raw(msg));}
    public AbstractPlayerCommand getTeamCommand(){
        return new AbstractPlayerCommand("team","Team management. /team create|join|leave|list|info|chat|ff"){
            @Override protected void execute(CommandContext ctx,Store<EntityStore> store,Ref<EntityStore> ref,PlayerRef playerRef,World world){
                UUID uid=playerRef.getUuid();String[]args=ctx.getInputString().trim().split("\\s+",3);
                String sub=args.length>0?args[0].toLowerCase():"list";
                switch(sub){
                    case"create"->{if(args.length<3){playerRef.sendMessage(Message.raw("Usage: /team create <name> <color_code>"));break;}String key=args[1].toLowerCase();if(teams.containsKey(key)){playerRef.sendMessage(Message.raw("[Team] Name taken."));break;}if(memberTeam.containsKey(uid)){playerRef.sendMessage(Message.raw("[Team] Leave current team first."));break;}Team t=new Team(args[1],args[2]);teams.put(key,t);t.addMember(uid);memberTeam.put(uid,key);playerRef.sendMessage(Message.raw("[Team] Created: "+t.getTag()));save();}
                    case"join"->{if(args.length<2)break;String key=args[1].toLowerCase();Team t=teams.get(key);if(t==null){playerRef.sendMessage(Message.raw("[Team] Not found."));break;}if(memberTeam.containsKey(uid)){playerRef.sendMessage(Message.raw("[Team] Leave current team first."));break;}t.addMember(uid);memberTeam.put(uid,key);playerRef.sendMessage(Message.raw("[Team] Joined: "+t.getTag()));broadcast(t,"[Team] "+playerRef.getUsername()+" joined!");save();}
                    case"leave"->{String key=memberTeam.remove(uid);if(key==null){playerRef.sendMessage(Message.raw("[Team] Not in a team."));break;}Team t=teams.get(key);if(t!=null){t.removeMember(uid);broadcast(t,"[Team] "+playerRef.getUsername()+" left.");}playerRef.sendMessage(Message.raw("[Team] Left."));save();}
                    case"list"->{if(teams.isEmpty()){playerRef.sendMessage(Message.raw("[Team] No teams."));break;}playerRef.sendMessage(Message.raw("=== Teams ==="));for(Team t:teams.values())playerRef.sendMessage(Message.raw("  "+t.getTag()+" — "+t.getMembers().size()+" members"));}
                    case"info"->{Team t=getTeamOf(uid);if(t==null){playerRef.sendMessage(Message.raw("[Team] Not in a team."));break;}playerRef.sendMessage(Message.raw(t.getTag()+" — "+t.getMembers().size()+" members | FF: "+t.isFriendlyFire()));}
                    case"chat","tc"->{Team t=getTeamOf(uid);if(t==null){playerRef.sendMessage(Message.raw("[Team] Not in a team."));break;}String msg=args.length>1?args[1]:"";if(msg.isBlank())break;broadcast(t,t.getTag()+" "+playerRef.getUsername()+": "+msg);}
                    case"ff"->{Team t=getTeamOf(uid);if(t==null){playerRef.sendMessage(Message.raw("[Team] Not in a team."));break;}t.setFriendlyFire(!t.isFriendlyFire());save();playerRef.sendMessage(Message.raw("[Team] Friendly fire: "+t.isFriendlyFire()));}
                    default->playerRef.sendMessage(Message.raw("Usage: /team create|join|leave|list|info|chat|ff"));
                }
            }
        };
    }
}
