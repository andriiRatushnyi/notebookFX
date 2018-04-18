package baobab.notebookfx.utils;

public class UtilMemory {

    public static void report() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        double mb = 1024 * 1024;

        System.out.println("##########################################################");
        System.out.printf("Used Memory after GC: %.2f MB", used / mb);
        System.out.println("##########################################################");
    }
}
