package ClearStartManager;

import java.util.List;

class SettingList {
    private List<Setting> settings;

    SettingList(List<Setting> settings) {
        this.settings = settings;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }
}
