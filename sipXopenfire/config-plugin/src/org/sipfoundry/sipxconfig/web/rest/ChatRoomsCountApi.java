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

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.sipfoundry.sipxconfig.persist.ChatRoomDao;

public class ChatRoomsCountApi extends Resource {
    private static final Logger LOG = Logger.getLogger(ChatRoomsCountApi.class);

    private ChatRoomDao m_dao;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        getVariants().add(new Variant(APPLICATION_JSON));
    }

    // GET
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        LOG.warn("GET count call");
        Long roomsCount = m_dao.roomsCount();

        return new StringRepresentation(roomsCount.toString());
    }

    public void setDao(ChatRoomDao dao) {
        this.m_dao = dao;
    }
}
