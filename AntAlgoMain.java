import entities.World_descr;
import utils.CommonUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class AntAlgoMain implements GuiCallback {

    private static AntAlgoMain instance = null;
    private final Gui gui;;

    private Image mapbuffer = null;

    private int viewModeMap;
    private int viewModeFeatures;
    private int[] mapInGPU;    //Карта для GPU

    private Thread thread = null;
    private boolean started = true; // поток работает?

    public AntAlgoMain() {
        World_descr.getInstance();
        gui = new Gui(this);
        gui.init();
    }


    private void GUI_start(){
        return;
    }

    public static void main(String[] args) {
        System.out.println("Ant Algorithm");
        instance = new AntAlgoMain();
    }

    public void paintMapView() {
        int mapred;
        int mapgreen;
        int mapblue;
        mapbuffer = gui.canvas.createImage(World_descr.getInstance().getWidth(), World_descr.getInstance().getHeight()); // ширина - высота картинки
        Graphics g = mapbuffer.getGraphics();

        final BufferedImage image = new BufferedImage(World_descr.getInstance().getWidth(), World_descr.getInstance().getWidth(), BufferedImage.TYPE_INT_RGB);
        final int[] rgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < rgb.length; i++) {
            if (i>=World_descr.getInstance().mapInGPU.length)
                continue;
            switch (viewModeMap) {
                case Consts.VIEW_MODE_HEIGHT: {
                    if (World_descr.getInstance().mapInGPU[i] < World_descr.getInstance().getSea_level()) {                     // подводная часть
                        mapred = 5;
                        mapblue = 140 - (World_descr.getInstance().getSea_level() - World_descr.getInstance().mapInGPU[i]) * 3;
                        mapgreen = 150 - (World_descr.getInstance().getSea_level() - World_descr.getInstance().mapInGPU[i]) * 10;
                        if (mapgreen < 10) mapgreen = 10;
                        if (mapblue < 20) mapblue = 20;
                    } else {                                        // надводная часть
                        mapred = (int) (150 + (World_descr.getInstance().mapInGPU[i] - World_descr.getInstance().getSea_level()) * 2.5);
                        mapgreen = (int) (100 + (World_descr.getInstance().mapInGPU[i] - World_descr.getInstance().getSea_level()) * 2.6);
                        mapblue = 50 + (World_descr.getInstance().mapInGPU[i] - World_descr.getInstance().getSea_level()) * 3;
                        if (mapred > 255) mapred = 255;
                        if (mapgreen > 255) mapgreen = 255;
                        if (mapblue > 255) mapblue = 255;
                    }
                    rgb[i] = (mapred << 16) | (mapgreen << 8) | mapblue;
                }
                break;
/*
                case Consts.VIEW_MODE_SOLAR: {
                    int x= i % width;
                    int y = i / width;
                    mapblue = 255;
                    mapgreen = 0;
                    int valueToDraw = mapObjectsLayer[x][y].getSunLevel();
                    mapred = (int) valueToDraw * 255 / maxsunpower;
                    rgb[i] = (mapred << 16) | (mapgreen << 8) | mapblue;

                }
                break;
                case Consts.VIEW_MODE_MINERAL: {
                    int x= i % width;
                    int y = i / width;
                    mapred = 0;
                    mapgreen = 160;
                    int valueToDraw = mapObjectsLayer[x][y].getMineralLevel();
                    mapblue = (int) valueToDraw * 255 / maxminerals;
                    rgb[i] = (mapred << 16) | (mapgreen << 8) | mapblue;

                }
                break;

 */
            }
        }
        g.drawImage(image, 0, 0, null);
    }


    //    @Override
    public void paint1() {

        Image buf = gui.canvas.createImage(World_descr.getInstance().getWidth(), World_descr.getInstance().getHeight()); //Создаем временный буфер для рисования
        Graphics g = buf.getGraphics(); //подеменяем графику на временный буфер
        g.drawImage(mapbuffer, 0, 0, null);

        final BufferedImage image = new BufferedImage(World_descr.getInstance().getWidth(), World_descr.getInstance().getHeight(), BufferedImage.TYPE_INT_ARGB);
        final int[] rgb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

//        population = 0;
//        organic = 0;
        int mapred, mapgreen, mapblue;

        for (int i = 0; i < World_descr.getInstance().getWidth(); i++) {
            for (int j = 0; j < World_descr.getInstance().getHeight(); j++) {
                mapred = mapgreen = mapblue = 0;
                if (viewModeFeatures == Consts.VIEW_MODE_BOT_TRACK || viewModeFeatures == Consts.VIEW_MODE_BOT_BOT_TRACK) {
                    mapgreen = Math.round(2.55f * World_descr.getInstance().getPointFeatures(new Point(i,j)).getTrace_force());
                    mapgreen = mapgreen%255;
//                    mapgreen = Math.min(mapgreen, 255);
                }
                if (viewModeFeatures == Consts.VIEW_MODE_BOT_BASE || viewModeFeatures == Consts.VIEW_MODE_BOT_BOT_TRACK) {
                    if (World_descr.getInstance().getPointFeatures(new Point(i,j)).getBot()!=null) {
                        mapred = 255;
                        mapgreen = 0;
                    }
                    else
                        mapred = 0;

                }

                if (mapred+mapgreen+mapblue>0)
                    rgb[j * World_descr.getInstance().getWidth() + i] = (255<<24) |(mapred << 16) | (mapgreen<<8) | (mapblue);
            }
        }
/*
        for (UUID botuid:botsLinkedList) {
            BotV2 currbot = null;
            try {
                currbot = allBots.get(botuid);
            }
            catch (Exception e) {
                continue;
            }
            if (viewModeBot == Consts.VIEW_MODE_BOT_BASE) {
                rgb[currbot.getPosition().y * width + currbot.getPosition().x] = currbot.getColor();
            } else if (viewModeBot == Consts.VIEW_MODE_BOT_HP) {
                mapgreen = 255 - (int) (currbot.getHP() * 0.25);
                if (mapgreen < 0) mapgreen = 0;
                rgb[currbot.getPosition().y * width + currbot.getPosition().x] = (255 << 24) | (0 << 16) | (mapgreen << 8);
            }
//                    else if (viewMode == Consts.VIEW_MODE_MINERAL) {
//                        mapblue = 255 - (int) (currentbot.mineral * 0.5);
//                        if (mapblue < 0) mapblue = 0;
//                        rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (0) | (255 << 8) | mapblue;
//                    }
//                    else if (viewMode == VIEW_MODE_COMBINED) {
//                        mapgreen = (int) (currentbot.c_green * (1 - currentbot.health * 0.0005));
//                        if (mapgreen < 0) mapgreen = 0;
//                        mapblue = (int) (currentbot.c_blue * (0.8 - currentbot.mineral * 0.0005));
//                        rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (currentbot.c_red << 16) | (mapgreen << 8) | mapblue;
//                    }
            else if (viewModeBot == Consts.VIEW_MODE_BOT_AGE) {
                mapred = 255 - (int) (currbot.getAge() / 4);
                if (mapred < 0) mapred = 0;
                rgb[currbot.getPosition().y * width + currbot.getPosition().x] = (255 << 24) | (mapred << 16) | (0) | 255;
            }
            else if (viewModeBot == Consts.VIEW_MODE_BOT_FAMILY) {
                rgb[currbot.getPosition().y * width + currbot.getPosition().x] = currbot.getStrain_color();
            }
            population++;

            // show map objects
        }
*/
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                if (checkBotAtPos(new MapPosition(x,y))) {
//                    if (viewMode == Consts.VIEW_MODE_BASE) {
//                        rgb[y * width + x] = allBots.get(mapBotsLayer[x][y]).getColor();
//                    }
//                    else if (viewMode == Consts.VIEW_MODE_HP) {
//                        mapgreen = 255 - (int) (allBots.get(mapBotsLayer[x][y]).getHP() * 0.25);
//                        if (mapgreen < 0) mapgreen = 0;
//                        rgb[y * width + x] = (255 << 24) | (255 << 16) | (mapgreen << 8);
//                    }
//                    else if (viewMode == Consts.VIEW_MODE_MINERAL) {
//                        mapblue = 255 - (int) (currentbot.mineral * 0.5);
//                        if (mapblue < 0) mapblue = 0;
//                        rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (0) | (255 << 8) | mapblue;
//                    }
//                    else if (viewMode == VIEW_MODE_COMBINED) {
//                        mapgreen = (int) (currentbot.c_green * (1 - currentbot.health * 0.0005));
//                        if (mapgreen < 0) mapgreen = 0;
//                        mapblue = (int) (currentbot.c_blue * (0.8 - currentbot.mineral * 0.0005));
//                        rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (currentbot.c_red << 16) | (mapgreen << 8) | mapblue;
//                    }
//                    else if (viewMode == VIEW_MODE_AGE) {
//                        mapred = 255 - (int) (Math.sqrt(currentbot.age) * 4);
//                        if (mapred < 0) mapred = 0;
//                        rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (mapred << 16) | (0) | 255;
//                    }
//                    else if (viewMode == VIEW_MODE_FAMILY) {
//                        rgb[currentbot.y * width + currentbot.x] = currentbot.c_family;
//                    }
//                    population++;
//                }
        // show map objects
//            }
//        }

//        while (currentbot != zerobot) {
//            if (currentbot.alive == 3) {                      // живой бот
//                if (viewMode == Consts.VIEW_MODE_BASE) {
//                    rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (currentbot.c_red << 16) | (currentbot.c_green << 8) | currentbot.c_blue;
//                }
//                else if (viewMode == Consts.VIEW_MODE_HP) {
//                    mapgreen = 255 - (int) (currentbot.health * 0.25);
//                    if (mapgreen < 0) mapgreen = 0;
//                    rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (255 << 16) | (mapgreen << 8);
//                }
//                else if (viewMode == VIEW_MODE_MINERAL) {
//                    mapblue = 255 - (int) (currentbot.mineral * 0.5);
//                    if (mapblue < 0) mapblue = 0;
//                    rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (0) | (255 << 8) | mapblue;
//                }
//                else if (viewMode == VIEW_MODE_COMBINED) {
//                    mapgreen = (int) (currentbot.c_green * (1 - currentbot.health * 0.0005));
//                    if (mapgreen < 0) mapgreen = 0;
//                    mapblue = (int) (currentbot.c_blue * (0.8 - currentbot.mineral * 0.0005));
//                    rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (currentbot.c_red << 16) | (mapgreen << 8) | mapblue;
//                }
//                else if (viewMode == VIEW_MODE_AGE) {
//                    mapred = 255 - (int) (Math.sqrt(currentbot.age) * 4);
//                    if (mapred < 0) mapred = 0;
//                    rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (mapred << 16) | (0) | 255;
//                }
//                else if (viewMode == VIEW_MODE_FAMILY) {
//                    rgb[currentbot.y * width + currentbot.x] = currentbot.c_family;
//                }
//                population++;
//            } else if (currentbot.alive == 1) {                                            // органика, известняк, коралловые рифы
//                if (map[currentbot.x][currentbot.y] < sealevel) {                     // подводная часть
//                    mapred = 20;
//                    mapblue = 160 - (sealevel - map[currentbot.x][currentbot.y]) * 2;
//                    mapgreen = 170 - (sealevel - map[currentbot.x][currentbot.y]) * 4;
//                    if (mapblue < 40) mapblue = 40;
//                    if (mapgreen < 20) mapgreen = 20;
//                } else {                                    // скелетики, трупики на суше
//                    mapred = (int) (80 + (map[currentbot.x][currentbot.y] - sealevel) * 2.5);   // надводная часть
//                    mapgreen = (int) (60 + (map[currentbot.x][currentbot.y] - sealevel) * 2.6);
//                    mapblue = 30 + (map[currentbot.x][currentbot.y] - sealevel) * 3;
//                    if (mapred > 255) mapred = 255;
//                    if (mapblue > 255) mapblue = 255;
//                    if (mapgreen > 255) mapgreen = 255;
//                }
//                rgb[currentbot.y * width + currentbot.x] = (255 << 24) | (mapred << 16) | (mapgreen << 8) | mapblue;
//                organic++;
//            }
//            currentbot = currentbot.next;
//        }
//        currentbot = currentbot.next;

        g.drawImage(image, 0, 0, null);

//        gui.generationLabel.setText(" Generation: " + generation);
//        gui.populationLabel.setText(" Population: " + population);
//        gui.organicLabel.setText(" Organic: " + organic);
//        gui.familiesLabel.setText(" Strains: " + allStrains.size());
//        gui.strainsLabel.setText(" Alive: " + liveStrains.size());
//        gui.deadStrainsLabel.setText(" Dead: " + deadStrains.size());
//        gui.mutationsLabel.setText(" Mutations: " + getMutation_count());

        gui.buffer = buf;
        gui.canvas.repaint();
    }


    @Override
    public void mapGenerationStarted(int canvasWidth, int canvasHeight) {
        clearMapItems();
        World_descr.getInstance().setWorldSize(canvasWidth, canvasHeight);
        World_descr.getInstance().generateMap((int) (Math.random() * 10000));
//        World_descr.getInstance().setGenom();
        World_descr.getInstance().generateInitGeneration();
        paintMapView();
        paint1();
    }

    @Override
    public boolean startedOrStopped() {
        if (thread == null) {
            thread = new AntAlgoMain.Worker(); // создаем новый поток
            thread.start();
            return true;
        } else {
            started = false;        //Выставляем влаг
            CommonUtils.joinSafe(thread);
            thread = null;
            return false;
        }
    }

    private void clearMapItems() {
        World_descr.getInstance().clearAllBots();
    }

    @Override
    public void viewModeBotChanged(int viewModeBot) {
        viewModeFeatures = viewModeBot;
        paintMapView();
        paint1();
    }

    @Override
    public void setWorldScale(int worldScale) {
//        world.setZoom(worldScale);
    }

    @Override
    public void setLRMirror(boolean lrMirror) {
        World_descr.getInstance().setLR_mirror(lrMirror);
    }

    @Override
    public void setTBMirror(boolean tbMirror) {
        World_descr.getInstance().setTB_mirror(tbMirror);
    }

    @Override
    public void setBotCount(int botCount) {
        World_descr.getInstance().setBotCount(botCount);
    }

    @Override
    public void setTraceDegrade(int traceDegrade) {
        World_descr.getInstance().setTraceDegrade(traceDegrade);
    }

    @Override
    public void setBotMovement(int botMovement) {
        World_descr.getInstance().setBotMovement(botMovement);
    }

    @Override
    public void setBotRotation(int botRotation) {
        World_descr.getInstance().setBotRotation(botRotation);
    }

    @Override
    public void setBotSensorAngle(int botSensorAngle) {
        World_descr.getInstance().setBotSensorAngle(botSensorAngle);
    }

    @Override
    public void setBotSensorDist(int botSensorDist) {
        World_descr.getInstance().setBotSensorDist(botSensorDist);
    }

    @Override
    public void setBotTraceForce(int botTraceForce) {
        World_descr.getInstance().setBotTraceForce(botTraceForce);
    }

    @Override
    public void setBotAvoid_OverTrace(boolean botAvoid_overTrace) {
        World_descr.getInstance().setBotAvoidOverTrace(botAvoid_overTrace);
    }

    @Override
    public void setBotOverTrace(int botOverTrace) {
        World_descr.getInstance().setBotOverTrace(botOverTrace);
    }


    class Worker extends Thread {
        public void run() {
            started = true;         // Флаг работы потока, если false  поток заканчивает работу
            while (started) {       // обновляем матрицу
                long time1 = System.currentTimeMillis();
                World_descr.getInstance().degrade_trace();
                World_descr.getInstance().moveAllBots();

                paintMapView();
                paint1();                           // отображаем текущее состояние симуляции на экран
/*
                if (botsLinkedList.size() == 0){
                    population = 0;
                    JOptionPane.showMessageDialog(gui, "All Dead!!!");
                    started = false;        // Закончили работу
                    return;
                }


                Iterator<UUID> it = allBots.keySet().iterator();
                while (it.hasNext()) {
                    try {
                        UUID botuid = it.next();
                        allBots.get(botuid).step();
                    }
                    catch (Exception e) {

                    }
                }

                generation++;
                long time2 = System.currentTimeMillis();
//                System.out.println("Step execute " + ": " + (time2-time1) + "");
                if (generation % drawstep == 0) {             // отрисовка на экран через каждые ... шагов
                    paintMapView();
                    paint1();                           // отображаем текущее состояние симуляции на экран
                }
                long time3 = System.currentTimeMillis();
*/
//                for (Strain s : AllStrains)
//                    if (!s.isAliveStrain() && s.isStableStrain()) strainDied(s);
//
//                Predicate<Strain> isDead = strain -> !strain.isAliveStrain();
//                AllStrains.removeIf(isDead);
//
//                stableStrainCount = (int) AllStrains.stream().filter(Strain::isStableStrain).count();
//                deadStrainCount = deadStrains.size();
//                System.out.println("Paint: " + (time3-time2));
            }
            started = false;        // Закончили работу
        }
    }

}
