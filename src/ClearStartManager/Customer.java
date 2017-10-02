package ClearStartManager;

import java.util.List;
import java.util.NoSuchElementException;

class Customer {


    private final String name;
    private List<Setting> settings;

    Customer(String name, List<Setting> settings) {
        this.name = name;
        this.settings = settings;
    }

    String getName() {
        return name;
    }

    List<Setting> getSettings() {
        return settings;
    }

    void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    Setting getSettingByIndex(Integer index) throws NoSuchElementException {
        Setting setting = settings.get(index);
        if (setting != null) {
            return setting;
        }
        throw new NoSuchElementException();
    }

    void setSettingKeyByIndex(Integer index, String key) {
        Setting setting = getSettingByIndex(index);
        setting.setKey(key);
    }

    void setSettingValueByIndex(Integer index, String value) {
        Setting setting = getSettingByIndex(index);
        setting.setValue(value);
    }
}