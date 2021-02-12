package pro.taskana.example.decision;

import java.util.List;

public class Decision {
    private final List<Rule> path;
    private final int clazz;


    public Decision(List<Rule> path, int clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    public List<Rule> getPath() {
        return path;
    }

    public int getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "Decision{" +
                "path=" + path +
                ", clazz=" + clazz +
                '}';
    }
}
