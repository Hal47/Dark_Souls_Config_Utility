/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dscfgutil.view;

import dscfgutil.FileIO.DSFixFileController;

import static dscfgutil.DSCfgUtilConstants.*;
import static dscfgutil.FileIO.DSFixFileController.launchProgram;
import static dscfgutil.FileIO.DSFixFileController.processIsRunning;
import dscfgutil.FileIO.WinRegistry;
import dscfgutil.configs.DSFConfiguration;
import dscfgutil.configs.DSFKeybindsConfiguration;
import dscfgutil.configs.DSPWConfiguration;
import dscfgutil.configs.DsMod;
import dscfgutil.configs.DsTextureMod;
import dscfgutil.dialog.AlertDialog;
import dscfgutil.dialog.ContinueDialog;
import dscfgutil.dialog.CopyableMsgDialog;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Sean Pesce
 */
public class DSCfgMainUI {

	//STATIC VARIABLES:
	public static boolean writeLogToFile = false;

    //INSTANCE VARIABLES:
    private int dsVersion = DS_VER_ENUM_UNKNOWN; // Current version of Dark Souls (Latest, Beta, Debug, or Unknown)
    private int dsfStatus; //Installed status of DSFix (Installed = 0, Not Installed = 1, Unknown = 2)
    private int dsmStatus; //Installed status of DSMouseFix (Installed, Not Installed, or Unknown)
    private int dspwStatus; //Installed status of DS PvP Watchdog (Installed, Not Installed, or Unknown)
    DSFConfiguration config; //Config object for saving/loading/editing DSFix configs
    DSFKeybindsConfiguration dsfKeybinds; //Config object for saving/loading/editing DSFix keybinds
    DSPWConfiguration dspwConfig; //Config object for saving/loading/editing DS PvP Watchdog settings
    File dataFolder; //Directory of DATA folder in Dark Souls install directory, if it exists
    public static boolean showConsoleBar = true; //Indicates if the console bar is visible
    public static boolean showConsoleWindow = false;//Indicates if the console window is open
    public static String consoleLog = "";
    DSFixFileController fileController;
    CopyableMsgDialog consoleWindow;

    //UI COMPONENTS:
    //Primary window builders
    Stage primaryStage;
    Scene primaryScene;
    public VBox primaryVBox;
    public Group primaryGroup; // Used for floating Nodes
    public static double defaultWindowWidth = 0.0;
    public static double defaultWindowHeight = 0.0;
    public static double userWindowWidth = 0.0;
    public static double userWindowHeight = 0.0;
    public static double defaultWindowXPos = 0.0;
    public static double defaultWindowYPos = 0.0;
    public static double windowXOffset = 0.0;
    public static double windowYOffset = 0.0;
    //
    //File toolbar
    MenuBar fileToolbar;
    Menu fileMenu;
    //////File options
        Menu loadMenu;
            MenuItem loadDSFCfg;
            MenuItem loadDSFKeybinds;
        Menu exportMenu;
            MenuItem exportDSFIni;
            MenuItem exportDSFKeybindsIni;
            MenuItem exportDSF;
        MenuItem openProgramDir;
        MenuItem exitProgram;
    //////Dark Souls options
    Menu dsMenu;
        MenuItem launchDS;
        MenuItem configureDS;
        MenuItem openDataFolder;
        Menu dsVersionMenu;
            MenuItem checkDSVersion;
            Menu changeDSVersion;
                MenuItem latestDSVersion;
                MenuItem betaDSVersion;
                MenuItem debugDSVersion;
    //
    ///////DSFix options
    Menu dsfMenu;
        MenuItem applyConfig;
        MenuItem applyDSFKeybinds;
        MenuItem installDSF;
        MenuItem uninstallDSF;
        MenuItem recheckDSF;
    //
    ///////DSMFix options
    Menu dsmMenu;
        MenuItem installDSM;
        MenuItem uninstallDSM;
        MenuItem configureDSM;
    //
    ///////DS PvP Watchdog options
    Menu dspwMenu;
        MenuItem applyDSPWConfig;
        MenuItem installDSPW;
        MenuItem uninstallDSPW;
        MenuItem recheckDSPW;
    //
    ///////Dark Souls Connectivity Mod options
    Menu dscmMenu;
        MenuItem launchDSCM;
        MenuItem launchDSAndCM;
        MenuItem launchDSCMnExit;
    //
    //////DSCfgUtil options
    Menu optionsMenu;
        public MenuItem toggleConsole;
    Menu helpMenu;
    //////Help options
        MenuItem updatesDSCU;
        MenuItem aboutDSCU;
        MenuItem aboutDSF;
        MenuItem aboutDSM;
        MenuItem aboutDSPW;
        MenuItem getDS;
        MenuItem getMods;
    //
    //Directory toolbar
    HBox directoryToolbar;
    Label directoryLabel;
    TextField directoryField;
    Button directoryButton;
    //
    //Tab Pane
    double scrollbarWidth;
    TabPane primaryTabPane;
    Tab graphicsTab;
    DSFGraphicsPane graphicsPane;
    Tab hudTab;
    DSFHudPane hudPane;
    Tab windowMouseTab;
    DSFWindowMousePane wmPane;
    Tab savesTab;
    DSFSavesPane savesPane;
    Tab texturesTab;
    DSFTexturesPane texPane;
    Tab otherTab;
    DSFOtherSettingsPane otherPane;
    Tab unsafeTab;
    DSFUnsafeSettingsPane unsafePane;
    Tab keysTab;
    DSFKeybindsPane keysPane;
    Tab dspwTab;
    DSPWPane dspwPane;
    Tab texModsTab;
    DSFTextureModPane texModsPane;
    //
    //Console
    public HBox consoleBar;
    Label consoleLabel;
    public TextField consoleText;
    Button consoleButton;
    public static double consoleWindowXOffset = 0.0;
    public static double consoleWindowYOffset = 0.0;
    double consoleBarHeight = 0.0;
    //Status bar
    HBox statusBar;
    Rectangle statusBarShadow;
    Label dsVersionLabeller;
    Label dsVersionLabel;
    Label dsfStatusLabeller;
    Label dsfStatusLabel;
    Label dsmStatusLabeller;
    Label dsmStatusLabel;
    Label dspwStatusLabeller;
    Label dspwStatusLabel;

    /**
     * Default (and only) constructor.
     */
    public DSCfgMainUI(Stage initStage){
    	MAIN_UI = this;
        primaryStage = initStage;
        fileController = new DSFixFileController(this);
        consoleLog = new String("");
        fileController.getVersion();
        initUI();
        printConsole(PROGRAM_LONG.toUpperCase() + " (Created by " + PROGRAM_AUTHOR + ")");
    }

    /**
     * Initializes the entire UI.
     *
     */
    private void initUI(){

        initWindow();


        //Check that DLL chaining is enabled if DSMFix is installed, or check
        // that DLL chaining is not set to dsmfix.dll if DSMfix is not installed
        if(dsmStatus == 0 && !config.dinput8dllWrapper.toString().equals(DSM_FILES[0])){
            config.dinput8dllWrapper.replace(0, config.dinput8dllWrapper.length(), DSM_FILES[0]);
            setSelectedTab(5);
            new AlertDialog(300.0, 80.0, DIALOG_TITLE_APPLY_CHANGES,
                                            DIALOG_MSG_DSM_INST_FIX_CHAINING +
                                            "\n\n" + DIALOG_MSG_APPLY_DSM_CHAIN,
                                            DIALOG_BUTTON_TEXTS[0]);
        }else if((dsmStatus == 1 || dsmStatus == 2) && config.dinput8dllWrapper.toString().equals(DSM_FILES[0])){
            config.dinput8dllWrapper.replace(0, config.dinput8dllWrapper.length(), NONE);
            setSelectedTab(5);
            new AlertDialog(300.0, 80.0, DIALOG_TITLE_APPLY_CHANGES,
                                            DIALOG_MSG_DSM_NOT_INST_FIX_CHAINING +
                                            "\n\n" + DIALOG_MSG_APPLY_NO_DLL_CHAIN,
                                            DIALOG_BUTTON_TEXTS[0]);
        }
    }

