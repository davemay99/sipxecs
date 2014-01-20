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
package org.sipfoundry.sipxconfig.persist;

import org.sipfoundry.sipxconfig.web.rest.IMUser;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;


public abstract class BaseChatRoomDao {
    private MongoTemplate m_imDbTemplate;
    private DBCollection m_usersCol;

    public void setImDbTemplate(MongoTemplate imDbTemplate) {
        m_imDbTemplate = imDbTemplate;

        m_usersCol = m_imDbTemplate.getCollection("entity");
    }

    protected IMUser getUser(String imid) {
        IMUser user = null;

        DBObject query = new BasicDBObject();
        query.put("ent", "user");
        query.put("imenbld", true);
        query.put("imid", imid);

        DBObject userObj = m_usersCol.findOne(query);

        if (userObj != null) {
            user = new IMUser();
            String id = (String) userObj.get("_id");
            String imdn = (String) userObj.get("imdn");
            user.setId(id);
            user.setImid(imid);
            user.setImdn(imdn);
        }

        return user;
    }

}
