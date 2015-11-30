/*
 * Copyright 2015 Philippe Schweitzer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dbiservices.monitoring.tail;

/**
 *
 * @author  Philippe Schweitzer
 * @version 1.1
 * @since   16.11.2015
 */

import com.dbiservices.monitoring.common.schedulerservice.ScheduledDefinition;
import com.dbiservices.tools.ApplicationContext;
import com.dbiservices.tools.Logger;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javafx.scene.paint.Color;

public class ColorConfiguration {

    private static final Logger logger = Logger.getLogger(ColorConfiguration.class);

    public Color backgroundColor;
    public Color defaultColor;
    public Color selectionColor;
    public Color searchColor;

    public ArrayList<PatternColorConfiguration> colorConfigurationList;

    public ColorConfiguration() {
        this((String) ApplicationContext.getInstance().get("colorFileName"));
    }

    public ColorConfiguration(String colorFileName) {

        colorConfigurationList = new ArrayList();

        if (!Files.exists(Paths.get(colorFileName))) {
            colorFileName = "etc/color.cfg";
        }

        if (Files.exists(Paths.get(colorFileName))) {

            File file = new File(colorFileName);
            DataInputStream in = null;
            BufferedReader br = null;

            try {
                FileInputStream fstream = new FileInputStream(file);
                InputStreamReader is = new InputStreamReader(fstream, Charset.forName("UTF-8"));
                br = new BufferedReader(is);

                StringTokenizer stringTokenizer;

                String line = "";
                String backgroudColorString = null;
                String defaultColorString = null;
                String selectionColorString = null;
                String searchColorString = null;
                String paternString = null;
                String paternColor = null;
                String paternCase = "false";

                int lineCounter = 0;

                while (line != null) {
                    if (!line.equals("") && !line.equals("\r")) {

                        stringTokenizer = new StringTokenizer(line, ",;\"\"");

                        int columnCounter = 0;
                        while (stringTokenizer.hasMoreElements()) {

                            String token = stringTokenizer.nextToken();

                            if (lineCounter == 1) {

                                switch (columnCounter) {

                                    case 0:

                                        backgroudColorString = token;
                                        break;

                                    case 1:

                                        defaultColorString = token;
                                        break;

                                    case 2:

                                        selectionColorString = token;
                                        break;

                                    case 3:

                                        searchColorString = token;
                                        break;

                                }
                            } else {
                                switch (columnCounter) {
                                    case 0:

                                        paternString = token;
                                        break;

                                    case 1:

                                        paternColor = token;
                                        break;

                                    case 2:

                                        paternCase = token;
                                        break;
                                }
                            }

                            columnCounter++;
                        }

                        if (lineCounter == 1) {

                            backgroundColor = Color.valueOf(backgroudColorString);
                            defaultColor = Color.valueOf(defaultColorString);
                            selectionColor = Color.valueOf(selectionColorString);
                            searchColor = Color.valueOf(searchColorString);
                        } else {
                            colorConfigurationList.add(new PatternColorConfiguration(paternString, Color.valueOf(paternColor), Boolean.parseBoolean(paternCase)));
                        }
                    }

                    line = br.readLine();
                    lineCounter++;
                }

                br.close();
            } catch (IOException e) {
                logger.error("Error opening color file: " + colorFileName, e);
            }

        } else {

            backgroundColor = Color.valueOf("#505050");
            defaultColor = Color.LIGHTGREY;
            selectionColor = Color.valueOf("#BBD2E1");
            searchColor = Color.MAGENTA;

            colorConfigurationList.add(new PatternColorConfiguration("error", Color.valueOf("#FF7402"), false));
            colorConfigurationList.add(new PatternColorConfiguration("warn", Color.ORANGE, false));
            colorConfigurationList.add(new PatternColorConfiguration("info", Color.CYAN, false));
            colorConfigurationList.add(new PatternColorConfiguration("debug", Color.LIGHTGREY, false));
        }

        if (colorFileName.equals("etc/color.cfg")) {

            ApplicationContext applicationContext = ApplicationContext.getInstance();

            if (applicationContext.containsKey("colorDefaultConfiguration")) {
                applicationContext.remove("colorDefaultConfiguration");
                applicationContext.put("colorDefaultConfiguration", this);
            }
        }

    }

    public Color getSelectionColor() {
        return selectionColor;
    }

    public Color getSearchColor() {
        return searchColor;
    }

    public void setSelectionColor(Color selectionColor) {
        this.selectionColor = selectionColor;
    }

    public void setSearchColor(Color searchColor) {
        this.searchColor = searchColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public java.awt.Color getBackgroundSwingColor() {
        return getSwingColor(backgroundColor);
    }

    public java.awt.Color getSelectionSwingColor() {
        return getSwingColor(selectionColor);
    }

    public java.awt.Color getSearchSwingColor() {
        return getSwingColor(searchColor);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public java.awt.Color getDefaultSwingColor() {
        return getSwingColor(defaultColor);
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public ArrayList<PatternColorConfiguration> getColorConfigurationList() {
        return colorConfigurationList;
    }

    public void setColorConfigurationList(ArrayList<PatternColorConfiguration> colorConfigurationList) {
        this.colorConfigurationList = colorConfigurationList;
    }

    public static java.awt.Color getSwingColor(Color c) {
        
        int r = (int) (c.getRed() * 255);
        int g = (int) (c.getGreen() * 255);
        int b = (int) (c.getBlue() * 255);
        
        int rgb = (r << 16) + (g << 8) + b;

        return new java.awt.Color(rgb);
    }
}
