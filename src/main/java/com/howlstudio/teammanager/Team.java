package com.howlstudio.teammanager;
import java.util.*;
public class Team {
    private final String name, color;
    private final Set<UUID> members=new HashSet<>();
    private boolean friendlyFire=false;
    public Team(String name,String color){this.name=name;this.color=color;}
    public String getName(){return name;} public String getColor(){return color;}
    public Set<UUID> getMembers(){return members;}
    public boolean isMember(UUID u){return members.contains(u);}
    public boolean isFriendlyFire(){return friendlyFire;} public void setFriendlyFire(boolean v){friendlyFire=v;}
    public void addMember(UUID u){members.add(u);}
    public void removeMember(UUID u){members.remove(u);}
    public String getTag(){return color+"["+name+"]§r";}
    public String toConfig(){return name+"|"+color+"|"+friendlyFire+"|"+String.join(",",members.stream().map(UUID::toString).toList());}
    public static Team fromConfig(String s){String[]p=s.split("\\|",4);if(p.length<4)return null;Team t=new Team(p[0],p[1]);t.friendlyFire=Boolean.parseBoolean(p[2]);if(!p[3].isBlank())for(String u:p[3].split(","))try{t.addMember(UUID.fromString(u));}catch(Exception e){}return t;}
}
