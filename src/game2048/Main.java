package game2048;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SplashScreen splash = new SplashScreen();
                splash.showSplash();
            }
        });
    }
}