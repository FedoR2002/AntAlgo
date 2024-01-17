package entities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class World_descr {

    private int degradeTrace =4;
    private int totalBotCount = 2000;

    private static World_descr instance;
    private int width;
    private int height;
    private boolean LR_mirror;
    private boolean TB_mirror;
    private int sea_level;

    public int[] mapInGPU = null;

    private Bot refBotGenom = null;

    private int zoom;
    private ArrayList<Bot> allBots = new ArrayList<Bot>();

    private PointFeatures[][] features;

    public static World_descr getInstance() {
        if (instance == null)
            instance = new World_descr();
        return instance;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLR_mirror(boolean LR_mirror) {
        this.LR_mirror = LR_mirror;
    }

    public void setTB_mirror(boolean TB_mirror) {
        this.TB_mirror = TB_mirror;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isLR_mirror() {
        return LR_mirror;
    }

    public boolean isTB_mirror() {
        return TB_mirror;
    }

    public int getZoom() {
        return zoom;
    }

    public int getSea_level(){
        return sea_level;
    }

    public void setSea_level(int sea_level){
        this.sea_level = sea_level;
    }

    private World_descr() {

        width = 800;
        height = 600;
        LR_mirror = false;
        TB_mirror = false;
        sea_level = 100;
        zoom = 100;
        features=new PointFeatures[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                features[i][j]=new PointFeatures();
        refBotGenom = new Bot();
    }


    public PointFeatures getPointFeatures(Point checkPoint) {
        return features[checkPoint.x][checkPoint.y];
    }

    public void degrade_trace(){
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                features[i][j].decTrace_force(degradeTrace);
    }

    public void moveAllBots(){
        for (Bot bot:allBots) {
            bot.move();
        }
    }

    public void removeBot(Point position) {
        features[position.x][position.y].setBot(null);
    }

    public void putBot(Point position, UUID uid) {
        features[position.x][position.y].setBot(uid);
    }

    public void clearAllBots() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                features[i][j]=new PointFeatures();
            }
        }
        allBots.clear();
    }

    public void setWorldSize(int w, int h) {
        width = w;
        height = h;
        features = new PointFeatures[w][h];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                features[i][j] = new PointFeatures();
            }
        }
        mapInGPU = new int[w*h];
        Arrays.fill(mapInGPU, 0);
    }

    public void setGenom(int move, int rotate, int sensAngle, int sensDist){
        refBotGenom.setMovement_distance(move);
        refBotGenom.setRotation_angle(rotate);
        refBotGenom.setSide_sensor_angle(sensAngle);
        refBotGenom.setSensor_distance(sensDist);
        for (Bot bot:allBots) {
            bot.setMovement_distance(move);
            bot.setRotation_angle(rotate);
            bot.setSide_sensor_angle(sensAngle);
            bot.setSensor_distance(sensDist);
        }
    }

    public void setBotCount(int botCount) {
        totalBotCount = botCount;
    }

    public void setTraceDegrade(int traceDegrade){
        degradeTrace = traceDegrade;
    }

    // генерируем карту
    public void generateMap(int seed) {
        Arrays.fill(mapInGPU, 0);
        return;
/*
        generation = 0;
        this.mapHeightLayer = new int[width][height];
        this.mapBotsLayer = new UUID[width][height];
        this.mapObjectsLayer = new PlaceProp[width][height];

        Perlin2D perlin = new Perlin2D(seed);
        int sunPower=0;
        int mineralLevel=0;
        maxsunpower = 0;
        maxminerals = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapObjectsLayer[x][y] = new PlaceProp();
                float f = (float) perlinValue;
                float value = perlin.getNoise(x / f, y / f, 8, 0.45f);        // вычисляем точку ландшафта
                int h = (int) (value * 255 + 128) & 255;
                mapHeightLayer[x][y] = h;
//                worldMap[x][y].level = map[x][y];
                // set solar power
                if ((h > sealevel) && (h <= sealevel + 100))
                    sunPower = (int) ((sealevel + 100 - h) * 0.2); // формула вычисления энергии
                if ((h > sealevel - 50) && (h <= sealevel))
                    sunPower = (int) ((h - sealevel + 50) * 0.2); // формула вычисления энергии
                mapObjectsLayer[x][y].setSunLevel(sunPower);
                maxsunpower = Math.max(maxsunpower, sunPower);
                // set mineral level
                if ((h > sealevel - 100) && (h <= sealevel+5))
                    mineralLevel = (int) ((sealevel - h+5) * 0.2); // формула вычисления минералов
                mapObjectsLayer[x][y].setMineralLevel(mineralLevel);
                maxminerals = Math.max(maxminerals, mineralLevel);

            }
        }
        mapInGPU = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mapInGPU[j * width + i] = mapHeightLayer[i][j];
            }
        }
        CommonConsts.regenerate();
        CommonConsts.clearFamilies();

        System.out.println("max sun power:" + maxsunpower);
        System.out.println("max minerals:" + maxminerals);

 */
    }

    public void generateInitGeneration(){
        if (totalBotCount > width * height)
            return;
        Bot newBot = new Bot();
        newBot.setPosition((int) (Math.random()*width), (int) (Math.random()*height));
        newBot.setDirection((int) (Math.random()*360));
        newBot.setMovement_distance(5/*(int) (Math.random()*10)*/);
        newBot.setRotation_angle((int) (Math.random()*180));
        newBot.setSensor_distance(20/*(int) (Math.random()*10)*/);
        newBot.setSide_sensor_angle((int) (Math.random()*180));

        for (int i = 0; i < totalBotCount; i++) {
            Bot nBot = new Bot(newBot);
            nBot.setPosition((int) (Math.random()*width), (int) (Math.random()*height));
            nBot.setDirection((int) (Math.random()*360));
            allBots.add(nBot);
            putBot(nBot.getPosition(), nBot.getUID());
        }
    }

    public void setBotMovement(int botMovement) {
        refBotGenom.setMovement_distance(botMovement);
        for (Bot bot:allBots) {
            bot.setMovement_distance(botMovement);
        }
    }

    public void setBotRotation(int botRotation) {
        refBotGenom.setRotation_angle(botRotation);
        for (Bot bot:allBots) {
            bot.setRotation_angle(botRotation);
        }
    }

    public void setBotSensorAngle(int botSensorAngle) {
        refBotGenom.setSide_sensor_angle(botSensorAngle);
        for (Bot bot:allBots) {
            bot.setSide_sensor_angle(botSensorAngle);
        }
    }

    public void setBotSensorDist(int botSensorDist) {
        refBotGenom.setSensor_distance(botSensorDist);
        for (Bot bot:allBots) {
            bot.setSensor_distance(botSensorDist);
        }
    }

    public void setBotTraceForce(int botTraceForce) {
        refBotGenom.setTraceForce(botTraceForce);
        for (Bot bot:allBots) {
            bot.setTraceForce(botTraceForce);
        }
    }

    public void setBotAvoidOverTrace(boolean botAvoid_overTrace) {
        refBotGenom.setAvoid_overTrace(botAvoid_overTrace);
        for (Bot bot:allBots) {
            bot.setAvoid_overTrace(botAvoid_overTrace);
        }
    }

    public void setBotOverTrace(int botOverTrace) {
        refBotGenom.setOverTraceLevel(botOverTrace);
        for (Bot bot:allBots) {
            bot.setOverTraceLevel(botOverTrace);
        }
    }
}