    private void initWindow(){

        //Set window size
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setX(bounds.getMaxX() / 4.0);
        primaryStage.setY(bounds.getMaxY() / 4.0);
        primaryStage.setWidth(bounds.getWidth() / 2.0);
        primaryStage.setHeight(bounds.getHeight() / 2.0);

        primaryVBox = new VBox();
        primaryGroup = new Group();
        statusBarShadow = new Rectangle();
        //primaryGroup.getChildren().add(statusBarShadow);
        primaryGroup.getChildren().addAll(primaryVBox, statusBarShadow);
        //primaryScene = new Scene(primaryVBox);
        primaryScene = new Scene(primaryGroup);


        //Set window title, icon, and stylesheet
        primaryStage.setTitle(PROGRAM_LONG + " v" + PROGRAM_VERSION);
        primaryStage.getIcons().add(new Image("file:" + IMAGE_DIRECTORY +
                                                          "\\" + PROGRAM_ICON));
        primaryScene.getStylesheets().add(getClass().getResource(
                                               CSS_DIRECTORY).toExternalForm());
        //Set up UI
        initializeFileToolbar();
        initializeDirectoryToolbar();
        initializeConsoleBar();
        initializeStatusBar();

        checkForDS();

        // Clear write files
        DsMod.clearReadmeTempFile();
        DSFixFileController.clearLogFile();

        // Initialize default class data
        DsTextureMod.initDefaultStorageDirs();

        // Load program configurable settings
        DSFixFileController.loadStartupConfig();

        // Initialize configurable class data
        DsTextureMod.initModList();

        dsfKeybinds = new DSFKeybindsConfiguration(this, true);
        config = new DSFConfiguration(this, true);
        dspwConfig = new DSPWConfiguration(this, true);
        initializeSettingsTabs();
        primaryVBox.getChildren().addAll(/*primaryGroup,*/ fileToolbar, directoryToolbar,
                                         primaryTabPane, statusBar, consoleBar);
        /*if(showConsoleBar){
        	primaryVBox.getChildren().add(consoleBar);
        }*/

        primaryStage.setScene(primaryScene);

        primaryStage.show();
        defaultWindowWidth = primaryStage.getWidth();
        defaultWindowHeight = primaryStage.getHeight();
        defaultWindowXPos = primaryStage.getX();
		defaultWindowYPos = primaryStage.getY();
        primaryStage.setX(primaryStage.getX() + DSCfgMainUI.windowXOffset);
        primaryStage.setY(primaryStage.getY() + DSCfgMainUI.windowYOffset);

        consoleBarHeight = consoleBar.getHeight();
        if(showConsoleWindow){
        	showConsoleBar = false;
			primaryVBox.getChildren().remove(consoleBar);
			toggleConsole.setText(TOGGLE_CONSOLE[1]);
			consoleWindow.show();
			consoleWindow.getAlert().setX(consoleWindow.getAlert().getX() + DSCfgMainUI.consoleWindowXOffset);
			consoleWindow.getAlert().setY(consoleWindow.getAlert().getY() + DSCfgMainUI.consoleWindowYOffset);
        }else if(showConsoleBar){
        	toggleConsole.setText(TOGGLE_CONSOLE[1]);
        }else{
        	primaryVBox.getChildren().remove(consoleBar);
        	toggleConsole.setText(TOGGLE_CONSOLE[0]);
        }

        primaryVBox.setPrefWidth(primaryScene.getWidth());
        primaryVBox.setPrefHeight(primaryScene.getHeight());

        scrollbarWidth = 16.0;
        statusBarShadow.setFill(LinearGradient.valueOf("linear-gradient(to top, rgba(0, 0, 0, 0.2), transparent)"));
        statusBarShadow.setHeight(7.0);
        statusBarShadow.setX(0.0);
        statusBarShadow.setMouseTransparent(true);
        updateStatusBarShadow();

        initializeEventHandlers();

        if(userWindowWidth > 0.0)
        	primaryStage.setWidth(userWindowWidth);
        if(userWindowHeight > 0.0)
        	primaryStage.setHeight(userWindowHeight);

        refreshUI();

        if(dsfStatus == 1){
            ContinueDialog cD = new ContinueDialog(300.0, 80.0, DIALOG_TITLE_INSTALL_DSF,
                                DIALOG_MSG_INSTALL_DSF, DIALOG_BUTTON_TEXTS[2],
                                DIALOG_BUTTON_TEXTS[3]);
            if(cD.show()){
                fileController.installDSFix();
            }
        }
    }

    private void initializeFileToolbar(){

        //File toolbar options
        fileToolbar = new MenuBar();
        fileMenu = new Menu(FILE);
        dsMenu = new Menu("Game");
        dsfMenu = new Menu(DSF);
        dsmMenu = new Menu(DSMOUSE);
        dspwMenu = new Menu(DSPW_SHORT);
        dscmMenu = new Menu(DSCM_SHORT);
        optionsMenu = new Menu(DSCU_OPTIONS);
        helpMenu = new Menu(HELP);
        fileToolbar.getMenus().addAll(fileMenu, dsMenu, dsfMenu, dsmMenu,
                                      dspwMenu, dscmMenu, optionsMenu, helpMenu);

        //File menu options
        loadMenu = new Menu(LOAD);
            loadDSFCfg = new MenuItem(LOAD_DSF_CFG);
            loadDSFKeybinds = new MenuItem(LOAD_DSF_KEYBINDS);
        loadMenu.getItems().addAll(loadDSFCfg, loadDSFKeybinds);
        exportMenu = new Menu(EXPORT_MENU);
            exportDSFIni = new MenuItem(EXPORT_DSF_INI);
            exportDSFKeybindsIni = new MenuItem(EXPORT_DSF_KEYBINDS_INI);
            exportDSF = new MenuItem(EXPORT_DSF);
        exportMenu.getItems().addAll(exportDSFIni, exportDSFKeybindsIni, exportDSF);
        openProgramDir = new MenuItem(OPEN_PROGRAM_DIR);
        exitProgram = new MenuItem(EXIT_PROGRAM);
        fileMenu.getItems().addAll(loadMenu, exportMenu, openProgramDir, exitProgram);

        //Dark Souls menu options
        launchDS = new MenuItem(LAUNCH + " " + DS);
        configureDS = new MenuItem(CONFIGURE_DS);
        openDataFolder = new MenuItem(OPEN_DATA_FOLDER);
        dsVersionMenu = new Menu(DS_VERSION_MENU);
            checkDSVersion = new MenuItem(CHECK_DS_VERSION);
            changeDSVersion = new Menu(CHANGE_DS_VERSION);
                latestDSVersion = new MenuItem(DS_VERSIONS[DS_VER_ENUM_LATEST]);
                betaDSVersion = new MenuItem("Steamworks " + DS_VERSIONS[DS_VER_ENUM_BETA]);
                debugDSVersion = new MenuItem(DS_VERSIONS[DS_VER_ENUM_DEBUG] + " build");
            changeDSVersion.getItems().addAll(latestDSVersion, betaDSVersion, debugDSVersion);
        dsVersionMenu.getItems().addAll(checkDSVersion, changeDSVersion);
        dsMenu.getItems().addAll(launchDS, openDataFolder, dsVersionMenu); //@TODO: add configureDS if you want to add support for configuring in-game settings

        //DSFix menu options
        applyConfig = new MenuItem(APPLY_CONFIG);
        applyDSFKeybinds = new MenuItem(APPLY_DSF_KEYBINDS);
        installDSF = new MenuItem(INSTALL_DSF);
        uninstallDSF = new MenuItem(UNINSTALL_DSF);
        recheckDSF = new MenuItem(RECHECK_DSF);
        dsfMenu.getItems().addAll(applyConfig, applyDSFKeybinds, installDSF,
                                                uninstallDSF, recheckDSF);

        //DSMfix menu options
        installDSM = new MenuItem(INSTALL_DSM);
        uninstallDSM = new MenuItem(UNINSTALL_DSM);
        configureDSM = new MenuItem(CONFIGURE_DSM);
        dsmMenu.getItems().addAll(installDSM, uninstallDSM, configureDSM);

        //DS PvP Watchdog menu options
        applyDSPWConfig = new MenuItem(APPLY_DSPW_CONFIG);
        installDSPW = new MenuItem(INSTALL_DSPW);
        uninstallDSPW = new MenuItem(UNINSTALL_DSPW);
        recheckDSPW = new MenuItem(RECHECK_DSPW);
        dspwMenu.getItems().addAll(applyDSPWConfig, installDSPW, uninstallDSPW,
                                    recheckDSPW);

        //Dark Souls Connectivity Mod options
        launchDSCM = new MenuItem(LAUNCH_DSCM);
        launchDSAndCM = new MenuItem(LAUNCH_DS_AND_CM);
        launchDSCMnExit = new MenuItem("Launch DSCM & exit");
        dscmMenu.getItems().addAll(launchDSCM, launchDSAndCM, launchDSCMnExit);

        //DSCfgUtil Options menu
        toggleConsole = new MenuItem(TOGGLE_CONSOLE[0]);
        optionsMenu.getItems().add(toggleConsole);

        //Help menu options
        updatesDSCU = new MenuItem(CHECK_DSCU_UPDATES);
        aboutDSCU = new MenuItem(ABOUT_DSCU);
        aboutDSF = new MenuItem(ABOUT_DSF);
        aboutDSM = new MenuItem(ABOUT_DSM);
        aboutDSPW = new MenuItem(ABOUT_DSPW);
        getDS = new MenuItem(GET_DS);
        getMods = new MenuItem(GET_MODS);
        helpMenu.getItems().addAll(updatesDSCU, aboutDSCU, aboutDSF, aboutDSM, aboutDSPW, getDS, getMods);
    }

    private void initializeDirectoryToolbar(){

        //Initialize
        directoryToolbar = new HBox();
        directoryLabel = new Label("   " + DS + " " + DIRECTORY + ":  ");
        directoryLabel.setTooltip(new Tooltip(DIRECTORY_DESC));
        directoryField = new TextField();
        directoryField.setEditable(false);
        directoryField.setTooltip(new Tooltip(DIRECTORY_DESC));
        directoryButton = new Button("...");
        directoryButton.setTooltip(new Tooltip(CHOOSE_DIRECTORY));


        //Stylize
        directoryToolbar.getStyleClass().add("gray_background");
        directoryLabel.getStyleClass().add("translate_y_4");
        directoryLabel.setPrefWidth(127.0);
        directoryField.getStyleClass().add("text_field");
        directoryField.setPrefWidth(primaryStage.getWidth() - 164.0);

        directoryToolbar.getChildren().addAll(directoryLabel, directoryField,
                                              directoryButton);

    }

