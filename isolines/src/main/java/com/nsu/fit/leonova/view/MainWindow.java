package com.nsu.fit.leonova.view;

import com.nsu.fit.leonova.controller.ImageController;
import com.nsu.fit.leonova.observers.Observer;
import com.nsu.fit.leonova.controller.LogicController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class MainWindow extends JFrame implements Observer {

    private ImageManager imageManager;
    private ImageManager legend = new ImageManager(10, 500);
    private LogicController logicController;

    private JToggleButton gradient;
    private JCheckBoxMenuItem gradientMenuItem = new JCheckBoxMenuItem("Gradient");

    public MainWindow(int width, int height){
        super("Isolines");
        imageManager = new ImageManager(width, height);
        JToolBar toolBar = createToolBar();
        JMenuBar menuBar = createMenu();
        setJMenuBar(menuBar);
        add(toolBar, BorderLayout.PAGE_START);
        add(imageManager, BorderLayout.CENTER);
        add(legend, BorderLayout.EAST);
        setMinimumSize(new Dimension(width + 40, height));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void setLogicController(LogicController logicController) {
        this.logicController = logicController;
    }

    public void setImageController(ImageController imageController){
        imageManager.setImageController(imageController);
    }

    @Override
    public void setImage(BufferedImage image) {
        imageManager.setImage(image);
    }

    @Override
    public void setLegend(BufferedImage legendImg) {
        legend.setImage(legendImg);
    }

    private JToolBar createToolBar(){
        JToolBar toolBar = new JToolBar();
        gradient = new JToggleButton();
        gradient.setToolTipText("Gradient");
        gradient.setIcon(new ImageIcon(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("icons/gradient.png"))));
        gradient.addActionListener(e -> {
            logicController.gradientWasPressed();
            gradientMenuItem.setSelected(gradient.isSelected());
        });
        JButton eraser = createButton("icons/eraser.png", "Erase isolines", e->logicController.eraseIsolines());
        JButton net = createButton("icons/net.png", "Draw net", e->logicController.drawNet());
        JButton allIsolines = createButton("icons/allIsolines.png", "Draw all isolines", e -> logicController.drawAllLevelIsolines());
        toolBar.add(gradient);
        toolBar.add(eraser);
        toolBar.add(net);
        toolBar.add(allIsolines);
        return toolBar;
    }

    private JMenuBar createMenu(){
        JMenuBar menu = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem openConfigs = createMenuItem("Open configuration file", e -> {});
        file.add(openConfigs);

        JMenu graphic = new JMenu("Graphic");
        graphic.add(gradientMenuItem);
        gradientMenuItem.addActionListener(e -> {
            gradient.setSelected(gradientMenuItem.isSelected());
            logicController.gradientWasPressed();
        });
        JMenuItem isolines = createMenuItem("Show all isolines", e -> logicController.drawAllLevelIsolines());
        graphic.add(isolines);

        menu.add(file);
        menu.add(graphic);

        return menu;
    }

    private JButton createButton(String relativePath, String tip, ActionListener actionListener) {
        JButton button = new JButton();
        button.setIcon(new ImageIcon(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(relativePath))));
        button.setToolTipText(tip);
        button.addActionListener(actionListener);
        return button;
    }

    private JMenuItem createMenuItem(String name, ActionListener listener){
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(listener);
        return item;
    }
}
