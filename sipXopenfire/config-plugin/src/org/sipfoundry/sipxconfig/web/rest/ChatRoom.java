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

import java.io.Serializable;
import java.util.List;

/**
 * Bean describing a chat room.
 */
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    private long m_id;

    private String m_name;
    private String m_description;
    private String m_subject;
    private boolean m_canChangeSubject;
    private String m_password;
    private boolean m_publicRoom;
    private boolean m_moderated;
    private boolean m_membersOnly;
    private int m_maxUsers;
    private boolean m_canInvite;
    private boolean m_canDiscoverJid;
    private boolean m_logEnabled;
    private int m_rolesToBroadcast;
    private IMUser m_owner;
    private List<IMUser> m_members;

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        this.m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        this.m_description = description;
    }

    public String getSubject() {
        return m_subject;
    }

    public void setSubject(String subject) {
        this.m_subject = subject;
    }

    public boolean isCanChangeSubject() {
        return m_canChangeSubject;
    }

    public void setCanChangeSubject(boolean canChangeSubject) {
        this.m_canChangeSubject = canChangeSubject;
    }

    public String getPassword() {
        return m_password;
    }

    public void setPassword(String password) {
        this.m_password = password;
    }

    public boolean isPublicRoom() {
        return m_publicRoom;
    }

    public void setPublicRoom(boolean publicRoom) {
        this.m_publicRoom = publicRoom;
    }

    public boolean isModerated() {
        return m_moderated;
    }

    public void setModerated(boolean moderated) {
        this.m_moderated = moderated;
    }

    public boolean isMembersOnly() {
        return m_membersOnly;
    }

    public void setMembersOnly(boolean membersOnly) {
        this.m_membersOnly = membersOnly;
    }

    public int getMaxUsers() {
        return m_maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.m_maxUsers = maxUsers;
    }

    public boolean isCanInvite() {
        return m_canInvite;
    }

    public void setCanInvite(boolean canInvite) {
        this.m_canInvite = canInvite;
    }

    public boolean isCanDiscoverJid() {
        return m_canDiscoverJid;
    }

    public void setCanDiscoverJid(boolean canDiscoverJid) {
        this.m_canDiscoverJid = canDiscoverJid;
    }

    public boolean isLogEnabled() {
        return m_logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.m_logEnabled = logEnabled;
    }

    public int getRolesToBroadcast() {
        return m_rolesToBroadcast;
    }

    public void setRolesToBroadcast(int rolesToBroadcast) {
        this.m_rolesToBroadcast = rolesToBroadcast;
    }

    public IMUser getOwner() {
        return m_owner;
    }

    public void setOwner(IMUser owner) {
        this.m_owner = owner;
    }

    public List<IMUser> getMembers() {
        return m_members;
    }

    public void setMembers(List<IMUser> members) {
        this.m_members = members;
    }

    @Override
    public String toString() {
        return "ChatRoom [m_id=" + m_id + ", m_name=" + m_name + ", m_description=" + m_description + ", m_subject="
            + m_subject + ", m_canChangeSubject=" + m_canChangeSubject + ", m_password=" + m_password
            + ", m_publicRoom=" + m_publicRoom + ", m_moderated=" + m_moderated + ", m_membersOnly=" + m_membersOnly
            + ", m_maxUsers=" + m_maxUsers + ", m_canInvite=" + m_canInvite + ", m_canDiscoverJid="
            + m_canDiscoverJid + ", m_logEnabled=" + m_logEnabled + ", m_rolesToBroadcast=" + m_rolesToBroadcast
            + ", m_owner=" + m_owner + ", m_members=" + m_members + "]";
    }
}
