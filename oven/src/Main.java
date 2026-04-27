// File: src/Main.java
import view.MainGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Mengatur Look and Feel agar aplikasi terlihat modern sesuai sistem operasi
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Menjalankan GUI di Event Dispatch Thread (EBT)
        SwingUtilities.invokeLater(() -> new MainGUI().showLoginWindow());
    }
}