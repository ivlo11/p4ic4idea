/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.groboclown.idea.p4ic.ui.config;

import com.intellij.openapi.project.Project;
import net.groboclown.idea.p4ic.config.ManualP4Config;
import net.groboclown.idea.p4ic.config.P4ConfigProject;
import net.groboclown.idea.p4ic.config.UserProjectPreferences;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class P4ConfigurationProjectPanel {

    private final Project project;
    private P4SettingsPanel myMainPanel;
    private volatile boolean isInitialized = false;

    public P4ConfigurationProjectPanel(@NotNull Project project) {
        this.project = project;
    }

    public boolean isModified(@NotNull ManualP4Config myConfig, @NotNull UserProjectPreferences preferences) {
        if (!isInitialized) {
            return false;
        }

        return myMainPanel.isModified(myConfig, preferences);
    }

    public void saveSettings(@NotNull P4ConfigProject config, @NotNull UserProjectPreferences preferences) {
        if (!isInitialized) {
            // nothing to do
            return;
        }
        ManualP4Config saved = new ManualP4Config();
        myMainPanel.saveSettingsToConfig(saved, preferences);
        config.loadState(saved);
    }

    public void loadSettings(@NotNull ManualP4Config config, @NotNull UserProjectPreferences preferences) {
        if (!isInitialized) {
            getPanel(config, preferences);
            return;
        }

        myMainPanel.loadSettingsIntoGUI(config, preferences);
    }

    public synchronized JPanel getPanel(@NotNull ManualP4Config config, @NotNull UserProjectPreferences preferences) {
        if (!isInitialized) {
            myMainPanel = new P4SettingsPanel();
            myMainPanel.initialize(project);
            isInitialized = true;
        }
        loadSettings(config, preferences);
        return myMainPanel.getPanel();
    }

    public void dispose() {
        //myMainPanel.dispose();
        myMainPanel = null;
        isInitialized = false;
    }
}