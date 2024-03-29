package com.nsu.fit.leonova.model;

import com.nsu.fit.leonova.model.filters.*;
import com.nsu.fit.leonova.model.volumeRendering.VolumeRendering;
import com.nsu.fit.leonova.observer.Observable;
import com.nsu.fit.leonova.observer.Observer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageConsumerImpl implements Observable, ImageConsumer {

    private BufferedImage sourceImage;
    private BufferedImage workingImage;
    private BufferedImage filteredImage;

    private HashMap<FiltersType, Filter> filters = new HashMap<>();
    private ArrayList<Observer> observers = new ArrayList<>();

    private VolumeRendering volumeRendering = new VolumeRendering();

    public ImageConsumerImpl() {
        filters.put(FiltersType.BLUR, new BlurFilter());
        filters.put(FiltersType.DESATURATION, new DesaturateFilter());
        filters.put(FiltersType.EMBOSS, new EmbossFilter());
        filters.put(FiltersType.FS_DITHERING, new FSDitheringFilter());
        filters.put(FiltersType.INVERT, new InvertFilter());
        filters.put(FiltersType.ORDERED, new OrderedDitheringFilter());
        filters.put(FiltersType.ROBERTS, new RobertsFilter());
        filters.put(FiltersType.SHARPEN, new SharpenFilter());
        filters.put(FiltersType.SOBEL, new SobelFilter());
        filters.put(FiltersType.WATERCOLOR, new WaterColorFilter());
        filters.put(FiltersType.ZOOM, new ZoomFilter());
        filters.put(FiltersType.GAMMA, new GammaFilter());
        filters.put(FiltersType.ROTATION, new RotationFilter());
        filters.put(FiltersType.VOLUME_RENDERING, volumeRendering);
    }

    @Override
    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    @Override
    public void deleteObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void useFilterWithImage(FiltersType filterType, double[] parameters) {
        try{
            if(workingImage != null){
                Filter filter = filters.get(filterType);
                filteredImage = filter.applyFilter(workingImage, parameters);
                setFilteredPicture();
            }
            else{
                for(Observer observer : observers){
                    observer.errorOccurred("Select area for working!");
                }
            }
        } catch (IllegalArgumentException e){
            for(Observer observer : observers){
                observer.errorOccurred(e.getMessage());
            }
        }
    }

    @Override
    public void openSourcePicture(File file) {
        try {
            BufferedImage loadedImage = ImageIO.read(file);
            removeAllImages();
            this.sourceImage = loadedImage;
            for(Observer observer : observers){
                observer.setSourceImage(loadedImage);
            }
        } catch (IOException e) {
            for(Observer observer : observers){
                observer.errorOccurred(e.getMessage());
            }
        }
    }

    @Override
    public void filteredImageAsWorking() {
        if(filteredImage != null){
            workingImage = filteredImage;
            setWorkingPicture();
        }
    }

    @Override
    public void workingImageAsFiltered() {
        if(workingImage != null){
            filteredImage = workingImage;
            setFilteredPicture();
        }
    }

    @Override
    public void removeAllImages() {
        sourceImage = workingImage = filteredImage = null;
        for(Observer observer : observers){
            observer.removeAllImages();
        }
    }

    @Override
    public void cropPicture(Point leftTop, int width, int height) {
        if(sourceImage != null){
            try{
                workingImage = sourceImage.getSubimage(leftTop.x, leftTop.y, width, height);
                setWorkingPicture();
            }
            catch (RasterFormatException e){
                System.out.println((leftTop.x + width) + " " + (leftTop.y + height) + " " + sourceImage.getWidth() + " " + sourceImage.getHeight());
            }
        }
    }

    @Override
    public void emissionWasPressed() {
        volumeRendering.emissionWasPressed();
    }

    @Override
    public void absorptionWasPressed() {
        volumeRendering.absorptionWasPressed();
    }

    @Override
    public void openConfigFile(File file) {
        try{
            ConfigFileLoader fileLoader = new ConfigFileLoader(file);
            volumeRendering.setAbsorptions(fileLoader.getAbsorption());
            volumeRendering.setEmissions(fileLoader.getEmission());
            volumeRendering.setCharges(fileLoader.getCharges());
            for(Observer observer : observers){
                observer.setOneGraphic(fileLoader.getAbsorption());
                int[][] intColors = fileLoader.getEmission();
                int width = fileLoader.getEmissionWidth();
                int height = fileLoader.getEmissionHeight();
                double[][] colors = createFloatColors(intColors, width, height);
                observer.setManyGraphics(colors, height, width);
            }
        }
        catch (IllegalArgumentException e){
            for(Observer observer : observers){
                observer.errorOccurred(e.getMessage());
            }
        }
    }

    @Override
    public void saveImage(File file) {
        try {
            ImageIO.write(filteredImage, "bmp", file);
        } catch (IOException e) {
            for(Observer observer : observers){
                observer.errorOccurred(e.getMessage());
            }
        }
    }

    private double[][] createFloatColors(int[][] intColor, int width, int height){
        double[][] floatColor = new double[height][width];
        for(int i = 0; i < height; ++i){
            for (int j = 0; j < width; ++j){
                floatColor[i][j] = intColor[j][i] / 255.;
            }
        }
        return floatColor;
    }

    private void setWorkingPicture() {
        for(Observer observer : observers){
            observer.setWorkingImage(workingImage);
        }
    }

    private void setFilteredPicture() {
        for(Observer observer : observers){
            observer.setFilteredImage(filteredImage);
        }
    }
}
