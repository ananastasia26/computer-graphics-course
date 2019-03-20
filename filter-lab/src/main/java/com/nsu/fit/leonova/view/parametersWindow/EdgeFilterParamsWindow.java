package com.nsu.fit.leonova.view.parametersWindow;

import com.nsu.fit.leonova.controller.ImageController;
import com.nsu.fit.leonova.model.FiltersType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EdgeFilterParamsWindow extends JFrame {

    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 255;
    private final int DEFAULT_VALUE = 100;

    private ImageController controller;
    private FiltersType type;
    private JTextField edgeTresholdTF;
    private JButton apply = new JButton("Apply");

    public EdgeFilterParamsWindow(){
        super("Edge treshold");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(new JLabel("Edge treshold"), BorderLayout.NORTH);
        edgeTresholdTF = new JTextField(String.valueOf(DEFAULT_VALUE));

        JSlider edgeSlider = new JSlider(MIN_VALUE, MAX_VALUE, Integer.parseInt(edgeTresholdTF.getText()));
        edgeSlider.addChangeListener(e -> edgeTresholdTF.setText(String.valueOf(edgeSlider.getValue())));
        edgeTresholdTF.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent ke) {
                String typed = edgeTresholdTF.getText();
                edgeSlider.setValue(0);
                int value = Integer.parseInt(typed);
                edgeSlider.setValue(value);
            }
        });
        mainPanel.add(edgeTresholdTF, BorderLayout.WEST);
        mainPanel.add(edgeSlider, BorderLayout.CENTER);
        mainPanel.add(apply, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
    }

    public void setController(ImageController controller) {
        //TODO: ask!!
        this.controller = controller;
        apply.addActionListener(e -> {
            try{
                int value = Integer.parseInt(edgeTresholdTF.getText());
                if(value < 1 || value > 255){
                    new ErrorShowingWindow("Value must be in range [" + MIN_VALUE + "; "  + MAX_VALUE + "]").show();
                    return;
                }
                double[] params = {value};
                controller.filterImage(type, params);
                setVisible(false);
            } catch (NumberFormatException e1){
                new ErrorShowingWindow("Not an integer").show();
            }
        });
    }

    public void setType(FiltersType type) {
        this.type = type;
    }

}
