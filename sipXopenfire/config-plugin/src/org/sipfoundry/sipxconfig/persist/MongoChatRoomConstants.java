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

public abstract class MongoChatRoomConstants {
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String SUBJECT = "subject";
    public static final String CAN_CHANGE_SUBJECT = "canChangeSubject";
    public static final String PASSWORD = "roomPassword";
    public static final String PUBLIC_ROOM = "publicRoom";
    public static final String MODERATED = "moderated";
    public static final String MEMBERS_ONLY = "membersOnly";
    public static final String MAX_USERS = "maxUsers";
    public static final String CAN_INVITE = "canInvite";
    public static final String CAN_DISCOVER_JID = "canDiscoverJid";
    public static final String LOG_ENABLED = "logEnabled";
    public static final String ROLES_TO_BROADCAST = "rolesToBroadcast";
    public static final String ROOM_ID = "roomId";
    public static final String JID = "jid";
    public static final String AFFILIATION = "affiliation";
}
