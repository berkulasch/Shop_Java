package ui;

import shop.common.Interface.ShopInterface;
import javax.swing.*;


public abstract class ShopFrame extends JFrame {
    protected ShopInterface shop;
    public ShopFrame(ShopInterface shop) {
        super();
        this.shop = shop;
    }
    protected void goBackToMain()  {
        this.dispose();
        GUI mainFrame = new GUI("Shop", shop);
        mainFrame.setVisible(true);
    }
}
