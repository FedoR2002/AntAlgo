import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gui extends JFrame implements Consts {

    Image buffer = null;

    JPanel canvas = new JPanel() {
        public void paint(Graphics g) {
            g.drawImage(buffer, 0, 0, null);
        }
    };

    JPanel paintPanel = new JPanel(new FlowLayout());
    JLabel generationLabel = new JLabel(" Generation: 0 ");
    JLabel populationLabel = new JLabel(" Population: 0 ");
    JLabel organicLabel = new JLabel(" Organic: 0 ");
    JLabel familiesLabel = new JLabel("Families: 0");
    JLabel strainsLabel = new JLabel("Stable: 0");
    JLabel deadStrainsLabel = new JLabel("Dead: 0");
    JLabel mutationsLabel = new JLabel("Mutations: 0");

    public static final Map<String, Integer> VIEW_MODE_MAP = new HashMap<>();
    public static final Map<String, Integer> VIEW_MODE_MAP_BOT = new HashMap<>();

//    static {
//        VIEW_MODE_MAP.put("Height", VIEW_MODE_HEIGHT);
//        VIEW_MODE_MAP.put("Solar", VIEW_MODE_SOLAR);
//        VIEW_MODE_MAP.put("Minerals", VIEW_MODE_MINERAL);
//        VIEW_MODE_MAP.put("Radiation", VIEW_MODE_RADIATION);
//        VIEW_MODE_MAP.put("Organics", VIEW_MODE_ORGANICS);
//    }

    static {
        VIEW_MODE_MAP_BOT.put("Агенты", VIEW_MODE_BOT_BASE);
        VIEW_MODE_MAP_BOT.put("След", VIEW_MODE_BOT_TRACK);
        VIEW_MODE_MAP_BOT.put("Агенты+След", VIEW_MODE_BOT_BOT_TRACK);
    }

    private final JRadioButton heightButton = new JRadioButton("Height", true);
    private final JRadioButton solarButton = new JRadioButton("Solar", false);
    private final JRadioButton mineralButton = new JRadioButton("Minerals", false);
    private final JRadioButton radiationButton = new JRadioButton("Radiation", false);
    private final JRadioButton organicsButton = new JRadioButton("Organics", false);


    private final JRadioButton botButton = new JRadioButton("Агенты", true);
    private final JRadioButton traceButton = new JRadioButton("След", false);
    private final JRadioButton btButton = new JRadioButton("Агенты+След", false);
    private final JRadioButton ageButton = new JRadioButton("Age", false);
    private final JRadioButton familyButton = new JRadioButton("Family", false);

    private final JLabel mapSizePercentLabel = new JLabel(wmspText + "100");
    private final JSlider mapSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
    private final JSlider perlinSlider = new JSlider(JSlider.HORIZONTAL, 0, 480, 300);
    private final JButton mapButton = new JButton("Create Map");
    private final JSlider sealevelSlider = new JSlider(JSlider.HORIZONTAL, 0, 256, 145);
    private final JButton startButton = new JButton("Start/Stop");
    private final JSlider drawstepSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 10);

    // world parameters
    private final JSlider botCountSlider = new JSlider(JSlider.HORIZONTAL, 0, 50000, 2000);
    private final JLabel botCountLabel = new JLabel("2000 ботов");
    private final JSlider traceDegradeSlider = new JSlider(JSlider.HORIZONTAL, 0, 20, 1);
    private final JLabel traceDegradeLabel = new JLabel("Затухание следа: "+1);
    private final JCheckBox lrMirrorCheckBox = new JCheckBox("Вертикальное зеркало", false);
    private final JCheckBox tbMirrorCheckBox = new JCheckBox("Горизонтальное зеркало", false);

    // bot genom
    private final JLabel botMovementLabel = new JLabel("Перемещение");
    private final JSlider botMovementSlider = new JSlider(JSlider.HORIZONTAL, 1, 15, 2);
    private final JLabel botRotationLabel = new JLabel("Угол поворота");
    private final JSlider botRotationSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, 45);
    private final JLabel botSideSensorLabel = new JLabel("Угол бок сенсоров");
    private final JSlider botSideSensorSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, 45);
    private final JLabel botSensorDistLabel = new JLabel("Дальность сенсоров");
    private final JSlider botSensorDistSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 6);
    private final JLabel botTraceForceLabel = new JLabel("Сила следа");
    private final JSlider botTraceForceSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 50);
    private final JCheckBox botAvoidOverTrace = new JCheckBox("Избегать суперследа");
    private final JLabel botOverForceLabel = new JLabel("Порог суперследа");
    private final JSlider botOverForceSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 500);

    private final GuiCallback guiCallback;

    public Gui(GuiCallback guiCallback) {
        this.guiCallback = guiCallback;
    }

    public void init() {
        setTitle("AntAlgo "+magor_v+"."+minor_v);
        setSize(new Dimension(1800, 900));
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize(), fSize = getSize();
        if (fSize.height > sSize.height) fSize.height = sSize.height;
        if (fSize.width > sSize.width) fSize.width = sSize.width;
        //setLocation((sSize.width - fSize.width)/2, (sSize.height - fSize.height)/2);
        setSize(new Dimension(sSize.width, sSize.height));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container container = getContentPane();

        paintPanel.setLayout(new BorderLayout());// у этого лейаута приятная особенность - центральная часть растягивается автоматически
        paintPanel.add(canvas, BorderLayout.CENTER);// добавляем нашу карту в центр
        container.add(paintPanel);

        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        container.add(statusPanel, BorderLayout.SOUTH);

        generationLabel.setPreferredSize(new Dimension(140, 18));
        generationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(generationLabel);
        populationLabel.setPreferredSize(new Dimension(140, 18));
        populationLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(populationLabel);
        organicLabel.setPreferredSize(new Dimension(140, 18));
        organicLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(organicLabel);
        familiesLabel.setPreferredSize(new Dimension(140, 18));
        familiesLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(familiesLabel);
        strainsLabel.setPreferredSize(new Dimension(140, 18));
        strainsLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(strainsLabel);
        deadStrainsLabel.setPreferredSize(new Dimension(140, 18));
        deadStrainsLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(deadStrainsLabel);
        mutationsLabel.setPreferredSize(new Dimension(140, 18));
        mutationsLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(mutationsLabel);


        JToolBar toolbar = new JToolBar();
        toolbar.setOrientation(SwingConstants.VERTICAL);
//        toolbar.setBorderPainted(true);
//        toolbar.setBorder(BorderFactory.createLoweredBevelBorder());
        container.add(toolbar, BorderLayout.WEST);

//        toolbar.add(mapSizePercentLabel);
//
//        mapSizeSlider.setMajorTickSpacing(5);
//        mapSizeSlider.setMinorTickSpacing(2);
//        mapSizeSlider.setPaintTicks(true);
//        mapSizeSlider.setPaintLabels(true);
//        mapSizeSlider.setPreferredSize(new Dimension(100, mapSizeSlider.getPreferredSize().height));
//        mapSizeSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
//        toolbar.add(mapSizeSlider);
//
//        JLabel slider1Label = new JLabel("Map scale");
//        toolbar.add(slider1Label);

//        perlinSlider.setMajorTickSpacing(160);
//        perlinSlider.setMinorTickSpacing(80);
//        perlinSlider.setPaintTicks(true);
//        perlinSlider.setPaintLabels(true);
//        perlinSlider.setPreferredSize(new Dimension(100, perlinSlider.getPreferredSize().height));
//        perlinSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
//        toolbar.add(perlinSlider);

//        mapButton.addActionListener(new World.mapButtonAction());
        toolbar.add(mapButton);

//        JLabel slider2Label = new JLabel("Sea level");
//        toolbar.add(slider2Label);
//
//        //sealevelSlider.addChangeListener(new World.sealevelSliderChange());
//        sealevelSlider.setMajorTickSpacing(128);
//        sealevelSlider.setMinorTickSpacing(64);
//        sealevelSlider.setPaintTicks(true);
//        sealevelSlider.setPaintLabels(true);
//        sealevelSlider.setPreferredSize(new Dimension(100, sealevelSlider.getPreferredSize().height));
//        sealevelSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
//        toolbar.add(sealevelSlider);

        //startButton.addActionListener(new World.startButtonAction());
        toolbar.add(startButton);

//        JLabel slider3Label = new JLabel("Draw step");
//        toolbar.add(slider3Label);

//        //drawstepSlider.addChangeListener(new World.drawstepSliderChange());
//        drawstepSlider.setMajorTickSpacing(10);
////        drawstepSlider.setMinimum(1);
////        drawstepSlider.setMinorTickSpacing(64);
//        drawstepSlider.setPaintTicks(true);
//        drawstepSlider.setPaintLabels(true);
//        drawstepSlider.setPreferredSize(new Dimension(100, sealevelSlider.getPreferredSize().height));
//        drawstepSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
//        toolbar.add(drawstepSlider);

//        JToolBar mapTB=new JToolBar();
//        mapTB.setOrientation(SwingConstants.VERTICAL);
//
//        ButtonGroup mapGroup = new ButtonGroup();
//        List<AbstractButton> mapRadioButtons = Arrays.asList(heightButton, solarButton, mineralButton, radiationButton, organicsButton);
//        for (AbstractButton radioButton : mapRadioButtons) {
//            mapGroup.add(radioButton);
//            mapTB.add(radioButton);
//        }

        JToolBar botTB=new JToolBar();
        botTB.setOrientation(SwingConstants.VERTICAL);

        ButtonGroup botGroup = new ButtonGroup();
        List<AbstractButton> botRadioButtons = Arrays.asList(botButton, traceButton, btButton);
        for (AbstractButton radioButton : botRadioButtons) {
            botGroup.add(radioButton);
            botTB.add(radioButton);
        }
//        toolbar.add(mapTB);
        toolbar.add(botTB);

        // world parameters
        JToolBar worldTB=new JToolBar();
        worldTB.setOrientation(SwingConstants.VERTICAL);
        worldTB.add(lrMirrorCheckBox);
        worldTB.add(tbMirrorCheckBox);

        botCountSlider.setMajorTickSpacing(5000);
        botCountSlider.setMinorTickSpacing(1000);
        botCountSlider.setPaintTicks(true);
        botCountSlider.setPaintLabels(false);
        botCountSlider.setPreferredSize(new Dimension(100, botCountSlider.getPreferredSize().height));
        botCountSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        worldTB.add(botCountSlider);
        worldTB.add(botCountLabel);

        traceDegradeSlider.setMajorTickSpacing(5);
        traceDegradeSlider.setMinorTickSpacing(2);
        traceDegradeSlider.setPaintTicks(true);
        traceDegradeSlider.setPaintLabels(true);
        traceDegradeSlider.setPreferredSize(new Dimension(100, traceDegradeSlider.getPreferredSize().height));
        traceDegradeSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        worldTB.add(traceDegradeSlider);
        worldTB.add(traceDegradeLabel);
        toolbar.add(worldTB);

        // bot parameters
        JToolBar genomTB=new JToolBar();
        genomTB.setOrientation(SwingConstants.VERTICAL);
        JLabel gtbLabel = new JLabel("ГЕНОМ");
        genomTB.add(gtbLabel);

        botMovementSlider.setMajorTickSpacing(5);
        botMovementSlider.setMinorTickSpacing(2);
        botMovementSlider.setPaintTicks(true);
        botMovementSlider.setPaintLabels(true);
        botMovementSlider.setPreferredSize(new Dimension(100, botMovementSlider.getPreferredSize().height));
        botMovementSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        genomTB.add(botMovementSlider);
        genomTB.add(botMovementLabel);
        botRotationSlider.setMajorTickSpacing(45);
        botRotationSlider.setMinorTickSpacing(15);
        botRotationSlider.setPaintTicks(true);
        botRotationSlider.setPaintLabels(true);
        botRotationSlider.setPreferredSize(new Dimension(100, botRotationSlider.getPreferredSize().height));
        botRotationSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        genomTB.add(botRotationSlider);
        genomTB.add(botRotationLabel);
        botSideSensorSlider.setMajorTickSpacing(45);
        botSideSensorSlider.setMinorTickSpacing(15);
        botSideSensorSlider.setPaintTicks(true);
        botSideSensorSlider.setPaintLabels(true);
        botSideSensorSlider.setPreferredSize(new Dimension(100, botSideSensorSlider.getPreferredSize().height));
        botSideSensorSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        genomTB.add(botSideSensorSlider);
        genomTB.add(botSideSensorLabel);
        botSensorDistSlider.setMajorTickSpacing(5);
        botSensorDistSlider.setMinorTickSpacing(2);
        botSensorDistSlider.setPaintTicks(true);
        botSensorDistSlider.setPaintLabels(true);
        botSensorDistSlider.setPreferredSize(new Dimension(100, botSensorDistSlider.getPreferredSize().height));
        botSensorDistSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        genomTB.add(botSensorDistSlider);
        genomTB.add(botSensorDistLabel);
        botTraceForceSlider.setMajorTickSpacing(50);
        botTraceForceSlider.setMinorTickSpacing(25);
        botTraceForceSlider.setPaintTicks(true);
        botTraceForceSlider.setPaintLabels(true);
        botTraceForceSlider.setPreferredSize(new Dimension(100, botTraceForceSlider.getPreferredSize().height));
        botTraceForceSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        genomTB.add(botTraceForceSlider);
        genomTB.add(botTraceForceLabel);
        genomTB.add(botAvoidOverTrace);
        botOverForceSlider.setMajorTickSpacing(250);
        botOverForceSlider.setMinorTickSpacing(100);
        botOverForceSlider.setPaintTicks(true);
        botOverForceSlider.setPaintLabels(true);
        botOverForceSlider.setPreferredSize(new Dimension(100, botOverForceSlider.getPreferredSize().height));
        botOverForceSlider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        genomTB.add(botOverForceSlider);
        genomTB.add(botOverForceLabel);
        toolbar.add(genomTB);

        this.pack();
        this.setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);

        mapSizeSlider.addChangeListener(e -> {
            int ws = mapSizeSlider.getValue();
            if (ws == 0) ws = 1;
            mapSizePercentLabel.setText(wmspText + ws);
            guiCallback.setWorldScale(ws);
        });

