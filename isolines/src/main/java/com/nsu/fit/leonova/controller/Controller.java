package com.nsu.fit.leonova.controller;

import com.nsu.fit.leonova.model.Model;
import com.nsu.fit.leonova.model.SafeColor;
import com.nsu.fit.leonova.model.graphicProvider.GraphicValues;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Controller implements LogicController, ImageController, FileController {
    private Model model;
    private boolean gradient = false;

    private int imageWidth, imageHeight, legendWidth, legendHeight;

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public void createGraphic(int width, int height) {
        imageWidth = width;
        imageHeight = height;
        model.createGraphic(gradient, width, height);
    }

    @Override
    public void createLegend(int width, int height) {
        legendWidth = width;
        legendHeight = height;
        model.createLegend(gradient, width, height);
    }

    @Override
    public void gradientWasPressed() {
        gradient = !gradient;
        model.createGraphic(gradient, imageWidth, imageHeight);
        model.createLegend(gradient, legendWidth, legendHeight);
    }

    @Override
    public void drawAllLevelIsolines() {
        model.drawAllLevelIsolines();
    }

    @Override
    public void eraseIsolines() {
        model.removeIsolines();
        model.createGraphic(gradient, imageWidth, imageHeight);
    }

    @Override
    public void drawNet() {
        model.drawNet();
    }

    @Override
    public void pivotPoints() {
        model.pivotPoints();
    }

    @Override
    public void setParameters(GraphicValues graphicValues, int k, int m) {
        model.setNet(k, m);
        model.setDefinitionArea(graphicValues);
        model.createGraphic(gradient, imageWidth, imageHeight);
    }

    @Override
    public void resizeImage(int width, int height) {
        imageWidth = width;
        imageHeight = height;
        legendHeight = height;
        createGraphic(width, height);
        createLegend(legendWidth, height);
    }

    @Override
    public void imageWasClicked(Point pressedPixel) {
        model.clickedIsoline(pressedPixel);
    }

    @Override
    public void imageWasDragged(Point draggedPixel) {
        model.draggedIsoline(draggedPixel);
    }

    @Override
    public void imageWasMoved(Point pixel) {
        model.pixelToCoordinate(pixel);
    }

    @Override
    public void openFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            String[] netValues = readNextNumbers(scanner, 2);
            int k = Integer.parseInt(netValues[0]);
            int m = Integer.parseInt(netValues[1]);
            SafeColor[] colors = readColors(scanner);
            SafeColor isolineColor = readNextColor(scanner);
            model.setColorsRGB(colors);
            model.setNet(k, m);
            model.setIsolineColor(isolineColor);
            createGraphic(imageWidth, imageHeight);
            createLegend(legendWidth, legendHeight);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private SafeColor[] readColors(Scanner scanner){
        int numOfColor = Integer.parseInt(readNextNumbers(scanner, 1)[0]);
        SafeColor[] colors = new SafeColor[numOfColor];
        for(int i =0; i < numOfColor; ++i){
            colors[i] = readNextColor(scanner);
        }
        return colors;
    }

    private SafeColor readNextColor(Scanner scanner) {
        String[] numbers = readNextNumbers(scanner, 3);
        int r = Integer.parseInt(numbers[0]);
        int g = Integer.parseInt(numbers[1]);
        int b = Integer.parseInt(numbers[2]);
        if(r < 0 || r > 255 || g < 0 || g > 255 | b < 0 || b > 255){
            throw new IllegalArgumentException("Wrong color value!");
        }
        return new SafeColor(r, g, b);
    }

    private String[] readNextNumbers(Scanner scanner, int n) {
        String line;
        do {
            line = scanner.nextLine();
            int commentBegin = line.indexOf("//");
            if (commentBegin != -1) {
                line = line.substring(0, commentBegin);
            }
        } while ("".equals(line));
        String[] numbers = line.split(" ");
        if (numbers.length != n) {
            throw new IllegalArgumentException("Bad number of parameters");
        }
        return numbers;
    }



}
