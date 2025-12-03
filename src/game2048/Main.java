package game2048;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Show splash screen for 5000 milliseconds (3 seconds)
                SplashScreen splash = new SplashScreen(3000);
                splash.showSplash();
            }
        });
    }
}