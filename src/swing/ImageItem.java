package swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.miginfocom.swing.MigLayout;

public class ImageItem extends JComponent {

    private Icon image;
    private final int shadowSize = 50;
    private Timer timer;
    private boolean show;

    public ImageItem(Icon image, MigLayout mig) {
        //  Test Image
        this.image = image;
        setBackground(Color.BLACK);
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (show) {
                    int width = getWidth();
                    int height = getHeight();
                    if (height < 220) {
                        mig.setComponentConstraints(ImageItem.this, "w " + (width + 1) + ", h " + (height + 1));
                        getParent().revalidate();
                    } else {
                        timer.stop();
                    }
                } else {
                    int width = getWidth();
                    int height = getHeight();
                    if (height > 200) {
                        mig.setComponentConstraints(ImageItem.this, "w " + (width - 1) + ", h " + (height - 1));
                        getParent().revalidate();
                    } else {
                        timer.stop();
                    }
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                show = true;
                timer.start();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                show = false;
                timer.start();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        if (image != null) {
            Graphics2D g2 = (Graphics2D) grphcs;
            Rectangle size = getAutoSize(image);
            int width = getWidth();
            int height = getHeight();
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, width, height - shadowSize, 25, 25);
            g.setComposite(AlphaComposite.SrcIn);
            g.drawImage(toImage(image), size.x, size.y, size.width, size.height, null);
            g.dispose();
            g2.drawImage(img, 0, 0, null);
            g2.drawImage(createShadowImage(img), 0, height - shadowSize + 5, null);
            g2.dispose();
        }
        super.paintComponent(grphcs);
    }

    private BufferedImage createShadowImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        int x = 0;
        int y = -shadowSize;
        //  Flip image
        g.drawImage(image, x, y + height, width, -height, null);
        GradientPaint gra = new GradientPaint(0, 0, Color.yellow, 0, shadowSize - 10, new Color(0, 0, 0, 0));
        g.setPaint(gra);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN, 0.5f));
        g.fillRect(0, 0, width, height);
        g.dispose();
        return img;
    }

    private Rectangle getAutoSize(Icon image) {
        int w = getWidth();
        int h = getHeight() - shadowSize;
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();
        double xScale = (double) w / iw;
        double yScale = (double) h / ih;
        double scale = Math.max(xScale, yScale);
        int width = (int) (scale * iw);
        int height = (int) (scale * ih);
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        return new Rectangle(new Point(x, y), new Dimension(width, height));
    }

    private Image toImage(Icon icon) {
        return ((ImageIcon) icon).getImage();
    }
}