    private void initializeSettingsTabs(){

        primaryTabPane = new TabPane();
        graphicsTab = new Tab(GRAPHICS);
        hudTab = new Tab(HUD);
        windowMouseTab = new Tab(WINDOW_MOUSE);
        savesTab = new Tab(SAVE_BACKUP);
        texturesTab = new Tab(TEXTURES);
        otherTab = new Tab(OTHER_OPS);
        unsafeTab = new Tab(UNSAFE_OPS);
        keysTab = new Tab(KEY_BINDINGS);
        dspwTab = new Tab(DSPW_SHORT);
        texModsTab = new Tab(TEXTURE_MOD + "s");


        //Populate tabs
        graphicsPane = new DSFGraphicsPane(this);
        graphicsPane.setPrefHeight(Integer.MAX_VALUE);
        graphicsTab.setContent(graphicsPane);
        hudPane = new DSFHudPane(this);
        hudPane.setPrefHeight(Integer.MAX_VALUE);
        hudTab.setContent(hudPane);
        wmPane = new DSFWindowMousePane(this);
        wmPane.setPrefHeight(Integer.MAX_VALUE);
        windowMouseTab.setContent(wmPane);
        savesPane = new DSFSavesPane(this);
        savesPane.setPrefHeight(Integer.MAX_VALUE);
        savesTab.setContent(savesPane);
        texPane = new DSFTexturesPane(this);
        texPane.setPrefHeight(Integer.MAX_VALUE);
        texturesTab.setContent(texPane);
        otherPane = new DSFOtherSettingsPane(this);
        otherPane.setPrefHeight(Integer.MAX_VALUE);
        otherTab.setContent(otherPane);
        unsafePane = new DSFUnsafeSettingsPane(this);
        unsafePane.setPrefHeight(Integer.MAX_VALUE);
        unsafeTab.setContent(unsafePane);
        keysPane = new DSFKeybindsPane(this);
        keysPane.setPrefHeight(Integer.MAX_VALUE);
        keysTab.setContent(keysPane);
        dspwPane = new DSPWPane(this);
        dspwPane.setPrefHeight(Integer.MAX_VALUE);
        dspwTab.setContent(dspwPane);
        texModsPane = new DSFTextureModPane(this);
        texModsPane.setPrefHeight(Integer.MAX_VALUE);
        texModsTab.setContent(texModsPane);

        primaryTabPane.getTabs().addAll(graphicsTab, hudTab, windowMouseTab,
                                        savesTab, texturesTab, otherTab,
                                        unsafeTab, keysTab, dspwTab, texModsTab);

        for(Tab tab : primaryTabPane.getTabs()){
            tab.setClosable(false);
            tab.setTooltip(new Tooltip(DSF +  " " + tab.getText() + " " + OPTIONS));
        }
        keysTab.setTooltip(new Tooltip(KEY_BINDINGS));
        dspwTab.setTooltip(new Tooltip(DSPW_SHORT + " " + OPTIONS));
        texModsTab.setTooltip(new Tooltip(TEX_MOD_TT));
    }

    private void initializeConsoleBar(){

        //Initialize
        consoleBar = new HBox();
        consoleLabel = new Label("    " + CONSOLE + ":  ");
        consoleText = new TextField();
        consoleText.setEditable(false);
        consoleButton = new Button();

        //Stylize
        consoleBar.getStyleClass().add("gray_background");
        consoleLabel.getStyleClass().add("translate_y_4");
        consoleLabel.setTooltip(new Tooltip(CONSOLE_HOVER));
        consoleText.getStyleClass().add("console_text");
        consoleText.setTooltip(new Tooltip(CONSOLE_HOVER));
        consoleText.setAlignment(Pos.CENTER);
        consoleText.setPrefWidth(primaryStage.getWidth() - 115.0);
        consoleButton.setGraphic(new ImageView("file:" + IMAGE_DIRECTORY + "\\"+
                                            CONSOLE_POPOUT_ICON));
        consoleButton.setTooltip(new Tooltip(CONSOLE_POPOUT_HOVER));
        consoleBar.getChildren().addAll(consoleLabel, consoleText, consoleButton);

        consoleWindow = new CopyableMsgDialog(500.0, 200.0,
			                CONSOLE.toUpperCase(), consoleLog,
			                DIALOG_BUTTON_TEXTS[5]);

        MAIN_UI.printConsole("[" + new java.util.Date() + "] Initializing");
    }

    private void initializeStatusBar(){


        //Initialize
        statusBar = new HBox();
        statusBar.setMinHeight(24.0);
        dsVersionLabeller = new Label("  " + GAME_VERSION + ": ");
        dsVersionLabeller.setTooltip(new Tooltip(DS_VERSION_DESC));
        dsVersionLabel = new Label();
        dsVersionLabel.setTextOverrun(OverrunStyle.CLIP);
        dsVersionLabel.setTooltip(new Tooltip(DS_VERSION_DESC));
        dsfStatusLabeller = new Label("  " + DSF + " " + STATUS + ": ");
        dsfStatusLabeller.setTooltip(new Tooltip(DSF_STATUS_DESC));
        dsfStatusLabel = new Label();
        dsfStatusLabel.setTextOverrun(OverrunStyle.CLIP);
        dsfStatusLabel.setTooltip(new Tooltip(DSF_STATUS_DESC));
        dsmStatusLabeller = new Label("  " + DSMOUSE + " " + STATUS + ": ");
        dsmStatusLabeller.setTooltip(new Tooltip(DSM_STATUS_DESC));
        dsmStatusLabel = new Label();
        dsmStatusLabel.setTextOverrun(OverrunStyle.CLIP);
        dsmStatusLabel.setTooltip(new Tooltip(DSM_STATUS_DESC));
        dspwStatusLabeller = new Label("  " + DSPW_SHORT + " " + STATUS + ": ");
        dspwStatusLabeller.setTooltip(new Tooltip(DSPW_STATUS_DESC));
        dspwStatusLabel = new Label();
        dspwStatusLabel.setTextOverrun(OverrunStyle.CLIP);
        dspwStatusLabel.setTooltip(new Tooltip(DSPW_STATUS_DESC));

        //Stylize
        statusBar.getStyleClass().add("light_gray_background_border");
        statusBar.setStyle("-fx-border-thickness: 0;");
        statusBar.setFillHeight(true);
        //statusBar.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.3), 10.0, 0.0, 0.0, -5.0));
        statusBar.setAlignment(Pos.TOP_CENTER);
        dsVersionLabeller.getStyleClass().add("translate_y_4");
        dsVersionLabeller.setPrefWidth(100.0);
        dsVersionLabeller.setAlignment(Pos.CENTER_RIGHT);
        dsVersionLabel.setPrefWidth(80.0);
        //dsVersionLabel.setAlignment(Pos.CENTER);
        dsfStatusLabeller.getStyleClass().add("translate_y_4");
        dsfStatusLabeller.setPrefWidth(80.0);
        //dsfStatusLabeller.setPrefWidth(primaryStage.getWidth() - 487.0);
        dsfStatusLabeller.setAlignment(Pos.CENTER_RIGHT);
        dsfStatusLabel.setPrefWidth(80.0);
        //dsfStatusLabel.setAlignment(Pos.CENTER);
        dsmStatusLabeller.getStyleClass().add("translate_y_4");
        dsmStatusLabeller.setPrefWidth(100.0);
        //dsmStatusLabeller.setAlignment(Pos.CENTER);
        dsmStatusLabel.setPrefWidth(80.0);
        //dsmStatusLabel.setAlignment(Pos.CENTER);
        dspwStatusLabeller.getStyleClass().add("translate_y_4");
        dspwStatusLabeller.setPrefWidth(128.0);
        //dspwStatusLabeller.setAlignment(Pos.CENTER);
        dspwStatusLabel.setPrefWidth(80.0);
        //dspwStatusLabel.setAlignment(Pos.CENTER);

        statusBar.getChildren().addAll(dsVersionLabeller, dsVersionLabel,
                                       dsfStatusLabeller, dsfStatusLabel,
                                       dsmStatusLabeller, dsmStatusLabel,
                                       dspwStatusLabeller, dspwStatusLabel);
    }

