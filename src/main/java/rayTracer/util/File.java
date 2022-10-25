package rayTracer.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.stream.Collectors;

public class File {
    public static String read(String filename) {
        String text = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            text = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) { Log.println(e.getMessage()); }
        return text;
    }

    public static String read(java.io.File file) {
        String text = "";

        if (file != null)
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                text = reader.lines().collect(Collectors.joining("\n"));
            } catch (Exception e) { Log.println(e.getMessage()); }
        return text;
    }

    public static void write(String filename, String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename)))
        { writer.write(text); }
        catch (Exception e) { Log.println(e.getMessage()); }
    }

    public static void write(java.io.File file, String text) {
        if (file != null)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
            { writer.write(text); }
            catch (Exception e) { Log.println(e.getMessage()); }
    }
}
