package tokoberkahjaya;

public class TokoBerkahJaya {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new view.FormLogin().setVisible(true);
        });
    }
}