    private void initializeEventHandlers(){

        //FILE TOOLBAR
        //File menu
        //
        loadDSFCfg.setOnAction(e -> {
           fileController.loadDSFConfig();
           if(getCurrentTab() >= 7){
               primaryTabPane.getSelectionModel().select(0);
           }
           refreshUI();
        });
        //
        loadDSFKeybinds.setOnAction(e -> {
           fileController.loadDSFKeybinds();
           primaryTabPane.getSelectionModel().select(7);
           refreshUI();
        });
        //
        exportDSF.setOnAction(e -> {
            if(!invalidDSFInputsExist()){
                fileController.exportDSF();
            }else{
                new AlertDialog(300.0, 80.0, DIALOG_TITLE_CFG_NOT_APPLIED,
                                                  COULDNT_EXPORT_INVALID_INPUT_ERR,
                                                  DIALOG_BUTTON_TEXTS[0]);
            }
        });
        //
        exportDSFIni.setOnAction(e -> {
            if(!invalidDSFInputsExist()){
                fileController.exportDSFIniFile();
            }else{
                new AlertDialog(300.0, 80.0, DIALOG_TITLE_CFG_NOT_APPLIED,
                                                  COULDNT_EXPORT_INVALID_INPUT_ERR,
                                                  DIALOG_BUTTON_TEXTS[0]);
            }
        });
        //
        exportDSFKeybindsIni.setOnAction(e -> {
            fileController.exportDSFKeybindsIniFile();
        });
        //
        openProgramDir.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir")));
            } catch (IOException ex) {
                //Logger.getLogger(DSFixFileController.class.getName()).log(Level.SEVERE, null, ex);
                printConsole(FAILED_OPEN_FOLDER_ERR + ": " + System.getProperty("user.dir"));
            }
        });
        //
        exitProgram.setOnAction(e -> {
        	DsMod.clearReadmeTempFile();
        	if(consoleBar != null){
            	MAIN_UI.printConsole("[" + new java.util.Date() + "] Exiting");
            }
            if(showConsoleWindow && consoleWindow != null){
                consoleWindow.close();
            }
            System.exit(0);
        });
        //
        //Dark Souls Menu
        launchDS.setOnAction(e -> {
            try {
                URI dsURI = new URI(LAUNCH_DS);
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(dsURI);
                }
            } catch (URISyntaxException | IOException ex) {
                printConsole(LAUNCH_DS_FAILED + ", " + ex.toString());
            }
        });
        //
        openDataFolder.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(dataFolder);
            } catch (IOException ex) {
                //Logger.getLogger(DSFixFileController.class.getName()).log(Level.SEVERE, null, ex);
                printConsole(FAILED_OPEN_FOLDER_ERR + ": " + dataFolder.getPath());
            }
        });
        //
        checkDSVersion.setOnAction(e -> {
            checkDSVersion();
            enableAndDisableElements();
        });
        //
        latestDSVersion.setOnAction(e -> {
            disableElementsForSleeping();
            fileController.changeDSVersion(DS_VER_ENUM_LATEST);
            enableElementsForSleeping();
            checkDSVersion();
            enableAndDisableElements();
        });
        //
        betaDSVersion.setOnAction(e -> {
            disableElementsForSleeping();
            fileController.changeDSVersion(DS_VER_ENUM_BETA);
            enableElementsForSleeping();
            checkDSVersion();
            enableAndDisableElements();
        });
        //
        debugDSVersion.setOnAction(e -> {
            disableElementsForSleeping();
            fileController.changeDSVersion(DS_VER_ENUM_DEBUG);
            enableElementsForSleeping();
            checkDSVersion();
            enableAndDisableElements();
        });
        //
        //DSFix menu
        applyConfig.setOnAction(e -> {
            applyDSFConfig();
        });
        //
        applyDSFKeybinds.setOnAction(e -> {
            keysPane.applySettingsButton.setDisable(true);
            dsfKeybinds.writeSettingsToIniFile(dataFolder.getPath() + "\\" + DSF_FILES[2]);
            keysPane.applySettingsButton.setDisable(false);
        });
        //
        installDSF.setOnAction(e -> {
            if(dsfStatus == 0 || dsfStatus == 2){
                ContinueDialog cD = new ContinueDialog(300.0, 80.0,
                                        DIALOG_TITLE_RESET, DIALOG_MSG_REINSTALL,
                                        DIALOG_BUTTON_TEXTS[2], DIALOG_BUTTON_TEXTS[1]);
                if(cD.show()){
                    fileController.installDSFix();
                }
            }else{
                fileController.installDSFix();
            }
        });
        //
        uninstallDSF.setOnAction(e -> {
            ContinueDialog cD = new ContinueDialog(300.0, 80.0,
                                        DIALOG_TITLE_UNINSTALL, DIALOG_MSG_UNINSTALL,
                                        DIALOG_BUTTON_TEXTS[2], DIALOG_BUTTON_TEXTS[1]);
            if(cD.show()){
                fileController.uninstallDSFix();
            }
        });
        //
        recheckDSF.setOnAction(e -> {
            checkForDSFix();
            if(dsfStatus == 0){
                printConsole(DSF_FOUND);
            }else if(dsfStatus == 1){
                printConsole(DSF_NOT_FOUND);
            }else{
                printConsole(DSF_PARTIALLY_FOUND);
            }
        });
        //
        //DSMFix menu
        installDSM.setOnAction(e -> {
           if(dsmStatus == 0 || dsmStatus == 2){
                ContinueDialog cD = new ContinueDialog(300.0, 80.0,
                                        DIALOG_TITLE_RESET, DIALOG_MSG_DSM_REINSTALL,
                                        DIALOG_BUTTON_TEXTS[2], DIALOG_BUTTON_TEXTS[1]);
                if(cD.show()){
                    fileController.installDSMFix();
                    enableAndDisableElements();
                }
            }else{
                fileController.installDSMFix();
                enableAndDisableElements();
            }
        });
        //
        uninstallDSM.setOnAction(e -> {
            ContinueDialog cD = new ContinueDialog(300.0, 80.0,
                                        DIALOG_TITLE_DSM_UNINSTALL, DIALOG_MSG_DSM_UNINSTALL,
                                        DIALOG_BUTTON_TEXTS[2], DIALOG_BUTTON_TEXTS[1]);
            if(cD.show()){
                fileController.uninstallDSMFix();
                enableAndDisableElements();
            }
        });
        //
        configureDSM.setOnAction(e -> {
            try {
                printConsole(LAUNCHING_DSM);
                String cfgDSM = dataFolder.toPath() + "\\" + DSM_FILES[2];
                File cfgDSMFile = new File(cfgDSM);
                Runtime.getRuntime().exec(cfgDSMFile.getPath(), null, dataFolder);
                disableElementsForSleeping();
                for(int i = DSM_DELAY; i >= 0; i--){
                    try {
                        Thread.sleep(1000); //Sleep 0.5 seconds
                        try{
                            if(processIsRunning(DSM_FILES[2])){
                                //Mouse Fix UI process found
                                i = -1; //End counter and loop
                                Thread.sleep(500);
                            }else{
                                //Mouse Fix UI process not found
                                if(i == 0){
                                    printConsole(END_TRYING_DSM_PROCESS);
                                    Thread.sleep(1000);
                                }
                            }
                        }catch(SecurityException | IOException ex){
                            //Error when attempting to check current process list
                            i = -1; //End counter and loop
                            printConsole(COULDNT_CHECK_PROCESS_DSM);
                            for(int j = DSM_DELAY; j >= 0; j--){
                                Thread.sleep(1000); //Sleep 1 second
                                printConsole(WAITING[0] + j + WAITING[1]);
                            }
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DSCfgMainUI.class.getName()).log(Level.SEVERE, null, ex);
                        printConsole(SLEEP_INTERRUPTED);
                    }

                }
                enableElementsForSleeping();
            } catch (IOException ex) {
                printConsole(CONFIGURE_DSM_FAILED + ": " + ex.toString());
				enableElementsForSleeping();
            }
        });
        //
        //PvP Watchdog menu
        applyDSPWConfig.setOnAction(e -> {
            applyDSPWConfig();
        });
        //
        installDSPW.setOnAction(e -> {
            if(dspwStatus == 0 || dspwStatus == 2){
                ContinueDialog cD = new ContinueDialog(300.0, 80.0,
                                        DIALOG_TITLE_RESET, DIALOG_MSG_REINSTALL_DSPW,
                                        DIALOG_BUTTON_TEXTS[2], DIALOG_BUTTON_TEXTS[1]);
                if(cD.show()){
                    fileController.installDSPW();
                }
            }else{
                fileController.installDSPW();
            }
        });
        //
        uninstallDSPW.setOnAction(e -> {
            ContinueDialog cD = new ContinueDialog(300.0, 80.0,
                                        DIALOG_TITLE_UNINSTALL_DSPW, DIALOG_MSG_UNINSTALL_DSPW,
                                        DIALOG_BUTTON_TEXTS[2], DIALOG_BUTTON_TEXTS[1]);
            if(cD.show()){
                fileController.uninstallDSPW();
            }
        });
        //
        recheckDSPW.setOnAction(e -> {
            checkForDSPW();
        });
        //
        //Dark Souls Connectivity Mod menu
        launchDSAndCM.setOnAction(e -> {
            try {
                printConsole(LAUNCHING_DS);
                URI programURI = new URI(LAUNCH_DS);
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(programURI);
                }
            } catch (URISyntaxException | IOException ex) {
                printConsole(LAUNCH_DS_FAILED + ", " + ex.toString());
                return;
            }

            printConsole(CHECK_FOR_DS_PROCESS);

            disableElementsForSleeping();
            for(int i = DSCM_DELAY; i >= 0; i--){
                try {
                    Thread.sleep(1000); //Sleep 1 second
                    try{
                        if(processIsRunning(DS_EXE)){
                            //Dark Souls process found
                            i = -1; //End counter and loop
                            printConsole(FOUND_DS_PROCESS);
                            Thread.sleep(2000);
                        }else{
                            //Dark Souls process not found
                            if(i != 0){
                                printConsole(COULDNT_FIND_DS_PROCESS
                                        + RECHECKING[0] + i + RECHECKING[1]);
                            }else{
                                printConsole(END_TRYING_DS_PROCESS);
                                Thread.sleep(2000);
                            }
                        }
                    }catch(SecurityException | IOException ex){
                        //Error when attempting to check current process list
                        i = -1; //End counter and loop
                        printConsole(COULDNT_CHECK_PROCESSES);
                        for(int j = DSCM_DELAY; j >= 0; j--){
                            Thread.sleep(1000); //Sleep 1 second
                            printConsole(WAITING[0] + j + WAITING[1]);
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(DSCfgMainUI.class.getName()).log(Level.SEVERE, null, ex);
                    printConsole(SLEEP_INTERRUPTED);
                }

            }
            enableElementsForSleeping();

            try {
                printConsole(LAUNCHING_DSCM);
                launchProgram(FILES_DIR + DSCM_FOLDER + "\\" + DSCM_FILES[1]);
            } catch (IOException ex) {
                printConsole(LAUNCH_DSCM_FAILED + ": " + ex.toString());
            }
        });

        launchDSCM.setOnAction(e -> {
            try {
                printConsole(LAUNCHING_DSCM);
                launchProgram(FILES_DIR + DSCM_FOLDER + "\\" + DSCM_FILES[1]);
            } catch (IOException ex) {
                printConsole(LAUNCH_DSCM_FAILED + ": " + ex.toString());
            }
        });

        launchDSCMnExit.setOnAction(e -> {
            try {
                printConsole(LAUNCHING_DSCM);
                launchProgram(FILES_DIR + DSCM_FOLDER + "\\" + DSCM_FILES[1]);
                System.exit(0);
            } catch (IOException ex) {
                printConsole(LAUNCH_DSCM_FAILED + ": " + ex.toString());
            }
        });
        //
        //Options menu
        toggleConsole.setOnAction(e -> {
            if(showConsoleBar){
                primaryVBox.getChildren().remove(consoleBar);
                showConsoleBar = false;
                showConsoleWindow = false;
                toggleConsole.setText(TOGGLE_CONSOLE[0]);
            }else if(showConsoleWindow){
                showConsoleBar = false;
                showConsoleWindow = false;
                consoleWindow.close();
                toggleConsole.setText(TOGGLE_CONSOLE[0]);
            }else{
                primaryVBox.getChildren().add(consoleBar);
                showConsoleBar = true;
                showConsoleWindow = false;
                toggleConsole.setText(TOGGLE_CONSOLE[1]);
            }
            enableAndDisableElements();
        });
        //
        // Help Menu
        updatesDSCU.setOnAction(e -> {
            try{
                if(Desktop.isDesktopSupported()){
                  Desktop.getDesktop().browse(new URI(DSCU_CHECK_UPDATES_URL));
                }else{
                    printConsole(UNSUPPORTED_DESKTOP);
                }
            }catch(IOException | URISyntaxException dscuUpdatesEx){
                printConsole(FAILED_TO_OPEN_URL + DSCU_CHECK_UPDATES_URL);
            }
        });
        aboutDSCU.setOnAction(e -> {
            try{
                if(Desktop.isDesktopSupported()){
                  Desktop.getDesktop().browse(new URI(DSCU_HELP_URL));
                }else{
                    printConsole(UNSUPPORTED_DESKTOP);
                }
            }catch(IOException | URISyntaxException dscuHelpEx){
                printConsole(FAILED_TO_OPEN_URL + DSCU_HELP_URL);
            }
        });
        aboutDSF.setOnAction(e -> {
            try{
                if(Desktop.isDesktopSupported()){
                  Desktop.getDesktop().browse(new URI(DSF_HELP_URL));
                }else{
                    printConsole(UNSUPPORTED_DESKTOP);
                }
            }catch(IOException | URISyntaxException dsfHelpEx){
                printConsole(FAILED_TO_OPEN_URL + DSF_HELP_URL);
            }
        });
        aboutDSM.setOnAction(e -> {
            try{
                if(Desktop.isDesktopSupported()){
                  Desktop.getDesktop().browse(new URI(DSM_HELP_URL));
                }else{
                    printConsole(UNSUPPORTED_DESKTOP);
                }
            }catch(IOException | URISyntaxException dsmHelpEx){
                printConsole(FAILED_TO_OPEN_URL + DSM_HELP_URL);
            }
        });
        aboutDSPW.setOnAction(e -> {
            try{
                if(Desktop.isDesktopSupported()){
                  Desktop.getDesktop().browse(new URI(DSPW_HELP_URL));
                }else{
                    printConsole(UNSUPPORTED_DESKTOP);
                }
            }catch(IOException | URISyntaxException dspwHelpEx){
                printConsole(FAILED_TO_OPEN_URL + DSPW_HELP_URL);
            }
        });
        getDS.setOnAction(e -> {
            try{
                if(Desktop.isDesktopSupported()){
                  Desktop.getDesktop().browse(new URI(GET_DS_URL));
                }else{
                    printConsole(UNSUPPORTED_DESKTOP);
                }
            }catch(IOException | URISyntaxException getDSHelpEx){
                printConsole(FAILED_TO_OPEN_URL + GET_DS_URL);
            }
        });
        getMods.setOnAction(e -> {
            try{
                if(Desktop.isDesktopSupported()){
                  Desktop.getDesktop().browse(new URI(GET_MODS_URL));
                }else{
                    printConsole(UNSUPPORTED_DESKTOP);
                }
            }catch(IOException | URISyntaxException getModsHelpEx){
                printConsole(FAILED_TO_OPEN_URL + GET_MODS_URL);
            }
        });

        //Directory bar
        directoryLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(dataFolder != null && mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        try {
                            Desktop.getDesktop().open(dataFolder);
                        } catch (IOException ex) {
                            //Logger.getLogger(DSFixFileController.class.getName()).log(Level.SEVERE, null, ex);
                            printConsole(FAILED_OPEN_FOLDER_ERR + ": " + dataFolder.getPath());
                        }
                    }
                }
            }
        });
        //
        directoryField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(dataFolder != null && mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        try {
                            Desktop.getDesktop().open(dataFolder);
                        } catch (IOException ex) {
                            //Logger.getLogger(DSFixFileController.class.getName()).log(Level.SEVERE, null, ex);
                            printConsole(FAILED_OPEN_FOLDER_ERR + ": " + dataFolder.getPath());
                        }
                    }
                }
            }
        });
        //
        directoryButton.setOnAction(e -> {
            fileController.chooseDataFolder();
        });

        //Console bar
        //
        consoleButton.setOnAction(e -> {
            if(primaryVBox.getChildren().contains(consoleBar)){
            	primaryVBox.getChildren().remove(consoleBar);
            }

            showConsoleBar = false;
            showConsoleWindow = true;
            updateStatusBarShadow();

            consoleWindow.show();
            consoleWindow.getAlert().setX(consoleWindow.getAlert().getX() + DSCfgMainUI.consoleWindowXOffset);
			consoleWindow.getAlert().setY(consoleWindow.getAlert().getY() + DSCfgMainUI.consoleWindowYOffset);

            updateStatusBarShadow();
        });

        //When exiting program:
        primaryStage.setOnCloseRequest(e -> {
        	DsMod.clearReadmeTempFile();
        	if(consoleBar != null){
        		MAIN_UI.printConsole("[" + new java.util.Date() + "] Exiting");
            }
            if(showConsoleWindow && consoleWindow != null){
                consoleWindow.close();
            }
            System.exit(0);
        });

        //When changing tabs:
        primaryTabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldTab, Number newTab) {
                    refreshUI();
                }
            });

        //All resizings based on window size will happen here
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
                    directoryField.setPrefWidth(newWidth.doubleValue() - 164.0);
                    consoleText.setPrefWidth(newWidth.doubleValue() - 115.0);
                }
            });

        primaryScene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
            	primaryVBox.setPrefWidth(primaryScene.getWidth());
            	//directoryField.setPrefWidth(primaryVBox.getWidth() - directoryLabel.getWidth() - directoryButton.getWidth());
                //consoleText.setPrefWidth(primaryVBox.getWidth() - consoleLabel.getWidth() - consoleButton.getWidth());
            	updateStatusBarShadow();
            }
        });

        primaryScene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldHeight, Number newHeight) {
                primaryVBox.setPrefHeight(primaryScene.getHeight());
                updateStatusBarShadow();
            }
        });


        directoryField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue,
                                    String oldText, String newText) {
                    //Check for valid Dark Souls directory
                    File tempDirectoryChecker = new File(newText);
                    if(!tempDirectoryChecker.exists()){
                        directoryField.pseudoClassStateChanged(INVALID_INPUT, true);
                    }else{
                        directoryField.pseudoClassStateChanged(INVALID_INPUT, false);
                    }

                }
            });
    }

    private void checkForDS(){
        printConsole(CHECKING_FOR_DS);

        //Check for DARKSOULS.exe in current working directory
        boolean checkReg = true;
        String workingDir = Paths.get(".").toAbsolutePath().toString();
        if(workingDir.lastIndexOf(DATA_FOLDER) != -1){
            workingDir = workingDir.substring(0, workingDir.lastIndexOf(DATA_FOLDER) + DATA_FOLDER.length());
            dataFolder = new File(workingDir + "\\" + DS_EXE);
            if(dataFolder.exists()){
                checkReg = false;
                dataFolder = new File(workingDir);
            }else{
                dataFolder = null;
            }
        }

        //Check for Steam install in registry, and use config.vdf to find games library directory
        File steamFolder;
        String line;
        printConsole(CHECKING_STEAM_LIBRARY);
        for(int i = 0; i < STEAM_INSTALL_REG_KEYS.length && checkReg; i++){
            try{
                String installDir = WinRegistry.readString(
                            WinRegistry.HKEY_LOCAL_MACHINE, //HKEY
                            STEAM_INSTALL_REG_KEYS[i], //Key
                            DS_INSTALL_REG_VALUE_NAME[2]); //ValueName
                steamFolder = new File(installDir + STEAM_CFG_FILE);
                if(steamFolder.exists()){
                    try {
                        Scanner cfgReader = new Scanner(steamFolder);
                        while(cfgReader.hasNext()){
                            line = cfgReader.nextLine();
                            if(line.contains(STEAM_CFG_ENTRY)){
                                line = line.trim();
                                line = line.substring(line.indexOf(':') - 1);
                                line = line.substring(0, line.indexOf('"'));
                                line = line.replace("\\\\", "\\");
                                line += DEFAULT_STEAM_SUBDIR + DATA_FOLDER;
                                dataFolder = new File(line);
                                if(dataFolder.exists()){
                                    checkReg = false;
                                    printConsole(FOUND_DS + line);
                                    break;
                                }else{
                                    dataFolder = null;
                                }
                            }
                        }
                        cfgReader.close();
                    } catch (FileNotFoundException ex) {
                        printConsole(UNABLE_TO_READ_FILE + steamFolder.getName());
                    }
                }
            }catch(IllegalAccessException iAE){
                //User doesn't have permission to access registry, try crappy method
                printConsole(CHECK_FOR_DS_NO_REG_ACC_ERR + TRYING_DEFAULT_DIR_ERR);
                break;
            }catch(InvocationTargetException iTE){
                printConsole(CHECK_FOR_DS_ITE_ERR + TRYING_DEFAULT_DIR_ERR);
                break;
            }
        }

        //Check for Dark Souls install in registry
        if(checkReg){
            printConsole(CHECKING_FOR_DS_REG_ENTRIES);
        }
        for(int i = 0; i < DS_REGISTRY_KEY.length && checkReg; i++){
            try{
                String installDir;
                if(i == 0 || i == 1){ //Check Windows uninstall registry keys
                    installDir = WinRegistry.readString(
                            WinRegistry.HKEY_LOCAL_MACHINE, //HKEY
                            DS_REGISTRY_KEY[i], //Key
                            DS_INSTALL_REG_VALUE_NAME[0]); //ValueName
                }else{ //Check Dark Souls game registry keys
                    installDir = WinRegistry.readString(
                            WinRegistry.HKEY_LOCAL_MACHINE, //HKEY
                            DS_REGISTRY_KEY[i], //Key
                            DS_INSTALL_REG_VALUE_NAME[1]); //ValueName
                }
                dataFolder = new File(installDir + DATA_FOLDER);
                if(dataFolder.exists()){
                    printConsole(FOUND_DS + installDir);
                    break;
                }else{
                    dataFolder = null;
                }
            }catch(IllegalAccessException iAE){
                //User doesn't have permission to access registry, try crappy method
                printConsole(CHECK_FOR_DS_NO_REG_ACC_ERR + TRYING_DEFAULT_DIR_ERR);
                checkForDSDefaultDirectories();
                if(dataFolder != null && dataFolder.exists()){
                    break;
                }else{
                    dataFolder = null;
                }
            }catch(InvocationTargetException iTE){
                printConsole(CHECK_FOR_DS_ITE_ERR + TRYING_DEFAULT_DIR_ERR);
                checkForDSDefaultDirectories();
                if(dataFolder != null && dataFolder.exists()){
                    break;
                }else{
                    dataFolder = null;
                }
            }
        }

        checkDSVersion();

        if(dataFolder != null && dataFolder.exists()){
            checkForDSFix();
            checkForDSPW();
        }else{
            printConsole(DS_INSTALL_NOT_FOUND);
            dataFolder = null;
            setDSFStatus(2);
            setDSPWStatus(1);
        }
    }

    private void checkForDSDefaultDirectories(){
        //Get user drive letter in case it's not C:
        char drive = System.getProperty("user.dir").charAt(0);

        switch(drive){
            //Check current drive
            default:
            for(int j = 0; j < DEFAULT_STEAM_DIR.length; j++){
                dataFolder = new File(drive + ":" + DEFAULT_STEAM_DIR[j] +
                    DEFAULT_STEAM_SUBDIR + DATA_FOLDER);
                if(!dataFolder.exists()){
                    System.out.println(NOT_FOUND);
                }else{
                    break;
                }
            }
            //If user drive letter is C: or Dark Souls was not
            //  installed in the current drive, check C:
            case 'C':
                for(int j = 0; j < DEFAULT_STEAM_DIR.length; j++){
                    dataFolder = new File("C:" + DEFAULT_STEAM_DIR[j] +
                        DEFAULT_STEAM_SUBDIR + DATA_FOLDER);
                    if(!dataFolder.exists()){
                        System.out.println(NOT_FOUND);
                    }else{
                        break;
                    }
                }
                break;
        }

        if(dataFolder != null && dataFolder.exists()){
            return;
        }else{
            dataFolder = null;
            setDSFStatus(2);
        }
    }

    public void checkDSVersion(){
        if(dataFolder == null){
            setDSVersion(DS_VER_ENUM_UNKNOWN);
        }

        printConsole(DS_VERSION_CHECKING);

        File gameExecutable = null;
        if (dataFolder != null) {
        	gameExecutable = new File(dataFolder.getPath() + "\\" + DS_EXE);
        }

        if(gameExecutable != null && !gameExecutable.exists()){
            setDSVersion(DS_VER_ENUM_UNKNOWN);
            printConsole(DS_VERSION_DETECTED[DS_VER_ENUM_UNKNOWN] + DS_VERSION_DETECTED_MISSING);
        }

        long gameExecutableSize = (gameExecutable == null) ? 0L : gameExecutable.length();

        if(MAIN_UI.getDataFolder() == null){
        	setDSVersion(DS_VER_ENUM_UNKNOWN);
        	printConsole(DS_VERSION_DETECTED[DS_VER_ENUM_UNKNOWN] + " (Failed to locate DATA folder)");
        }else if(gameExecutableSize == DS_SIZES[DS_VER_ENUM_LATEST]){
            setDSVersion(DS_VER_ENUM_LATEST);
            printConsole(DS_VERSION_DETECTED[DS_VER_ENUM_LATEST]);
        }else if(gameExecutableSize == DS_SIZES[DS_VER_ENUM_BETA]){
            setDSVersion(DS_VER_ENUM_BETA);
            printConsole(DS_VERSION_DETECTED[DS_VER_ENUM_BETA]);
        }else if(gameExecutableSize == DS_SIZES[DS_VER_ENUM_DEBUG]){
            setDSVersion(DS_VER_ENUM_DEBUG);
            printConsole(DS_VERSION_DETECTED[DS_VER_ENUM_DEBUG]);
        }else{
            setDSVersion(DS_VER_ENUM_UNKNOWN);
            printConsole(DS_VERSION_DETECTED[DS_VER_ENUM_UNKNOWN] + DS_VERSION_DETECTED_UNKNOWN);
        }
    }

    public void checkForDSFix(){

        if(dataFolder != null && dataFolder.exists()){
            printConsole(CHECKING_FOR_DSF);

            //Check for DINPUT8.dll
            File dsfFile = new File("");
            int i;
            for(i = 0; i < DSF_FILES.length - 1; i++){
                if(i != 3 && i !=4){ //Ignore unnecessary files
                    dsfFile = new File(dataFolder.getPath() + "\\" + DSF_FILES[i]);
                    if(!dsfFile.exists()){
                        printConsole(DSF_FILES[i] + DSCUTIL_FILE_NOT_FOUND);
                        break;
                    }
                }
            }

            if(dsfFile.exists()){
                //DSFix is installed
                printConsole(DSF_FOUND);
                setDSFStatus(0);
            }else{
                if(i == 0){
                    //DSFix is not installed
                    printConsole(DSF_NOT_FOUND);
                    setDSFStatus(1);
                }else{
                    //DSFix is partially installed
                    printConsole(DSF_PARTIALLY_FOUND);
                    setDSFStatus(2);
                }
            }

            enableAndDisableElements();
        }else{
            setDSFStatus(2);
            checkForDS();
        }

    }

    public void checkForDSMFix(){

        printConsole(CHECKING_FOR_DSM);

        for(int i = 0; i < DSM_FILES.length; i++){
            File dsmCheck = new File(dataFolder + "\\" + DSM_FILES[i]);
            printConsole("Checking for file " + DSM_FILES[i]);
            if(!dsmCheck.exists()){
                setDSMStatus(1);
                printConsole(DSM_FILES[i] + DSCUTIL_FILE_NOT_FOUND);
                printConsole(DSM_NOT_FOUND);
                break;
            }else if(i == DSM_FILES.length-1){
                setDSMStatus(0);
                printConsole(DSM_FOUND);
            }
        }

    }

    public void checkForDSPW(){

        printConsole(CHECKING_FOR_DSPW);

        for(int i = 0; i < DSPW_FILES.length; i++){
            if(i == 2){
                //Ignore readme file
                i++;
            }
            File dspwCheck = new File(dataFolder + "\\" + DSPW_FILES[i]);
            if(!dspwCheck.exists()){
                setDSPWStatus(1);
                printConsole(DSPW_FILES[i] + DSCUTIL_FILE_NOT_FOUND);
                printConsole(DSPW_NOT_FOUND);
                break;
            }else if(i == 6){
                setDSPWStatus(0);
                printConsole(DSPW_FOUND);
            }
        }

        enableAndDisableElements();
    }

    public void refreshUI(){

        //Check for data folder
        if(dataFolder == null){
            directoryField.pseudoClassStateChanged(INVALID_INPUT, true);
            directoryField.setText(NOT_FOUND);
            directoryButton.requestFocus();
        }else{
            directoryField.setText(dataFolder.getPath());
            directoryField.pseudoClassStateChanged(INVALID_INPUT, false);
        }

        //Check console status
        if(showConsoleBar || showConsoleWindow){
            toggleConsole.setText(TOGGLE_CONSOLE[1]);
        }else{
            toggleConsole.setText(TOGGLE_CONSOLE[0]);
        }

        //Find selected pane and refresh it
        if(primaryTabPane != null){
        	switch(getCurrentTab()){
                case 0:
                    graphicsPane = new DSFGraphicsPane(this);
                    graphicsTab.setContent(graphicsPane);
                    break;
                case 1:
                    hudPane = new DSFHudPane(this);
                    hudTab.setContent(hudPane);
                    break;
                case 2:
                    wmPane = new DSFWindowMousePane(this);
                    windowMouseTab.setContent(wmPane);
                    break;
                case 3:
                    savesPane = new DSFSavesPane(this);
                    savesTab.setContent(savesPane);
                    break;
                case 4:
                    texPane = new DSFTexturesPane(this);
                    texturesTab.setContent(texPane);
                    break;
                case 5:
                    otherPane = new DSFOtherSettingsPane(this);
                    otherTab.setContent(otherPane);
                    break;
                case 6:
                    unsafePane = new DSFUnsafeSettingsPane(this);
                    unsafeTab.setContent(unsafePane);
                    break;
                case 7:
                    keysPane = new DSFKeybindsPane(this);
                    keysTab.setContent(keysPane);
                    break;
                case 8:
                    dspwPane = new DSPWPane(this);
                    dspwTab.setContent(dspwPane);
                    break;
                case 9:
                	texModsPane = new DSFTextureModPane(this);
                    texModsTab.setContent(texModsPane);
                    break;
                default:
                    break;

            }
        }

        enableAndDisableElements();
    }

    public void enableAndDisableElements(){

        //File menu
        if(config != null && dsfKeybinds != null){
            exportDSFIni.setDisable(false);
            exportDSFKeybindsIni.setDisable(false);
            exportDSF.setDisable(false);
        }else{
            exportDSFIni.setDisable(true);
            exportDSFKeybindsIni.setDisable(true);
            exportDSF.setDisable(true);
        }

        //Dark Souls menu
        if(dataFolder != null){
            openDataFolder.setDisable(false);
            dsVersionMenu.setDisable(false);
            File dsEXE = new File(dataFolder.toPath() + "\\" + DS_EXE);
            if(dsEXE.exists()){
                launchDS.setDisable(false);
                configureDS.setDisable(false);
                changeDSVersion.setDisable(false);
            }else{
                launchDS.setDisable(true);
                configureDS.setDisable(true);
                changeDSVersion.setDisable(true);
            }
            switch(dsVersion){
                case DS_VER_ENUM_LATEST:
                    latestDSVersion.setDisable(true);
                    betaDSVersion.setDisable(false);
                    debugDSVersion.setDisable(false);
                    break;
                case DS_VER_ENUM_BETA:
                    latestDSVersion.setDisable(false);
                    betaDSVersion.setDisable(true);
                    debugDSVersion.setDisable(false);
                    break;
                case DS_VER_ENUM_DEBUG:
                    latestDSVersion.setDisable(false);
                    betaDSVersion.setDisable(false);
                    debugDSVersion.setDisable(true);
                    break;
                default:
                    // Unknown game version
                    latestDSVersion.setDisable(false);
                    betaDSVersion.setDisable(false);
                    debugDSVersion.setDisable(false);
                    break;
            }
        }else{
            launchDS.setDisable(true);
            configureDS.setDisable(true);
            openDataFolder.setDisable(true);
            dsVersionMenu.setDisable(true);
        }

        //DSFix menu
        switch(dsfStatus){
            case 0:
                if(config != null){
                    applyConfig.setDisable(false);
                    if(graphicsPane != null){
                        graphicsPane.applySettingsButton.setDisable(false);
                        hudPane.applySettingsButton.setDisable(false);
                        otherPane.applySettingsButton.setDisable(false);
                        savesPane.applySettingsButton.setDisable(false);
                        texPane.applySettingsButton.setDisable(false);
                        unsafePane.applySettingsButton.setDisable(false);
                        wmPane.applySettingsButton.setDisable(false);
                        keysPane.applySettingsButton.setDisable(false);
                    }
                    applyDSFKeybinds.setDisable(false);
                }else{
                    applyConfig.setDisable(true);
                    if(graphicsPane != null){
                        graphicsPane.applySettingsButton.setDisable(true);
                        hudPane.applySettingsButton.setDisable(true);
                        otherPane.applySettingsButton.setDisable(true);
                        savesPane.applySettingsButton.setDisable(true);
                        texPane.applySettingsButton.setDisable(true);
                        unsafePane.applySettingsButton.setDisable(true);
                        wmPane.applySettingsButton.setDisable(true);
                        keysPane.applySettingsButton.setDisable(true);
                    }
                    applyDSFKeybinds.setDisable(true);
                }
                installDSF.setText(REINSTALL_DSF);
                installDSF.setDisable(false);
                uninstallDSF.setDisable(false);
                recheckDSF.setDisable(false);
                if(texModsTab != null){
                    texModsTab.setDisable(false);
                }
                break;
            case 1:
                applyConfig.setDisable(true);
                if(graphicsPane != null){
                    graphicsPane.applySettingsButton.setDisable(true);
                    hudPane.applySettingsButton.setDisable(true);
                    otherPane.applySettingsButton.setDisable(true);
                    savesPane.applySettingsButton.setDisable(true);
                    texPane.applySettingsButton.setDisable(true);
                    unsafePane.applySettingsButton.setDisable(true);
                    wmPane.applySettingsButton.setDisable(true);
                    keysPane.applySettingsButton.setDisable(true);
                }
                applyDSFKeybinds.setDisable(true);
                installDSF.setDisable(false);
                installDSF.setText(INSTALL_DSF);
                uninstallDSF.setDisable(true);
                recheckDSF.setDisable(false);
                if(texModsTab != null){
                    texModsTab.setDisable(true);
                }
                break;
            case 2:
                applyConfig.setDisable(true);
                if(graphicsPane != null){
                    graphicsPane.applySettingsButton.setDisable(true);
                    hudPane.applySettingsButton.setDisable(true);
                    otherPane.applySettingsButton.setDisable(true);
                    savesPane.applySettingsButton.setDisable(true);
                    texPane.applySettingsButton.setDisable(true);
                    unsafePane.applySettingsButton.setDisable(true);
                    wmPane.applySettingsButton.setDisable(true);
                    keysPane.applySettingsButton.setDisable(true);
                }
                applyDSFKeybinds.setDisable(true);
                if(dataFolder != null){
                    installDSF.setDisable(false);
                    uninstallDSF.setDisable(false);
                    recheckDSF.setDisable(false);
                }else{
                    installDSF.setDisable(true);
                    uninstallDSF.setDisable(true);
                    recheckDSF.setDisable(true);
                }
                if(texModsTab != null){
                    texModsTab.setDisable(true);
                }
                installDSF.setText(INSTALL_DSF);
                break;
            default:
                break;
        }

        //DSMfix menu
        if(dsmStatus == 0){
            installDSM.setText(REINSTALL_DSM);
            installDSM.setDisable(false);
            uninstallDSM.setDisable(false);
            configureDSM.setDisable(false);
        }else{
            installDSM.setText(INSTALL_DSM);
            if(dsfStatus == 0){
                installDSM.setDisable(false);
            }else{
                installDSM.setDisable(true);
            }
            uninstallDSM.setDisable(true);
            configureDSM.setDisable(true);
        }

        //DS PvP Watchdog Menu
        if(dataFolder != null){
            installDSPW.setDisable(false);
            recheckDSPW.setDisable(false);
            if(dspwStatus == 0 || dspwStatus == 2){
                installDSPW.setText(REINSTALL_DSPW);
                uninstallDSPW.setDisable(false);
                applyDSPWConfig.setDisable(false);
                if(dspwTab != null){
                    dspwTab.setDisable(false);
                }
            }else if(dspwStatus == 1){
                installDSPW.setText(INSTALL_DSPW);
                uninstallDSPW.setDisable(true);
                applyDSPWConfig.setDisable(true);
                if(dspwTab != null){
                    dspwTab.setDisable(true);
                }
            }
        }else{
            installDSPW.setText(INSTALL_DSPW);
            applyDSPWConfig.setDisable(true);
            installDSPW.setDisable(true);
            uninstallDSPW.setDisable(true);
            recheckDSPW.setDisable(true);
            if(dspwTab != null){
                dspwTab.setDisable(true);
            }
        }

        //Dark Souls Connectivity Mod Menu
        if(dataFolder != null){
            launchDSCM.setDisable(false);
            File dsEXE = new File(dataFolder.toPath() + "\\" + DS_EXE);
            if(dsEXE.exists()){
                launchDSAndCM.setDisable(false);
            }else{
                launchDSAndCM.setDisable(true);
            }
        }else{
            launchDSCM.setDisable(true);
            launchDSAndCM.setDisable(true);
        }

        //Console bar
        /*if(showConsoleBar){
            consoleButton.setDisable(false);
        }else{
            consoleButton.setDisable(true);
        }*/

        updateStatusBarShadow();
    }


    // Update the dimensions of the status bar's shadow
    public void updateStatusBarShadow(){
    	statusBarShadow.setWidth(primaryScene.getWidth() - scrollbarWidth);
    	double consoleBarOffset = 0.0;
    	if(showConsoleBar)
    		consoleBarOffset = consoleBarHeight;
        statusBarShadow.setY(primaryScene.getHeight() - consoleBarOffset - statusBar.getHeight() - statusBarShadow.getHeight());
    }


    public void disableElementsForSleeping(){
        graphicsPane.applySettingsButton.setDisable(true);
        hudPane.applySettingsButton.setDisable(true);
        otherPane.applySettingsButton.setDisable(true);
        savesPane.applySettingsButton.setDisable(true);
        texPane.applySettingsButton.setDisable(true);
        unsafePane.applySettingsButton.setDisable(true);
        wmPane.applySettingsButton.setDisable(true);
        keysPane.applySettingsButton.setDisable(true);
        dspwPane.applySettingsButton.setDisable(true);
        dsMenu.setDisable(true);
        dsfMenu.setDisable(true);
        dsmMenu.setDisable(true);
        dspwMenu.setDisable(true);
        dscmMenu.setDisable(true);
    }

    public void enableElementsForSleeping(){
        graphicsPane.applySettingsButton.setDisable(false);
        hudPane.applySettingsButton.setDisable(false);
        otherPane.applySettingsButton.setDisable(false);
        savesPane.applySettingsButton.setDisable(false);
        texPane.applySettingsButton.setDisable(false);
        unsafePane.applySettingsButton.setDisable(false);
        wmPane.applySettingsButton.setDisable(false);
        keysPane.applySettingsButton.setDisable(false);
        dspwPane.applySettingsButton.setDisable(false);
        dsMenu.setDisable(false);
        dsfMenu.setDisable(false);
        dsmMenu.setDisable(false);
        dspwMenu.setDisable(false);
        dscmMenu.setDisable(false);
    }

    public void applyDSFConfig(){
        if(invalidDSFInputsExist()){
            new AlertDialog(300.0, 80.0, DIALOG_TITLE_CFG_NOT_APPLIED,
                                              COULDNT_APPLY_CFG_ERR, DIALOG_BUTTON_TEXTS[0]);
        }else{
            graphicsPane.applySettingsButton.setDisable(true);
            hudPane.applySettingsButton.setDisable(true);
            otherPane.applySettingsButton.setDisable(true);
            savesPane.applySettingsButton.setDisable(true);
            texPane.applySettingsButton.setDisable(true);
            unsafePane.applySettingsButton.setDisable(true);
            wmPane.applySettingsButton.setDisable(true);
            config.writeSettingsToIniFile(dataFolder.getPath() + "\\" + DSF_FILES[1]);
            graphicsPane.applySettingsButton.setDisable(false);
            hudPane.applySettingsButton.setDisable(false);
            otherPane.applySettingsButton.setDisable(false);
            savesPane.applySettingsButton.setDisable(false);
            texPane.applySettingsButton.setDisable(false);
            unsafePane.applySettingsButton.setDisable(false);
            wmPane.applySettingsButton.setDisable(false);
        }
    }

    public void applyDSPWConfig(){
        if(dspwPane.hasInvalidInputs()){
            new AlertDialog(300.0, 80.0, DIALOG_TITLE_CFG_NOT_APPLIED,
                                              COULDNT_APPLY_CFG_ERR, DIALOG_BUTTON_TEXTS[0]);
        }else{
            dspwPane.applySettingsButton.setDisable(true);
            dspwConfig.applySettings(dataFolder.getPath() + "\\" + DSPW_FILES[3]);
            dspwPane.applySettingsButton.setDisable(false);
        }
    }

    public void printConsole(String message){

        System.out.println(message);
        consoleText.setText(message);
        message += String.format("%n");
        if(writeLogToFile){
        	try {
				Files.write(Paths.get(LOG_FILE), message.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {

			}
        }
        consoleLog += message;
        consoleText.setAlignment(Pos.CENTER_LEFT);
        consoleText.setAlignment(Pos.CENTER);

        if(consoleWindow != null){
            consoleWindow.setMessage(consoleLog);
        }
    }

    //Getter/Accessor Methods
    public Stage getStage(){
        return primaryStage;
    }

    public int getCurrentTab(){
        return primaryTabPane.getSelectionModel().getSelectedIndex();
    }

    public File getDataFolder(){
        return this.dataFolder;
    }

    public int getDSFStatus(){
        return dsfStatus;
    }

    public int getDSPWStatus(){
        return dspwStatus;
    }

    public DSFConfiguration getConfig(){
        return config;
    }

    public DSFKeybindsConfiguration getDSFKeybinds(){
        return dsfKeybinds;
    }

    public DSPWConfiguration getDSPWConfig(){
        return dspwConfig;
    }

    public boolean invalidDSFInputsExist(){
        if(graphicsPane == null){
            return true;
        }else if(graphicsPane.hasInvalidInputs()){
            return true;
        }

        if(hudPane == null){
            return true;
        }else if(hudPane.hasInvalidInputs()){
            return true;
        }

        if(savesPane == null){
            return true;
        }else if(savesPane.hasInvalidInputs()){
            return true;
        }

        if(unsafePane == null){
            return true;
        }else if(unsafePane.hasInvalidInputs()){
            return true;
        }
        return false;
    }

    //Setter/Mutator Methods
    public void setDSVersion(int newVersion){
        dsVersion = newVersion;
        dsVersionLabel.setText(DS_VERSIONS[newVersion] + "      ");
        dsVersionLabel.getStyleClass().clear();

        switch (newVersion) {
            case DS_VER_ENUM_LATEST: //Latest version of Dark Souls is installed
                dsVersionLabel.getStyleClass().addAll("label", "translate_y_4",
                        "green_text");
                dsVersionLabel.setTooltip(new Tooltip(DS_VERSION_DESC));
                break;
            case DS_VER_ENUM_BETA: // Steamworks Beta version of Dark Souls is installed
            case DS_VER_ENUM_DEBUG: // Debug version of Dark Souls is installed
                dsVersionLabel.getStyleClass().addAll("label", "translate_y_4",
                        "yellow_text");
                dsVersionLabel.setTooltip(new Tooltip(DS_VERSION_WARN_DESC));
                break;
            default:
                //Game is not installed, or version is unknown
                dsVersionLabel.getStyleClass().addAll("label", "translate_y_4",
                        "red_text");
                dsVersionLabel.setTooltip(new Tooltip(DS_VERSION_DESC));
                break;
        }

    }

    public void setDSFStatus(int newStatus){
        dsfStatus = newStatus;
        dsfStatusLabel.setText(DSF_STATUS[newStatus] + "         ");
        dsfStatusLabel.getStyleClass().clear();

        if(newStatus == 0){
            //DSFix is installed
            dsfStatusLabel.getStyleClass().addAll("label", "translate_y_4",
                                                  "green_text");
            checkForDSMFix();
        }else{
            //DSFix is not installed, or status is unknown
            dsfStatusLabel.getStyleClass().addAll("label", "translate_y_4",
                                                  "red_text");
            setDSMStatus(dsfStatus);
        }

    }

    public void setDSMStatus(int newStatus){
        dsmStatus = newStatus;
        dsmStatusLabel.setText(DSF_STATUS[newStatus] + "         ");
        dsmStatusLabel.getStyleClass().clear();

        if(newStatus == 0){
            //DSFix is installed
            dsmStatusLabel.getStyleClass().addAll("label", "translate_y_4",
                                                  "green_text");
        }else{
            //DSFix is not installed, or status is unknown
            dsmStatusLabel.getStyleClass().addAll("label", "translate_y_4",
                                                  "red_text");
        }

        refreshUI();
    }

    public void setDSPWStatus(int newStatus){
        dspwStatus = newStatus;
        dspwStatusLabel.setText(DSF_STATUS[newStatus] + "         ");
        dspwStatusLabel.getStyleClass().clear();

        if(newStatus == 0){
            //DSFix is installed
            dspwStatusLabel.getStyleClass().addAll("label", "translate_y_4",
                                                  "green_text");
        }else{
            //DSFix is not installed, or status is unknown
            dspwStatusLabel.getStyleClass().addAll("label", "translate_y_4",
                                                  "red_text");
        }

        refreshUI();
    }

    public void setDataFolder(String newPath){
        dataFolder = new File(newPath);
        checkForDSFix();
        checkForDSPW();
        refreshUI();
    }

    public void setSelectedTab(int index){
        primaryTabPane.getSelectionModel().select(index);
    }

    public void resetDSFConfigDefaults(){
        config = new DSFConfiguration(this, false);
        config.setWindowsResolution();
    }

    public void showConsoleBar(boolean show){
        showConsoleBar = show;
    }

    public void showConsoleWindow(boolean show){
        showConsoleWindow = show;
    }

    public int getDSVersion(){
        return dsVersion;
    }

}
