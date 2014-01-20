library chat_room;

import 'dart:convert';
import 'package:logging/logging.dart';

class ChatRoom {
  int id;
  String name;
  String description;
  String subject;
  bool canChangeSubject;
  String password;
  bool public;
  bool moderated;
  bool membersOnly;
  int maxUsers;
  bool canInvite;
  bool canDiscoverJid;
  bool logMessages;
  int rolesToBroadcast;
  User owner;
  List<User> members;

  ChatRoom({
      this.id : -1,
      this.name : "",
      this.description : "",
      this.subject : "",
      this.canChangeSubject : true,
      this.password : "",
      this.public : true,
      this.moderated : false,
      this.membersOnly : false,
      this.maxUsers : 10,
      this.canInvite : true,
      this.canDiscoverJid : false,
      this.logMessages : true,
      this.rolesToBroadcast : 7,
      this.owner : null,
      this.members : const []
  });

  factory ChatRoom.fromMap(Map attrs) {
    ChatRoom room = new ChatRoom(
        id: attrs['id'],
        name: attrs['name'],
        description: attrs['description'],
        subject: attrs['subject'],
        canChangeSubject: attrs['canChangeSubject'],
        password: attrs['password'],
        public: attrs['publicRoom'],
        moderated: attrs['moderated'],
        membersOnly: attrs['membersOnly'],
        maxUsers: attrs['maxUsers'],
        canInvite: attrs['canInvite'],
        canDiscoverJid: attrs['canDiscoverJid'],
        logMessages: attrs['logEnabled'],
        rolesToBroadcast: attrs['rolesToBroadcast'],
        owner: new User(attrs['owner']),
        members: _convert(attrs['members'])
    );

    return room;
  }

  static List<User> _convert(List<Map> userMaps) {
    List<User> converted = [];

    if(userMaps != null) {
      userMaps.forEach((Map userMap) => converted.add(new User(userMap)));
    }

    return converted;
  }

  get broadcastModerator => rolesToBroadcast & 1 != 0;
  set broadcastModerator(int val) => rolesToBroadcast |= val;

  get broadcastParticipant => rolesToBroadcast & 2 != 0;
  set broadcastParticipant(int val) => rolesToBroadcast |= val;

  get broadcastVisitor => rolesToBroadcast & 4 != 0;
  set broadcastVisitor(int val) => rolesToBroadcast |= val;

  Map<String, dynamic> asMap() {
    Map<String, dynamic> m = {};

    m["id"] = id;
    m["name"] = name;
    m["description"] = description;
    m["subject"] = subject;
    m["canChangeSubject"] = canChangeSubject;
    m["password"] = password;
    m["publicRoom"] = public;
    m["moderated"] = moderated;
    m["membersOnly"] = membersOnly;
    m["maxUsers"] = maxUsers;
    m["canInvite"] = canInvite;
    m["canDiscoverJid"] = canDiscoverJid;
    m["logEnabled"] = logMessages;
    m["rolesToBroadcast"] = rolesToBroadcast;
    m["owner"] = owner.asMap();
    List<Map<String, dynamic>> membersList = [];
    members.forEach((User member) => membersList.add(member.asMap()));
    m["members"] = membersList;
    Logger.root.fine("asMap: " + m.toString());

    return m;
  }

  String toJson() {
    Map<String, dynamic> m = asMap();
    m.remove("id"); // don't encode id, it's sent as part of the URL
    Logger.root.fine(JSON.encode(m));
    return JSON.encode(m);
  }

  String toString() => asMap().toString();
}

class User {
  String id;
  String name;
  String displayName;
  int hashCode;

  User(Map<String, dynamic> attrs) {
    //if(attrs != null) {
      id = attrs['_id'];
      name = attrs['imid'];
      displayName = attrs['imdn'];
      hashCode = id.hashCode;
    //}
  }

  bool operator ==(var other) => other.runtimeType == this.runtimeType && id == other.id;

  String toString() => asMap().toString();

  String prettyName() => name != displayName ? "${name} [${displayName}]" : name;

  Map<String, dynamic> asMap() {
    Map<String, dynamic> m = {};

    m["id"] = id;
    m["imid"] = name;
    m["imdn"] = displayName;

    return m;
  }

  String toJson() {
    Logger.root.fine(JSON.encode(asMap()));
    return JSON.encode(asMap());
  }
}
