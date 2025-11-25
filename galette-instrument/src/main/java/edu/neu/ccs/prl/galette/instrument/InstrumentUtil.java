package edu.neu.ccs.prl.galette.instrument;

import java.io.File;

 /**
   * Modified Version (Galette-Vitruv):
   * Added Windows platform detection to append .exe extension
   *
   * @param javaHome The Java installation directory
   * @return File pointing to java or java.exe based on operating system
   *
   * @since galette-vitruv
   * @modification Added OS detection: checks System.getProperty("os.name")
   * @modification Windows systems get "java.exe", others get "java"
   * @modification Windows systems get "jlink.exe", others get "jlink"
   */

public final class InstrumentUtil {
    private InstrumentUtil() {
        throw new AssertionError();
    }

    public static File javaHomeToBin(File javaHome) {
        return new File(javaHome, "bin");
    }

    public static File javaHomeToJavaExec(File javaHome) {
        String os = System.getProperty("os.name").toLowerCase();
        String executable = os.contains("win") ? "java.exe" : "java";
        return new File(javaHomeToBin(javaHome), executable);
    }

    public static File javaHomeToJLinkExec(File javaHome) {
        String os = System.getProperty("os.name").toLowerCase();
        String executable = os.contains("win") ? "jlink.exe" : "jlink";
        return new File(javaHomeToBin(javaHome), executable);
    }

    public static boolean isJavaHome(File directory) {
        return javaHomeToJavaExec(directory).isFile();
    }

    public static boolean isModularJvm(File javaHome) {
        return isJavaHome(javaHome) && javaHomeToJLinkExec(javaHome).isFile();
    }

    public static File getClassPathElement(Class<?> clazz) {
        return new File(
                clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}
