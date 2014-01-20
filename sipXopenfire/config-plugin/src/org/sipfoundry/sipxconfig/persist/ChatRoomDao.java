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

import java.util.List;

import org.sipfoundry.sipxconfig.web.rest.ChatRoom;

/**
 * Access to chat rooms as stored in Openfire's DB
 */
public interface ChatRoomDao {

    /**
     * Create a room
     *
     * @param room Room as a bean. See {@linkplain ChatRoom}
     */
    void createRoom(ChatRoom room);

    /**
     * Retrieve rooms with support for paging. IMPORTANT: in order to guarantee consistent
     * results, there must be an implicit sorting of the rooms when querying the DB
     *
     * @param start Return rooms after skipping this many
     * @param count Number of rooms to retrieve
     * @return List of chat rooms
     */
    List<ChatRoom> getRooms(int start, int count);

    /**
     * Updates a chat room, if a room with the specified id exists
     *
     * @param room Room as a bean. See {@linkplain ChatRoom}
     * @return <code>true</code> if a room was updated, <code>false</code> otherwise
     */
    boolean updateRoom(ChatRoom room);

    /**
     * @param roomId ID of the room to delete
     */
    void deleteRoom(long roomId);

    /**
     * @return The total number of the rooms in the DB
     */
    long roomsCount();

}
