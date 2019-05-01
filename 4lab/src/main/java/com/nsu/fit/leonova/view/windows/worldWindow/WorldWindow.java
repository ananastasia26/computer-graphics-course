package com.nsu.fit.leonova.view.windows.worldWindow;

import com.nsu.fit.leonova.controller.WorldController;
import com.nsu.fit.leonova.model.Point3D;
import com.nsu.fit.leonova.observer.WorldObserver;
import com.nsu.fit.leonova.view.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class WorldWindow extends JFrame implements WorldObserver {
    private ImageManager imageManager;
    private WorldController worldController;
    private FigureSettingsPanel figureSettingsPanel;

    public WorldWindow(int width, int height, WorldController worldController) throws HeadlessException {
        super("Wireframe");
        this.worldController = worldController;
        figureSettingsPanel = new FigureSettingsPanel(worldController);
        imageManager = new ImageManager(width, height);
        imageManager.setMouseListener(new MyMouseAdapter());
        JToolBar toolBar = createToolBar();
        JMenuBar menuBar = createMenuBar();
        add(toolBar, BorderLayout.PAGE_START);
        add(imageManager, BorderLayout.CENTER);
        add(figureSettingsPanel, BorderLayout.EAST);
        setJMenuBar(menuBar);
        //setMinimumSize(new Dimension(Globals.MIN_FRAME_WIDTH, Globals.MIN_FRAME_HEIGHT));
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setWorldController(WorldController worldController) {
        this.worldController = worldController;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton settings = createButton(JButton.class, e -> worldController.settingsButtonPressed(),  "icons/icons8-table-of-content-16.png", "Create B-spline");
        toolBar.add(settings);
        return toolBar;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menu = new JMenuBar();
        return menu;
    }

    private  <T extends AbstractButton> T createButton(Class<T> targetClass, ActionListener listener, String... args) {
        try {
            T button = targetClass.newInstance();
            button.addActionListener(listener);
            if (args.length == 2) {
                button.setIcon(new ImageIcon(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(args[0]))));
                button.setToolTipText(args[1]);
            }
            if (args.length == 1) {
                button.setText(args[0]);
            }
            return button;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setMainImage(BufferedImage image) {
        imageManager.setImage(image);
    }

    @Override
    public void addFigure(String name) {
        figureSettingsPanel.addFigure(name);
    }

    @Override
    public void removeFigure(int index) {
        figureSettingsPanel.removeFigure(index);
    }

    @Override
    public void renameFigure(String name, int index) {
        figureSettingsPanel.renameFigure(name, index);
    }

    @Override
    public void setInfo(Point3D figureCenter) {
        figureSettingsPanel.setInfo(figureCenter);
    }

    private class MyMouseAdapter extends MouseAdapter {

        private Point oldPoint;

        @Override
        public void mousePressed(MouseEvent e){
            oldPoint = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e){
            if(oldPoint != null){
                int dx = oldPoint.x - e.getX();
                int dy = oldPoint.y - e.getY();
                if(dx != 0){
                    worldController.shiftX(dx);
                }
                if(dy != 0){
                    worldController.shiftY(dy);
                }
            }
            oldPoint = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e){
            oldPoint = null;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e){
            System.out.println("It's work!");
        }

    }
}