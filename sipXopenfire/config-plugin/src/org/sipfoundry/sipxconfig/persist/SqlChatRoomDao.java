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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sipfoundry.sipxconfig.web.rest.ChatRoom;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SqlChatRoomDao extends BaseChatRoomDao implements ChatRoomDao {
    private static final String SELECT_ROOMS = "SELECT * FROM ofMucRoom ORDER BY name OFFSET ? LIMIT ?";
    private static final String SELECT_COUNT = "SELECT count(*) FROM ofMucRoom";
    // @formatter:off
    private static final String INSERT_ROOM = "INSERT INTO ofMucRoom ("
        + "roomID, "
        + "name, "
        + "description, "
        + "canChangeSubject, "
        + "maxusers, "
        + "publicRoom, "
        + "moderated, "
        + "membersOnly, "
        + "canInvite, "
        + "roomPassword, "
        + "canDiscoverJID, "
        + "logEnabled, "
        + "subject, "
        + "rolesToBroadcast) "
        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_ROOM = "UPDATE ofMucRoom SET "
        + "name=? "
        + "description=? "
        + "canChangeSubject=? "
        + "maxusers=? "
        + "publicRoom=? "
        + "moderated=? "
        + "membersOnly=? "
        + "canInvite=? "
        + "roomPassword=? "
        + "canDiscoverJID=? "
        + "logEnabled=? "
        + "subject=? "
        + "rolesToBroadcast=? "
        + "WHERE roomID=?";
    // @formatter:on
    private static final String DELETE_BY_ID = "DELETE FROM ofMucRoom WHERE id=?";

    private JdbcTemplate m_openfireDbTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRoom(ChatRoom room) {
        Object[] params = new Object[] {
            room.getId(), room.getName(), room.getDescription(), room.isCanChangeSubject(), room.getMaxUsers(),
            room.isPublicRoom(), room.isModerated(), room.isMembersOnly(), room.isCanInvite(), room.getPassword(),
            room.isCanDiscoverJid(), room.isLogEnabled(), room.getSubject(), room.getRolesToBroadcast()

        };
        m_openfireDbTemplate.update(INSERT_ROOM, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatRoom> getRooms(int start, int count) {
        List<ChatRoom> rooms = m_openfireDbTemplate.query(SELECT_ROOMS, new Object[] {
            start, count
        }, new RoomMapper());

        return rooms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateRoom(ChatRoom room) {
        Object[] params = new Object[] {
            room.getName(), room.getDescription(), room.isCanChangeSubject(), room.getMaxUsers(),
            room.isPublicRoom(), room.isModerated(), room.isMembersOnly(), room.isCanInvite(), room.getPassword(),
            room.isCanDiscoverJid(), room.isLogEnabled(), room.getSubject(), room.getRolesToBroadcast(),
            room.getId()
        };
        int changedRows = m_openfireDbTemplate.update(UPDATE_ROOM, params);

        return changedRows > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRoom(long roomId) {
        m_openfireDbTemplate.update(DELETE_BY_ID, roomId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long roomsCount() {
        return m_openfireDbTemplate.queryForInt(SELECT_COUNT);
    }

    public void setOpenfireDbTemplate(JdbcTemplate jdbcTemplate) {
        m_openfireDbTemplate = jdbcTemplate;
    }

    private static class RoomMapper implements RowMapper<ChatRoom> {

        @Override
        public ChatRoom mapRow(ResultSet row, int rowNum) throws SQLException {
            ChatRoom room = new ChatRoom();

            room.setId(row.getLong("roomID"));
            room.setName(row.getString("name"));
            room.setDescription(row.getString("description"));
            room.setCanChangeSubject(row.getBoolean("canChangeSubject"));
            room.setMaxUsers(row.getInt("maxusers"));
            room.setPublicRoom(row.getBoolean("publicRoom"));
            room.setModerated(row.getBoolean("moderated"));
            room.setMembersOnly(row.getBoolean("membersOnly"));
            room.setCanInvite(row.getBoolean("canInvite"));
            room.setPassword(row.getString("roomPassword"));
            room.setCanDiscoverJid(row.getBoolean("canDiscoverJID"));
            room.setLogEnabled(row.getBoolean("logEnabled"));
            room.setSubject(row.getString("subject"));
            room.setRolesToBroadcast(row.getInt("rolesToBroadcast"));

            return room;
        }

    }
}
