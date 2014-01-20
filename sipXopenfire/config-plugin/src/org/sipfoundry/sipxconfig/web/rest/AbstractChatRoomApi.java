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

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;

public abstract class AbstractChatRoomApi extends Resource {
    private static final Logger LOG = Logger.getLogger(AbstractChatRoomApi.class);

    protected static ChatRoom toRoom(Representation entity) throws IOException, JsonParseException,
        JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String inStr = IOUtils.toString(entity.getStream(), "UTF-8");
        inStr = StringEscapeUtils.unescapeJava(inStr);
        inStr = StringEscapeUtils.unescapeJava(inStr);
        LOG.debug("Converting: " + inStr);
        ChatRoom room = mapper.readValue(inStr, ChatRoom.class);
        LOG.debug("Converted: " + room);

        return room;
    }
}
