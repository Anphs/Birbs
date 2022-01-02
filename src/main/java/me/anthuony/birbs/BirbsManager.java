package me.anthuony.birbs;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BirbsManager extends AbstractBirbsManager {

    public static void main(String[] args) {
        BirbsContainer bc = new BirbsContainer(new BirbsManager());
        bc.start();
    }

    @Override
    public long update(BirbsContainer bc) {
		long t1 = System.currentTimeMillis();

        InputBinds.doInputBinds(bc, this);

        doBirbLogic(bc);

        if (bc.getPursuitEntity() != null) {
            Point2D.Float pursuitBirbPoint = bc.getPursuitEntity().getWorldPoint();
            bc.setCameraOffsetX(bc.getWindowWidth() / bc.getScale() / 2 - pursuitBirbPoint.getX());
            bc.setCameraOffsetY(bc.getWindowHeight() / bc.getScale() / 2 - pursuitBirbPoint.getY());
        }

		long t2 = System.currentTimeMillis();

		return t2 - t1;
    }

    @Override
    public long render(BirbsContainer bc, Renderer r) {
		long t1 = System.currentTimeMillis();
        Graphics2D g2d = (Graphics2D) bc.getWindow().getG();
        AffineTransform originalPosition = g2d.getTransform();
        Font bigWords = new Font("Calibri", Font.PLAIN, (int) ((bc.getWorldHeight() * bc.getScale()) / 20));
        Font interfaceFont = new Font("Calibri", Font.PLAIN, 24);

		//Draw Background
        if (!bc.getInput().isKey(KeyEvent.VK_E)) { r.drawBackground(g2d); }

		//Draw Entities
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setStroke(new BasicStroke((float) (5 * bc.getScale())));
        int onScreenCount = drawOnEntities(g2d, r, bc, originalPosition);

		//Return To Original Position
        g2d.setTransform(originalPosition);

        //UI Text
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (bc.getInput().getMouseIdleTime() > 3) {
            int red = bc.getTextUIColor().getRed();
            int green = bc.getTextUIColor().getGreen();
            int blue = bc.getTextUIColor().getBlue();
            g2d.setColor(new Color(red, green, blue, ExtraMath.boundNumber((int) (255 - (bc.getInput().getMouseIdleTime() - 3) * 1000), 0, 255)));
            bc.getWindow().getFrame().getContentPane().setCursor(bc.getWindow().getBlankCursor());
        } else {
            g2d.setColor(bc.getTextUIColor());
            bc.getWindow().getFrame().getContentPane().setCursor(Cursor.getDefaultCursor());
        }

        g2d.setFont(interfaceFont);
        FontMetrics interfaceFontMetrics = g2d.getFontMetrics();

        List<String> topLeftText = new LinkedList<>(Arrays.asList(
                "" + bc.getEntityList().size() + " Birbs in the World",
                "" + onScreenCount + " Birbs on Screen"
        ));

        int xMouse = (int) (bc.getInput().getMousePoint().getX() * bc.getScale());
        int yMouse = (int) (bc.getInput().getMousePoint().getY() * bc.getScale());
        List<String> topRightText = new LinkedList<>(Arrays.asList(
                "Kernel Processing Time: " + bc.getKernelTime() + "ms",
                "Render Time: " + bc.getRenderTime() + "ms",
                "x: " + xMouse + " y: " + yMouse,
                "FPS: " + bc.getFps()
        ));

        List<String> entityStringList = new LinkedList<>();
        for (Entity e : bc.getEntityList()) {
            String str = String.valueOf(e.getEntityID());
            entityStringList.add(str);
        }

        List<String> pursuitBirbHistoryListNames = new LinkedList<>();
        for (Entity e : bc.getPursuitList()) {
            if (e instanceof Birb) {
                String name = ((Birb) e).getName();
                if (e == bc.getPursuitEntity()) {
                    name += "   <<<";
                }
                pursuitBirbHistoryListNames.add(name);
            }
        }

        if (bc.isDrawUI()) {
            //Say click anywhere
            if (bc.getEntityList().size() == 0) {
                r.drawCenteredString(g2d, bigWords, "Click Anywhere to Begin", bc.getWorldWidth() / 2.0, bc.getWorldHeight() / 2.0);
            }

            r.drawLeftAlignedList(g2d, interfaceFont, topLeftText, 10, 0);
            r.drawRightAlignedList(g2d, interfaceFont, topRightText, bc.getWindowWidth() - 10, 0);
            r.drawRightAlignedList(g2d, interfaceFont, bc.getKeybindsHint(), bc.getWindowWidth() - 10, bc.getWindowHeight() - (bc.getKeybindsHint().size() * interfaceFontMetrics.getAscent()) - 10);
            r.drawLeftAlignedList(g2d, interfaceFont, pursuitBirbHistoryListNames, 10, 100);

//			r.drawLeftAlignedList(g2d, interfaceFont, entityStringList, 400, 0);
//			r.drawLeftAlignedList(g2d, interfaceFont, chunkInfo, 500, 0);
        }

		long t2 = System.currentTimeMillis();
		return t2 - t1;
    }

	private int drawOnEntities(Graphics2D g2d, Renderer r, BirbsContainer bc, AffineTransform originalPosition)
	{
		int onScreenCount = 0;
		for (Entity entity : bc.getEntityList()) {
			if (entity.isOnScreen()) {
				g2d.setTransform(originalPosition);
				g2d.translate(entity.getXScreen(), entity.getYScreen());
				r.drawBirb(g2d, (Birb) entity);
				onScreenCount++;
			}
		}
		return onScreenCount;
	}

    private void doBirbLogic(BirbsContainer bc) {
        ThreadGroup tg = new ThreadGroup("Update Locations");

        BirbLogic logic = new BirbLogic(bc.getBirbsList(), "BirbLogicSingle", tg, bc);
        logic.start();
    }

    private void addBirb(BirbsContainer bc, float worldX, float worldY) {
        Birb birb = new Birb(bc, bc.getEntityCount(), 1, bc.getRandomName(), worldX, worldY, 1);
        bc.getEntityList().add(birb);
        bc.getBirbsList().add(birb);
    }

    public void addBirb(BirbsContainer bc, Point2D.Float p, int multiplier) {
        for (int i = 0; i < multiplier; i++) {
            float x = (float) (p.getX() + 10 * multiplier * (Math.random() - 0.5)) % bc.getWorldWidth();
            float y = (float) (p.getY() + 10 * multiplier * (Math.random() - 0.5)) % bc.getWorldHeight();

            addBirb(bc, x, y);
        }
    }

    public void updateScale(BirbsContainer bc) {
        bc.setScale((bc.getScale() - bc.getInput().getScroll() / 10.0));
    }

    public void reset(BirbsContainer bc) {
        bc.removeAllEntities();
        bc.setCameraOffsetX((bc.getWorldWidth() - bc.getWindowWidth() * 10) / -2.0);
        bc.setCameraOffsetY((bc.getWorldHeight() - bc.getWindowHeight() * 10) / -2.0);
        bc.setScale(0.1);
        bc.setPursuitEntity(null);
        bc.getPursuitList().clear();
        updateScale(bc);
    }
}
