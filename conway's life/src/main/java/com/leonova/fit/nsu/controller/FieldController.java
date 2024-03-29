package com.leonova.fit.nsu.controller;

import com.leonova.fit.nsu.model.Cell;
import com.leonova.fit.nsu.model.FieldModel;
import com.leonova.fit.nsu.model.GameOptions;
import com.leonova.fit.nsu.model.Position;
import com.leonova.fit.nsu.view.GraphicsOptions;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class FieldController implements GameController, FileManager {
    private final static int TIME_TO_SLEEP = 1000;
    private FieldModel field;
    private GameOptions gameOptions;
    private boolean running = false;

    private int rows;
    private int columns;

    public FieldController(GameOptions gameOptions, int rows, int columns) {
        this.gameOptions = gameOptions;
        this.rows = rows;
        this.columns = columns;
    }

    public void setField(FieldModel field) {
        this.field = field;
    }


    @Override
    public void clearField() {
        field.clearField();
    }

    @Override
    public void nextStep() {
        field.nextStep();
    }

    @Override
    public void run() {
        running = !running;
        if(running){
            Thread timer = new Thread(){
                @Override
                public void run(){
                    super.run();
                    while (running){
                        try {
                            field.nextStep();
                            sleep(TIME_TO_SLEEP);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            timer.start();
        }
    }

    @Override
    public void setXor() {
        gameOptions.setModeXor(true);
    }

    @Override
    public void setReplace() {
        if(gameOptions.isModeXor()){
            gameOptions.setModeXor(false);
        }
    }

    @Override
    public void displayImpact() {
        field.impactPressed();
    }

    @Override
    public void pressCell(Position position) {

        if(position.getX() >= 0 && position.getX() < rows &&
         position.getY() >= 0 && position.getY() < columns - (position.getX() % 2 == 1 ? 1 : 0)) {
            field.pressedCell(position);
        }
    }

    @Override
    public void newOptions(GameOptions gameOptions, GraphicsOptions graphicsOptions) {
        this.gameOptions = gameOptions;
        columns = graphicsOptions.getColumns();
        rows = graphicsOptions.getRows();
        field.newOptions(gameOptions, graphicsOptions);
    }

    @Override
    public void createNewField(GameOptions gameOptions, GraphicsOptions graphicsOptions) {
        this.gameOptions = gameOptions;
        columns = graphicsOptions.getColumns();
        rows = graphicsOptions.getRows();

        field.newField(gameOptions, graphicsOptions, new ArrayList<>());
    }

    @Override
    public void save(File file, GraphicsOptions graphicsOptions) {
        try(PrintWriter write = new PrintWriter(new FileOutputStream(file))) {

            write.println(graphicsOptions.getRows() + " " + graphicsOptions.getColumns());
            write.println(graphicsOptions.getLineWidth());

            write.println(graphicsOptions.getCellEdge());

            HashSet<Cell> aliveCells = field.getAliveCells();

            write.println(aliveCells.size());
            for(Cell cell : aliveCells){
                write.println(cell.getPosition().getX() + " " + cell.getPosition().getY());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open(File file) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line = reader.readLine();
            line = getLineWithoutComments(line);
            String[] widthHeight = line.split(" ");
            int width = Integer.parseInt(widthHeight[0]);
            int height = Integer.parseInt(widthHeight[1]);

            line = getLineWithoutComments(reader.readLine());
            int weight = Integer.parseInt(line);
            int edge = Integer.parseInt(getLineWithoutComments(reader.readLine()));
            int k = Integer.parseInt(getLineWithoutComments(reader.readLine()));
            ArrayList<Position> aliveCells = new ArrayList<>();
            while (k--> 0){
                String positionsLine = reader.readLine();
                positionsLine = getLineWithoutComments(positionsLine);
                String[] positions = positionsLine.split(" ");
                int x = Integer.parseInt(positions[0]);
                int y = Integer.parseInt(positions[1]);
                aliveCells.add(new Position(x, y));
            }

            GraphicsOptions graphicsOptions = new GraphicsOptions(width, height, weight, edge);
            field.newField(gameOptions, graphicsOptions, aliveCells);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLineWithoutComments(String string){
        if(string.contains("//")){
           int i = string.indexOf("//");
           String subStr = string.substring(0,i);
           return subStr;
        }
        return string;
    }
}
