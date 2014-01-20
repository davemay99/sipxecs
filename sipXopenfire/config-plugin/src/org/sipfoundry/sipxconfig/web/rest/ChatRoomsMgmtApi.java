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

import static org.restlet.data.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.sipfoundry.sipxconfig.persist.ChatRoomDao;

public class ChatRoomsMgmtApi extends AbstractChatRoomApi {
    private static final Logger LOG = Logger.getLogger(ChatRoomsMgmtApi.class);

    private ChatRoomDao m_dao;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        getVariants().add(new Variant(APPLICATION_JSON));
    }

    // GET
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        LOG.warn("GET call " + getQuery().getValuesMap());
        Integer start = null;
        try {
            start = Integer.parseInt(getQuery().getValues("start"));
        } catch (NumberFormatException e) {
            LOG.error(e.getMessage(), e);
        }
        if (start == null) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Dude, you suck");
        }
        LOG.warn("start " + start);
        Integer count = Integer.parseInt(getQuery().getValues("count"));
        if (count == null) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        LOG.warn("count " + count);
        List<ChatRoom> rooms = m_dao.getRooms(start, count);
        String jsonStr;
        try {
            jsonStr = new ObjectMapper().writeValueAsString(rooms);
        } catch (JsonGenerationException e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        } catch (JsonMappingException e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        } catch (IOException e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }

        LOG.warn("Returning: " + jsonStr);
        return new StringRepresentation(jsonStr);
    }

    public void setDao(ChatRoomDao dao) {
        this.m_dao = dao;
    }
}
