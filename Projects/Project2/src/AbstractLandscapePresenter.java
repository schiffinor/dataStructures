import javax.swing.*;

abstract class AbstractLandscapePresenter {
    final JFrame win;
    public AbstractLandscapePresenter(){
        this.win = new JFrame("hehe");
    }
    public void repaint() {
        this.win.repaint();
    }

}
