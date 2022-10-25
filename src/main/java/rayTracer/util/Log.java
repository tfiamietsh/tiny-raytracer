package rayTracer.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

public class Log {
    public static void println(String string) {
        if (!isLogCreated)
            try { isLogCreated = new File(filename).createNewFile(); }
            catch (Exception e) { e.printStackTrace(); }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true))))
        { out.println(new Date(System.currentTimeMillis()).toString() + '\t' + string); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private static final String filename = "log.txt";
    private static boolean isLogCreated = false;
}
