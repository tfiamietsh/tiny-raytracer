package rayTracer.tracer.script;

import rayTracer.tracer.misc.Material;
import rayTracer.tracer.RTObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Keyword {
    public Keyword(RTObject object) {
        this.object = object;
    }

    public String getError(String line) {
        String[] values = Arrays.stream(line.split(",")).map(String::trim)
                .map(i -> i.split(" +")).flatMap(Arrays::stream).toArray(String[]::new);
        if (object.getPattern(values.length - 1) == null)
            return object.getClass().getName() + " incorrect argument count";
        String[] keys = Arrays.stream(object.getPattern(values.length - 1).split(",")).map(String::trim)
                .map(i -> i.split(" +")).flatMap(Arrays::stream).toArray(String[]::new);

        String lastValue = values[values.length - 1];   //  check for nonexistent material
        if (lastValue.matches("[_a-zA-Z]+[_a-zA-Z0-9]+") && Material.getByName(lastValue) == null)
            return "material '" + lastValue + "' is not declared";
        if (keys.length == values.length) {
            for (int i = 1; i < keys.length; i++)
                if (keys[i].equals("%f"))
                    try { Double.parseDouble(values[i]); }
                    catch (Exception e) { return object.getClass().getName() + " incorrect syntax"; }
                else if (!keys[i].equals("%s"))
                    return object.getClass().getName() + " incorrect command pattern";
        } else
            return object.getClass().getName() + " incorrect argument count";

        if (keys[0].equals("mt"))
            getObject(line);    //  add material in advance

        return "";
    }

    public RTObject getObject(String line) {
        List<Double> dblParams = new ArrayList<>();
        List<String> strParams = new ArrayList<>();
        String[] values = Arrays.stream(line.split(",")).map(String::trim)
                .map(i -> i.split(" +")).flatMap(Arrays::stream).toArray(String[]::new);
        String[] keys = Arrays.stream(object.getPattern(values.length - 1).split(",")).map(String::trim)
                .map(i -> i.split(" +")).flatMap(Arrays::stream).toArray(String[]::new);

        for (int i = 1; i < keys.length; i++)
            if (keys[i].equals("%f"))
                dblParams.add(Double.parseDouble(values[i]));
            else if (keys[i].equals("%s"))
                strParams.add(values[i]);
        return object.newInstance(dblParams, strParams);
    }

    private final RTObject object;
}
