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

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UserDao {
    private MongoTemplate m_imDbTemplate;

    public List<DBObject> getUsers() {
        List<DBObject> users = new ArrayList<DBObject>();
        DBObject query = new BasicDBObject();
        query.put("imenbld", true);
        query.put("ent", "user");

        DBObject fields = new BasicDBObject();
        fields.put("imid", 1);
        fields.put("imdn", 1);

        for (DBObject userObj : m_imDbTemplate.getCollection("entity").find(query, fields)) {
            users.add(userObj);
        }

        return users;
    }

    public void setImDbTemplate(MongoTemplate imDbTemplate) {
        m_imDbTemplate = imDbTemplate;
    }
}
