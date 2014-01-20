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

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.sipfoundry.sipxconfig.persist.ChatRoomDao;

public class ChatRoomMgmtApi extends AbstractChatRoomApi {
    private static final Logger LOG = Logger.getLogger(ChatRoomMgmtApi.class);

    private static final String ID = "id";

    private ChatRoomDao m_dao;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        getVariants().add(new Variant(APPLICATION_JSON));
    }

    @Override
    public boolean allowGet() {
        return false;
    }

    @Override
    public boolean allowPost() {
        return true;
    }

    @Override
    public boolean allowPut() {
        return true;
    }

    @Override
    public boolean allowDelete() {
        return true;
    }

    // POST
    @Override
    public void acceptRepresentation(Representation entity) throws ResourceException {
        LOG.warn("POSTed");
        try {
            ChatRoom room = toRoom(entity);
            m_dao.createRoom(room);
        } catch (JsonProcessingException e1) {
            LOG.error("POSTa: " + e1.getMessage());
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            LOG.error("POSTb: " + e1.getMessage());
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    // PUT
    @Override
    public void storeRepresentation(Representation entity) throws ResourceException {
        long roomId = -1;
        LOG.warn("POST");
        String idStr = (String) getRequest().getAttributes().get(ID);
        if (idStr != null) {
            roomId = Long.valueOf(idStr);
        }
        LOG.warn("Room ID: " + roomId);
        if (roomId != -1) {
            try {
                ChatRoom room = toRoom(entity);
                room.setId(roomId);
                if (!m_dao.updateRoom(room)) {
                    throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
                }
            } catch (JsonParseException e) {
                LOG.error("JsonParseException: " + e.getMessage());
            } catch (JsonMappingException e) {
                LOG.error("JsonMappingException: " + e.getMessage());
            } catch (IOException e) {
                LOG.error("IOException: " + e.getMessage());
            }
        } else {
            LOG.info("PUT: missing id");
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }

    // DELETE
    @Override
    public void removeRepresentations() throws ResourceException {
        long roomId = -1;
        LOG.info(getRequest().getAttributes());
        LOG.info(getQuery().getValuesMap());
        String idStr = (String) getRequest().getAttributes().get(ID);
        if (idStr != null) {
            roomId = Long.valueOf(idStr);
        }
        if (roomId != -1) {
            m_dao.deleteRoom(roomId);
        } else {
            LOG.info("DELETE: missing id");
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }

    public void setDao(ChatRoomDao dao) {
        this.m_dao = dao;
    }
}
