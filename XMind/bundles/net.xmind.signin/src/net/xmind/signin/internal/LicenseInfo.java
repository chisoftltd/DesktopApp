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

import net.xmind.signin.ILicenseInfo;
import net.xmind.signin.ILicenseKeyHeader;

public class LicenseInfo implements ILicenseInfo {

    private final int status;

    private final Throwable error;

    private final String name;

    private final ILicenseKeyHeader licenseKeyHeader;

    public LicenseInfo() {
        this(VERIFYING, null, null, null);
    }

    public LicenseInfo(int status) {
        this(status, null, null, null);
    }

    public LicenseInfo(int status, Throwable error, String name,
            ILicenseKeyHeader licenseKeyHeader) {
        this.status = status;
        this.error = error;
        this.name = name;
        this.licenseKeyHeader = licenseKeyHeader;
    }

    public int getType() {
        return status;
    }

    public Throwable getError() {
        return error;
    }

    public String getLicensedTo() {
        return name;
    }

    public ILicenseKeyHeader getLicenseKeyHeader() {
        return licenseKeyHeader;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("LicenseInfo{"); //$NON-NLS-1$
        buffer.append(status);
        buffer.append(",error="); //$NON-NLS-1$
        buffer.append(error == null ? "" : error.getLocalizedMessage()); //$NON-NLS-1$
        buffer.append(",licensedTo="); //$NON-NLS-1$
        buffer.append(name);
        buffer.append(",licenseKeyHeader="); //$NON-NLS-1$
        buffer.append(licenseKeyHeader);
        buffer.append('}');
        return super.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !(obj instanceof LicenseInfo))
            return false;
        LicenseInfo that = (LicenseInfo) obj;
        return this.status == that.status
                && (this.name == that.name || (this.name != null && this.name
                        .equals(that.name)))
                && (this.error == that.error || (this.error != null && this.error
                        .equals(that.error)))
                && (this.licenseKeyHeader == that.licenseKeyHeader || (this.licenseKeyHeader != null && this.licenseKeyHeader
                        .equals(that.licenseKeyHeader)));
    }

    public int hashCode() {
        return status;
    }

}
