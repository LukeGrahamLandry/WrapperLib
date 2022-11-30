package ca.lukegrahamlandry.lib.base;

public class ModuleAvailable {
    public static boolean packets(){
        return canFindClass("ca.lukegrahamlandry.lib.packets.PacketWrapper");
    }

    public static boolean data(){
        return canFindClass("ca.lukegrahamlandry.lib.data.DataWrapper");
    }

    public static boolean config(){
        return canFindClass("ca.lukegrahamlandry.lib.config.ConfigWrapper");
    }

    public static boolean commentedJson(){
        return canFindClass("ca.lukegrahamlandry.lib.config.GenerateComments");
    }

    private static boolean canFindClass(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
