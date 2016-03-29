/* ******************************************************************************
 * Copyright (c) 2006-2012 XMind Ltd. and others.
 * 
 * This file is a part of XMind 3. XMind releases 3 and
 * above are dual-licensed under the Eclipse Public License (EPL),
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 * and the GNU Lesser General Public License (LGPL), 
 * which is available at http://www.gnu.org/licenses/lgpl.html
 * See http://www.xmind.net/license.html for details.
 * 
 * Contributors:
 *     XMind Ltd. - initial API and implementation
 *******************************************************************************/
package net.xmind.signin.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

public class UpdateData {

    private static final String VERSION_TYPE = "Version"; //$NON-NLS-1$

    private static final String DOWNLOAD_URL_TYPE = "DownloadURL"; //$NON-NLS-1$

    private static final String ALL_DOWNLOADS_TYPE = "AllDownloadsURL"; //$NON-NLS-1$

    private static final String SIZE_TYPE = "Size"; //$NON-NLS-1$

    private static final String WHATS_NEW_TYPE = "WhatsNew"; //$NON-NLS-1$

    private static final String CAN_INSTALL_TYPE = "CanInstall"; //$NON-NLS-1$

    private static final String INSTALLER_PATH = "InstallerPath"; //$NON-NLS-1$

    private static final String BUILD_ID_TYPE = "BuildId"; //$NON-NLS-1$

    private Properties data;

    private UpdateData(Properties data) {
        this.data = data;
    }

    private String getValue(String name) {
        return data.getProperty(name);
    }

    private long parseLong(String value, long defaultValue) {
        if (value == null)
            return defaultValue;
        try {
            return Long.parseLong(value, 10);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null)
            return defaultValue;
        if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) //$NON-NLS-1$ //$NON-NLS-2$
            return true;
        if ("no".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        return defaultValue;
    }

    private void setValue(String name, String value) {
        if (value == null) {
            data.remove(name);
        } else {
            data.setProperty(name, value);
        }
    }

    public String getDownloadURL() {
        return getValue(DOWNLOAD_URL_TYPE);
    }

    public String getAllDownloadsURL() {
        return getValue(ALL_DOWNLOADS_TYPE);
    }

    public String getVersion() {
        return getValue(VERSION_TYPE);
    }

    public String getBuildId() {
        return getValue(BUILD_ID_TYPE);
    }

    public long getSize() {
        return parseLong(getValue(SIZE_TYPE), 0);
    }

    public String getWhatsNew() {
        return getValue(WHATS_NEW_TYPE);
    }

    public boolean canInstall() {
        return parseBoolean(getValue(CAN_INSTALL_TYPE), false);
    }

    public File getInstallerFile() {
        String path = getValue(INSTALLER_PATH);
        if (path == null)
            return null;
        return new File(path);
    }

    public void setDownloadURL(String downloadURL) {
        setValue(DOWNLOAD_URL_TYPE, downloadURL);
    }

    public void setAllDownloadsURL(String allDownloadsURL) {
        setValue(ALL_DOWNLOADS_TYPE, allDownloadsURL);
    }

    public void setVersion(String version) {
        setValue(VERSION_TYPE, version);
    }

    public void setBuildId(String buildId) {
        setValue(BUILD_ID_TYPE, buildId);
    }

    public void setSize(long size) {
        setValue(SIZE_TYPE, Long.toString(size));
    }

    public void setWhatsNew(String whatsNew) {
        setValue(WHATS_NEW_TYPE, whatsNew);
    }

    public void setCanInstall(boolean canInstall) {
        setValue(CAN_INSTALL_TYPE, Boolean.toString(canInstall));
    }

    public void setInstallerFile(File installerFile) {
        String path = installerFile == null ? null : installerFile
                .getAbsolutePath();
        setValue(INSTALLER_PATH, path);
    }

    public boolean save() {
        File file = getUpdateDataFile();
        File dir = file.getParentFile();
        if (!dir.isDirectory())
            dir.mkdirs();
        if (!dir.isDirectory())
            return false;
        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                data.store(stream, "Generated by XMind about software updates."); //$NON-NLS-1$
                return true;
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static UpdateData createNewData() {
        return new UpdateData(new Properties());
    }

    public static UpdateData loadData() {
        File file = getUpdateDataFile();
        return loadDataFromFile(file);
    }

    private static UpdateData loadDataFromFile(File file) {
        if (file.exists() && file.canRead()) {
            try {
                InputStream stream = new BufferedInputStream(
                        new FileInputStream(file), 1024);
                try {
                    Properties properties = new Properties();
                    properties.load(stream);
                    return new UpdateData(properties);
                } finally {
                    try {
                        stream.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static void clear() {
        File file = getUpdateDataFile();
        UpdateData data = loadDataFromFile(file);
        if (data != null) {
            File installerFile = data.getInstallerFile();
            if (installerFile != null) {
                installerFile.delete();
            }
        }
        file.delete();
    }

    private static File getUpdateDataFile() {
        File dir = getUpdateFolder();
        return new File(dir, "update.properties"); //$NON-NLS-1$
    }

    public static File getUpdateFolder() {
        String instancePath = getInstancePath();
        if (instancePath == null) {
            return new File(System.getProperty("java.io.tmpdir"), "XMindUpdate"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return new File(instancePath, "SoftwareUpdate"); //$NON-NLS-1$
    }

    private static String getInstancePath() {
        Location instanceLoc = Platform.getInstanceLocation();
        if (instanceLoc != null) {
            URL url = instanceLoc.getURL();
            try {
                url = FileLocator.toFileURL(url);
            } catch (IOException e) {
            }
            String file = url.getFile();
            if (file != null && !"".equals(file)) {//$NON-NLS-1$
                return file;
            }
            return url.toExternalForm();
        }
        return null;
    }

}
