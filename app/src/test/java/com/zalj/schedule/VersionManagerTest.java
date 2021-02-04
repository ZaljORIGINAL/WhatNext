package com.zalj.schedule;

import com.zalj.schedule.VersionControl.CallBack;
import com.zalj.schedule.VersionControl.Exceptions.VersionNotReceivedException;
import com.zalj.schedule.VersionControl.Version;
import com.zalj.schedule.VersionControl.VersionManager;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class VersionManagerTest {

    @Test
    public void getVersionInfoFromServer(){
        VersionManager versionManager = VersionManager.getInstance();
        versionManager.checkVersion(
                (enabled, version) -> {
                    assertNotNull(version);
                }
        );
    }
}
