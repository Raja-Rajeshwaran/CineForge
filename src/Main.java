public class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new CineForgeApp();
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Failed to start CineForge: " + e.getMessage(), 
                    "Startup Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}