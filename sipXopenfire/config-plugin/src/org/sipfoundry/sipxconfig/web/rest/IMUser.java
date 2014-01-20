/**
 * Copyright (c) 2014 eZuce, Inc. All rights reserved.
 * Contributed to SIPfoundry under a Contributor Agreement
 *
 * This software is free software; you can redistribute it and/or modify it under
 * the terms of the Affero General Public License (AGPL) as published by the
 * Free Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 */
package org.sipfoundry.sipxconfig.web.rest;

public class IMUser {
    private String m_id;
    private String m_imid;
    private String m_imdn;

    public String getId() {
        return m_id;
    }

    public void setId(String id) {
        this.m_id = id;
    }

    public String getImid() {
        return m_imid;
    }

    public void setImid(String imid) {
        this.m_imid = imid;
    }

    public String getImdn() {
        return m_imdn;
    }

    public void setImdn(String imdn) {
        this.m_imdn = imdn;
    }

    @Override
    public String toString() {
        return "IMUser [m_id=" + m_id + ", m_imid=" + m_imid + ", m_imdn=" + m_imdn + "]";
    }
}
