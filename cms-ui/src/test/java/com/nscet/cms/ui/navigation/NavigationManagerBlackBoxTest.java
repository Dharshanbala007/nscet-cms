package com.nscet.cms.ui.navigation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class NavigationManagerBlackBoxTest {

    @Test
    @DisplayName("Black-Box Resource Verification: All Mapped FXML Files Must Exist in Classpath")
    @SuppressWarnings("unchecked")
    void verifyAllFxmlFilesExistInClasspath() throws Exception {
        Field mapField = NavigationManager.class.getDeclaredField("MODULE_FXML_MAP");
        mapField.setAccessible(true);
        Map<String, String> fxmlMap = (Map<String, String>) mapField.get(null);

        assertNotNull(fxmlMap, "MODULE_FXML_MAP should not be null.");
        assertFalse(fxmlMap.isEmpty(), "MODULE_FXML_MAP should contain module mappings.");

        int missingCount = 0;
        StringBuilder missingFiles = new StringBuilder();

        for (Map.Entry<String, String> entry : fxmlMap.entrySet()) {
            String moduleKey = entry.getKey();
            String fxmlPath = entry.getValue();

            InputStream is = NavigationManager.class.getResourceAsStream(fxmlPath);
            if (is == null) {
                missingCount++;
                missingFiles.append("\n- Module '").append(moduleKey).append("' -> Missing FXML: ").append(fxmlPath);
            } else {
                is.close();
            }
        }

        assertEquals(0, missingCount, "All mapped FXML files must exist on the classpath:" + missingFiles);
    }

    @Test
    @DisplayName("Black-Box Resource Verification: Core Layout & Report FXMLs")
    void testCoreLayoutAndReportFxmlsExist() {
        String[] coreFxmls = {
                "/fxml/MainShell.fxml",
                "/fxml/PortalSelection.fxml",
                "/fxml/Login.fxml",
                "/fxml/reports/PendingFeesReport.fxml",
                "/fxml/reports/FeesLetterDialog.fxml",
                "/fxml/reports/DfcrReport.fxml",
                "/fxml/reports/ParentsMeeting.fxml",
                "/fxml/tools/StudentEnrollment.fxml",
                "/fxml/tools/BulkFeeEntry.fxml",
                "/fxml/tools/BusFeesUpdate.fxml"
        };

        for (String path : coreFxmls) {
            InputStream is = getClass().getResourceAsStream(path);
            assertNotNull(is, "Core FXML resource path must exist: " + path);
            try {
                is.close();
            } catch (Exception ignored) {}
        }
    }
}
