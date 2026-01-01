package sg.overlay.backend;

import sg.overlay.BaseVolume;
import sg.overlay.IVolume;

public class DatabaseStateLoader {
    public static IVolume fromDatabase(String volumeName) {
        return BaseVolume.ofEntries();
    }
}
