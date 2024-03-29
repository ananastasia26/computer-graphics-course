package com.nsu.fit.leonova.view;

import com.nsu.fit.leonova.controller.ImageController;
import com.nsu.fit.leonova.globals.GlobalsImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImagesHolder extends JPanel {

    private ImageController imageController;
    private SelectableImageManager sourceImage = new SelectableImageManager();
    private ImageManager chosenArea = new ImageManager();
    private ImageManager filteredArea = new ImageManager();

    public ImagesHolder() throws IOException {
        setLayout(new FlowLayout(FlowLayout.LEFT, GlobalsImage.BORDER, GlobalsImage.BORDER));

        add(sourceImage);
        add(chosenArea);
        add(filteredArea);
    }

    public void setImageController(ImageController imageController) {
        this.imageController = imageController;
        sourceImage.setImageController(imageController);
    }

    public void setSelected(boolean selected) {
        sourceImage.changeSelect(selected);
    }

    public void setWorkingImage(BufferedImage workingImage){
        this.chosenArea.setImage(workingImage);
    }

    public void setSourceImage(BufferedImage sourceImage){
        this.sourceImage.setImage(sourceImage);
    }

    public void removeAllImages(){
        sourceImage.clearAll();
        chosenArea.clearAll();
        filteredArea.clearAll();
    }

    public void setFilteredImage(BufferedImage filteredImage){
        this.filteredArea.setImage(filteredImage);
    }
}
