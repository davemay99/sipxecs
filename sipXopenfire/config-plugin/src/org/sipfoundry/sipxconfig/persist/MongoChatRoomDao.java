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

import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.AFFILIATION;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.CAN_CHANGE_SUBJECT;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.CAN_DISCOVER_JID;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.CAN_INVITE;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.DESCRIPTION;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.ID;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.JID;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.LOG_ENABLED;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.MAX_USERS;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.MEMBERS_ONLY;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.MODERATED;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.NAME;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.PASSWORD;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.PUBLIC_ROOM;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.ROLES_TO_BROADCAST;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.ROOM_ID;
import static org.sipfoundry.sipxconfig.persist.MongoChatRoomConstants.SUBJECT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sipfoundry.sipxconfig.domain.Domain;
import org.sipfoundry.sipxconfig.web.rest.ChatRoom;
import org.sipfoundry.sipxconfig.web.rest.IMUser;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoChatRoomDao extends BaseChatRoomDao implements ChatRoomDao {
    private static final Logger LOG = Logger.getLogger(MongoChatRoomDao.class);

    private static final String SUBDOMAIN = "subdomain";
    private static final String CONFERENCE_SUBDOMAIN = "conference";
    private static final int MUC_ROOM_SEQ_ID = 23; // defined as constant in Openfire
    private static final int OWNER = 10; // defined as constant in Openfire
    private static final String JID_TEMPLATE = "%s@%s";
    private static final String SEQ_ID = "id";
    private static final String TYPE_COL = "idType";

    private MongoTemplate m_openfireDbTemplate;

    private DBCollection m_roomsCol;
    private DBCollection m_affiliationsCol;
    private DBCollection m_membersCol;
    private DBCollection m_idsCol;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRoom(ChatRoom room) {
        DBObject service = findService();

        DBObject seqObj = new BasicDBObject(TYPE_COL, MUC_ROOM_SEQ_ID);
        DBObject update = new BasicDBObject();
        update.put(SEQ_ID, 1);

        DBObject idObj = m_idsCol.findAndModify(seqObj, new BasicDBObject("$inc", update));
        room.setId((Long) idObj.get(SEQ_ID));

        addOwnerAndMembers(room, false);

        DBObject roomObj = roomToDBObject(room, service.get(ID));

        m_roomsCol.save(roomObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatRoom> getRooms(int start, int count) {
        List<ChatRoom> rooms = new ArrayList<ChatRoom>();

        DBObject byName = new BasicDBObject(NAME, 1);
        for (DBObject roomObj : m_roomsCol.find().sort(byName).skip(start)
                .limit(count)) {
            rooms.add(dbObjectToRoom(roomObj));
        }

        return rooms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateRoom(ChatRoom room) {
        boolean updated = false;
        DBObject byId = new BasicDBObject(ID, room.getId());
        LOG.warn("Updating room: " + room.getId());
        if (m_roomsCol.findOne(byId) != null) {
            DBObject service = findService();
            DBObject updatedRoom = roomToDBObject(room, service.get(ID));

            updated = m_roomsCol.update(byId, updatedRoom).getError() == null;
            addOwnerAndMembers(room, true);
        }

        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRoom(long roomId) {
        DBObject toDelete = new BasicDBObject();
        DBObject toDeleteOther = new BasicDBObject();

        toDelete.put(ID, roomId);
        toDeleteOther.put(ROOM_ID, roomId);

        m_affiliationsCol.remove(toDeleteOther);
        m_membersCol.remove(toDeleteOther);
        m_roomsCol.remove(toDelete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long roomsCount() {
        return m_roomsCol.count();
    }

    /**
     * Set DB template, make a note of the collection we'll use and ensure the sequence is in
     * place
     *
     * @param openfireDbTemplate
     */
    public void setOpenfireDbTemplate(MongoTemplate openfireDbTemplate) {
        m_openfireDbTemplate = openfireDbTemplate;

        m_roomsCol = m_openfireDbTemplate.getCollection("ofMucRoom");
        m_affiliationsCol = m_openfireDbTemplate.getCollection("ofMucAffiliation");
        m_membersCol = m_openfireDbTemplate.getCollection("ofMucMember");
        m_idsCol = m_openfireDbTemplate.getCollection("ofId");
        // make sure the "sequence" is initialized
        DBObject seqObj = new BasicDBObject(TYPE_COL, MUC_ROOM_SEQ_ID);
        if (m_idsCol.findOne(seqObj) == null) {
            seqObj.put(SEQ_ID, 1L); // start sequence
            m_idsCol.insert(seqObj);
        }
    }

    private static DBObject createService(DBCollection mucService) {
        DBObject service = new BasicDBObject();

        service.put(ID, 1L);
        service.put(SUBDOMAIN, CONFERENCE_SUBDOMAIN);
        service.put("description", "default MUC service");
        service.put("isHidden", false);

        mucService.save(service);

        return service;
    }

    private ChatRoom dbObjectToRoom(DBObject roomObj) {
        ChatRoom room = new ChatRoom();

        Long roomId = (Long) roomObj.get(ID);
        String name = (String) roomObj.get(NAME);
        String description = (String) roomObj.get(DESCRIPTION);
        Boolean canChangeSubject = (Boolean) roomObj.get(CAN_CHANGE_SUBJECT);
        Integer maxUsers = (Integer) roomObj.get(MAX_USERS);
        Boolean publicRoom = (Boolean) roomObj.get(PUBLIC_ROOM);
        Boolean moderated = (Boolean) roomObj.get(MODERATED);
        Boolean membersOnly = (Boolean) roomObj.get(MEMBERS_ONLY);
        Boolean canInvite = (Boolean) roomObj.get(CAN_INVITE);
        String roomPassword = (String) roomObj.get(PASSWORD);
        Boolean canDiscoverJid = (Boolean) roomObj.get(CAN_DISCOVER_JID);
        Boolean logEnabled = (Boolean) roomObj.get(LOG_ENABLED);
        String subject = (String) roomObj.get(SUBJECT);
        Integer rolesToBroadcast = (Integer) roomObj.get(ROLES_TO_BROADCAST);

        DBObject query = new BasicDBObject(ROOM_ID, roomId);

        List<IMUser> members = new ArrayList<IMUser>();
        for (DBObject memberObj : m_membersCol.find(query)) {
            String memberJid = (String) memberObj.get(JID);
            String memberId = memberJid.substring(0, memberJid.indexOf('@'));
            members.add(getUser(memberId));
        }
        room.setMembers(members);

        query.put(AFFILIATION, OWNER);

        DBObject ownerObj = m_affiliationsCol.findOne(query);
        LOG.warn("Query: " + query);
        LOG.warn(String.format("Owner for room %d found? %b", roomId, ownerObj != null));
        if (ownerObj != null) {
            String ownerJid = (String) ownerObj.get(JID);
            String ownerId = ownerJid.substring(0, ownerJid.indexOf('@'));
            room.setOwner(getUser(ownerId));
        }

        room.setId(roomId);
        room.setName(name);
        room.setDescription(description);
        room.setSubject(subject);
        room.setCanChangeSubject(canChangeSubject);
        room.setPassword(roomPassword);
        room.setPublicRoom(publicRoom);
        room.setModerated(moderated);
        room.setMembersOnly(membersOnly);
        room.setMaxUsers(maxUsers);
        room.setCanInvite(canInvite);
        room.setCanDiscoverJid(canDiscoverJid);
        room.setLogEnabled(logEnabled);
        room.setRolesToBroadcast(rolesToBroadcast);

        return room;
    }

    private void addOwnerAndMembers(ChatRoom room, boolean deletePrevious) {
        if (deletePrevious) {
            DBObject affiliation = new BasicDBObject();

            affiliation.put(ROOM_ID, room.getId());
            affiliation.put(AFFILIATION, OWNER);

            m_affiliationsCol.remove(affiliation);

            DBObject memberObj = new BasicDBObject();
            memberObj.put(ROOM_ID, room.getId());

            m_membersCol.remove(memberObj);
        }

        // create MUC room affiliation
        DBObject affiliation = new BasicDBObject();

        affiliation.put(ROOM_ID, room.getId());
        affiliation.put(JID, String.format(JID_TEMPLATE, room.getOwner().getImid(), Domain.getDomain().getName()));
        affiliation.put(AFFILIATION, OWNER);
        m_affiliationsCol.save(affiliation);

        LOG.warn("Members");
        if (room.isMembersOnly()) {
            LOG.warn("Members only");
            for (IMUser member : room.getMembers()) {
                LOG.warn("Saving " + member.getImid());
                DBObject memberObj = new BasicDBObject();
                memberObj.put(ROOM_ID, room.getId());
                memberObj.put(JID, String.format(JID_TEMPLATE, member.getImid(), Domain.getDomain().getName()));

                m_membersCol.save(memberObj);
            }
        }
    }

    private DBObject findService() {
        // create MUC service if needed
        DBCollection mucService = m_openfireDbTemplate.getCollection("ofMucService");
        DBObject serviceQuery = new BasicDBObject();
        serviceQuery.put(SUBDOMAIN, CONFERENCE_SUBDOMAIN);
        DBObject service = mucService.findOne(serviceQuery);

        if (service == null) {
            LOG.debug("Conference service not found. Creating.");
            service = createService(mucService);
        }

        return service;
    }

    private static DBObject roomToDBObject(ChatRoom room, Object serviceId) {
        DBObject roomObj = new BasicDBObject();

        roomObj.put(ID, room.getId());
        roomObj.put("serviceId", serviceId);
        String now = String.valueOf(new Date().getTime());
        roomObj.put("creationDate", now);
        roomObj.put("modificationDate", now);
        roomObj.put(NAME, room.getName().toLowerCase());
        roomObj.put("naturalName", room.getName());
        roomObj.put(DESCRIPTION, room.getDescription());
        roomObj.put("lockedDate", "000000000000000");
        roomObj.put("emptyDate", null);
        roomObj.put(CAN_CHANGE_SUBJECT, room.isCanChangeSubject());
        roomObj.put(MAX_USERS, room.getMaxUsers());
        roomObj.put(PUBLIC_ROOM, room.isPublicRoom());
        roomObj.put(MODERATED, room.isModerated());
        roomObj.put(MEMBERS_ONLY, room.isMembersOnly());
        roomObj.put(CAN_INVITE, room.isCanInvite());
        roomObj.put(PASSWORD, StringUtils.isEmpty(room.getPassword()) ? room.getPassword() : null);
        roomObj.put(CAN_DISCOVER_JID, room.isCanDiscoverJid());
        roomObj.put(LOG_ENABLED, room.isLogEnabled());
        roomObj.put(SUBJECT, room.getSubject());
        roomObj.put(ROLES_TO_BROADCAST, room.getRolesToBroadcast());
        roomObj.put("useReservedNick", false);
        roomObj.put("canChangeNick", true);
        roomObj.put("canRegister", true);

        return roomObj;
    }
}
