package ca.lukegrahamlandry.examplemod.obj;

public class ListItem {
    int a = 12345;
    String name = "no name given";

    @Override
    public String toString() {
        return "ListItem{" +
                "a=" + a +
                ", name='" + name + '\'' +
                '}';
    }
}