/*
        drawstepSlider.addChangeListener(e -> {
            int ds = drawstepSlider.getValue();
            if (ds == 0) ds = 1;
            guiCallback.drawStepChanged(ds);
        });
*/
        mapButton.addActionListener(e -> guiCallback.mapGenerationStarted(canvas.getWidth(), canvas.getHeight()));

//        sealevelSlider.addChangeListener(event -> guiCallback.seaLevelChanged(sealevelSlider.getValue()));

        startButton.addActionListener(e -> {
            boolean started = guiCallback.startedOrStopped();
            mapSizeSlider.setEnabled(!started);
            perlinSlider.setEnabled(!started);
            mapButton.setEnabled(!started);
        });
/*
        ActionListener mapRadioListener = e -> {
            String action = e.getActionCommand();
            Integer mode = VIEW_MODE_MAP.get(action);
            if (mode != null) {
                guiCallback.viewModeMapChanged(mode);
            }
        };

        for (AbstractButton radioButton : mapRadioButtons) {
            radioButton.addActionListener(mapRadioListener);
        }
*/
        ActionListener botRadioListener = e -> {
            String action = e.getActionCommand();
            Integer mode = VIEW_MODE_MAP_BOT.get(action);
            if (mode != null) {
                guiCallback.viewModeBotChanged(mode);
            }
        };

        for (AbstractButton radioButton : botRadioButtons) {
            radioButton.addActionListener(botRadioListener);
        }

        // world parameters
        lrMirrorCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                guiCallback.setLRMirror(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        tbMirrorCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                guiCallback.setTBMirror(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        botCountSlider.addChangeListener(e -> {
            int ws = botCountSlider.getValue();
            if (ws == 0) ws = 1;
            botCountLabel.setText(ws+ " ботов");
            guiCallback.setBotCount(ws);
        });
        traceDegradeSlider.addChangeListener(e -> {
            int ws = traceDegradeSlider.getValue();
            if (ws == 0) ws = 1;
            traceDegradeLabel.setText("Затухание следа:"+ws);
            guiCallback.setTraceDegrade(ws);
        });

        // bot parameters
        botMovementSlider.addChangeListener(e -> {
            int ws = botMovementSlider.getValue();
            if (ws == 0) ws = 1;
            botMovementLabel.setText("Движение:"+ws);
            guiCallback.setBotMovement(ws);
        });
        botRotationSlider.addChangeListener(e -> {
            int ws = botRotationSlider.getValue();
            if (ws == 0) ws = 1;
            botRotationLabel.setText("Поворот:"+ws);
            guiCallback.setBotRotation(ws);
        });
        botSideSensorSlider.addChangeListener(e -> {
            int ws = botSideSensorSlider.getValue();
            if (ws == 0) ws = 1;
            botSideSensorLabel.setText("Угол сенсора:"+ws);
            guiCallback.setBotSensorAngle(ws);
        });
        botSensorDistSlider.addChangeListener(e -> {
            int ws = botSensorDistSlider.getValue();
            if (ws == 0) ws = 1;
            botSensorDistLabel.setText("Дальность сенсора:"+ws);
            guiCallback.setBotSensorDist(ws);
        });
        botTraceForceSlider.addChangeListener(e -> {
            int ws = botTraceForceSlider.getValue();
            if (ws == 0) ws = 1;
            botTraceForceLabel.setText("Сила следа:"+ws);
            guiCallback.setBotTraceForce(ws);
        });
        botAvoidOverTrace.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                guiCallback.setBotAvoid_OverTrace(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        botOverForceSlider.addChangeListener(e -> {
            int ws = botOverForceSlider.getValue();
//            if (ws == 0) ws = 1;
            botOverForceLabel.setText("Уровень суперследа:"+ws);
            guiCallback.setBotOverTrace(ws);
        });
    }
}
