package com.nsu.fit.leonova.view;

import com.nsu.fit.leonova.controller.BSplineController;
import com.nsu.fit.leonova.observer.Observer;
import com.nsu.fit.leonova.view.windows.BSplineWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class MainWindow extends JFrame implements Observer {
    private ImageManager imageManager;
    private BSplineWindow bSplineWindow = new BSplineWindow();

    public MainWindow(int width, int height) throws HeadlessException {
        super("Wireframe");
        imageManager = new ImageManager(width, height);
        JToolBar toolBar = createToolBar();
        JMenuBar menuBar = createMenuBar();
        add(toolBar, BorderLayout.PAGE_START);
        add(imageManager, BorderLayout.CENTER);
        setJMenuBar(menuBar);
        //setMinimumSize(new Dimension(Globals.MIN_FRAME_WIDTH, Globals.MIN_FRAME_HEIGHT));
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setBSplineController(BSplineController bSplineController){
        bSplineWindow.setController(bSplineController);
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton settings = createButton(JButton.class, e -> bSplineWindow.setVisible(true),  "icons/icons8-table-of-content-16.png", "Create B-spline");
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
    public void setBSpline(BufferedImage bSpline) {
        bSplineWindow.setImage(bSpline);
    }
}
