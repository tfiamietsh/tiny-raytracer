package rayTracer.tracer.script;

import rayTracer.core.RayTracer;
import rayTracer.core.Scene;
import rayTracer.tracer.geometry.*;
import rayTracer.tracer.misc.Camera;
import rayTracer.tracer.light.Light;
import rayTracer.tracer.light.AmbientLight;
import rayTracer.tracer.light.PointLight;
import rayTracer.tracer.RTObject;
import rayTracer.tracer.misc.Material;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Script {
    public Script() {
        keywords = new HashMap<>();

        keywords.put("c", new Keyword(new Camera()));
        keywords.put("a", new Keyword(new AmbientLight()));
        keywords.put("p", new Keyword(new PointLight()));
        keywords.put("sp", new Keyword(new Sphere()));
        keywords.put("pl", new Keyword(new Plane()));
        keywords.put("tr", new Keyword(new Triangle()));
        keywords.put("sq", new Keyword(new Square()));
        keywords.put("cy", new Keyword(new Cylinder()));
        keywords.put("mt", new Keyword(new Material()));
        keywords.put("cu", new Keyword(new Cube()));
        keywords.put("ss", new Keyword(new Skysphere()));
    }

    public String tryToExecuteScript(String outputFullPath, String script, Dimension resolution,
                 int cores, int maxDepth, int shadowSamplesNum) {
        String errors = checkForErrors(script);
        if (errors.equals(""))
            executeScript(outputFullPath, script, resolution, cores, maxDepth,
                    shadowSamplesNum);
        return errors;
    }

    private void executeScript(String outputFullPath, String script, Dimension resolution,
               int cores, int maxDepth, int shadowSamplesNum) {
        Scene scene = new Scene();

        String[] lines = script.split("\r\n|\r|\n");
        for (String line : lines) {
            if (line.length() == 0 || line.startsWith("//"))
                continue;

            String key = line.split(" +")[0];
            RTObject object = keywords.get(key).getObject(line);

            if (object instanceof Light)
                scene.addLight((Light) object);
            else if (object instanceof Shape)
                scene.addShape((Shape) object);
            else if (object instanceof Camera)
                scene.setCamera((Camera) object);
        }

        RayTracer tracer = new RayTracer();
        tracer.configure(resolution, maxDepth, shadowSamplesNum);
        tracer.render(outputFullPath, scene, cores);
    }

    public String checkForErrors(String script) {
        String errors = "";

        Pattern pattern = Pattern.compile("(//.*\n)|(/\u0085.*\u0085/)");
        Matcher matcher = pattern.matcher(script);
        script = matcher.replaceAll("");

        String[] lines = script.split("\r\n|\r|\n");
        int camerasCounter = 0, lightsCounter = 0;

        if (script.equals(""))
            return errorAtLine(0, "empty script");

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() == 0 || lines[i].startsWith("//"))
                continue;

            String key = lines[i].split(" +")[0];
            if (keywords.containsKey(key)) {
                String error = keywords.get(key).getError(lines[i]);
                if (key.equals("c"))
                    camerasCounter++;
                else if (key.equals("a") || key.equals("p"))
                    lightsCounter++;

                if (!error.equals(""))
                    errors += errorAtLine(i, error);
            } else
                errors += errorAtLine(i, "incorrect keyword '" + key + "'");
        }

        if (camerasCounter == 0)
            errors += errorAtLine(-1, "no camera set");
        else if (camerasCounter > 1)
            errors += errorAtLine(-1, "more than one camera set");
        if (lightsCounter == 0)
            errors += errorAtLine(-1, "no light source set");

        if (errors.length() > 0)
            errors = errors.substring(0, errors.length() - 1);
        return errors;
    }

    private String errorAtLine(int line, String msg) {
        return "Line " + (line + 1) + ": " + msg + "\n";
    }

    private final Map<String, Keyword> keywords;
}
