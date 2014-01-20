library rooms_controller;

import 'dart:html';
import 'dart:convert';
import 'dart:async';
import '../sipxconfig.dart';
import 'package:angular/angular.dart';
import 'package:logging/logging.dart';
import '../lib/chat_room.dart';

@NgController(
    selector: '[rooms]',
    publishAs: 'roomCtrl')
class RoomsController {
  // page data
  List<ChatRoom> _rooms = [];
  Map<int, ChatRoom> _roomsMap;
  ChatRoom currentRoom = null;
  int roomCount = 0;
  int _itemsPerPage = 1;
  int _currentPage = 0;
  int _maxPages = 0;

  // page state
  String _show = "";
  bool _showOwnerPicker = false;
  bool _showMembersPicker = false;
  bool _saveClicked = false;

  Api api = new Api(test : false);
  UserMessage msg = new UserMessage(querySelector("#userMessage"));

  RoomsController() {
    Logger.root.fine("Msg is $msg");
    _reload();
  }

  get rooms => _rooms;
  get roomsMap => _roomsMap;
  get showOwnerPicker => _showOwnerPicker;
  get showMembersPicker => _showMembersPicker;
  set showOwnerPicker(bool val) {
    _showOwnerPicker = val;
    show = val ? "owner" : "editor";
  }
  set showMembersPicker(bool val) {
    _showMembersPicker = val;
    show = val ? "members" : "editor";
  }
  get show => _show;

  set show(String val) {
    _show = val;
    //Logger.root.fine("Current room type: ${currentRoom.runtimeType}");
    //Logger.root.fine("Current room: " + currentRoom.toString());
  }

  get maxPages => _maxPages;

  void selectAdd() {
    currentRoom = new ChatRoom();
    show = "editor";
    _saveClicked = false;
  }

  void selectEdit(ChatRoom room) {
    currentRoom = room;
    show = "editor";
    _saveClicked = false;
  }

  void _getCount(String data) {
    roomCount = JSON.decode(data);
    _maxPages = (roomCount / _itemsPerPage).ceil();
    Logger.root.fine("Found ${roomCount} rooms");
    if(roomCount > 0) {
      Logger.root.fine("Loading page $_currentPage");
      DataLoader loader = new DataLoader(msg, _loadRooms);
      loader.load(api.url("rest/chatrooms?start=${_currentPage * _itemsPerPage}&count=${_itemsPerPage}", 'chatroommgmt_test.json'));
    }
  }

  void _loadRooms(String data) {
    List<Map> tmpRooms = JSON.decode(data);
    Logger.root.fine("Loaded rooms $tmpRooms");
    rooms.clear();
    tmpRooms.forEach((Map roomMap) => rooms.add(new ChatRoom.fromMap(roomMap)));
    _roomsMap = new Map.fromIterable(rooms,
        key: (ChatRoom item) => item.id,
        value: (ChatRoom item) => item
    );
    Logger.root.fine("Parsed ok");
  }

  void delete(ChatRoom room) {
    if (window.confirm("Are you sure you want to delete ${room.name}?")) {
      HttpRequest req = new HttpRequest();
      req.open('DELETE', api.url("rest/chatroom/${room.id}/"));
      req.setRequestHeader("Content-Type", "application/json");
      req.send();
      req.onLoad.listen(_reload);
      //req.onLoad.listen(reload, onError: onError);
    }
  }

  void _reload([event]) {
    if (event != null) {
      HttpRequest req = event.target;
      if (req.status != 200) {
        var err = JSON.decode(req.responseText);
        msg.error(err['error']);
      }
    }
    DataLoader loader = new DataLoader(msg, _getCount);
    loader.load(api.url("rest/chatrooms/count", 'chatroomcount_test.json'));
  }

  String nameErrorClass() {
    return _errorClass(_validateName);
  }

  String ownerErrorClass() {
    return _errorClass(_validateOwner);
  }

  String _errorClass(bool _validate()) {
    String _cssClassName = null;
    if(!_saveClicked) {
      _cssClassName = "ng-hide";
    } else if(_validate()) {
      _cssClassName = "ng-hide";
    }
    return _cssClassName;
  }

  bool _validateName() {
    return currentRoom == null || currentRoom.name.isNotEmpty;
  }

  bool _validateOwner() {
    return currentRoom.owner != null;
  }

  String ownerName() {
    String name;
    Logger.root.fine("Room (${currentRoom.hashCode})? ${currentRoom != null}");
    Logger.root.fine("Owner (${currentRoom.hashCode})? ${currentRoom.owner != null}");
    if(currentRoom != null && currentRoom.owner != null) {
      name = currentRoom.owner.prettyName();
    } else {
      name = "...";
    }
    return name;
  }

  String membersNames() {
    String name = "";
    int maxLabelLen = 40;
    if(currentRoom != null && currentRoom.members != null && currentRoom.members.isNotEmpty) {
      for(User member in currentRoom.members) {
        name += member.prettyName() + ",";
        if(name.length > maxLabelLen) {
          name = name.substring(0, maxLabelLen - 3) + "...";
        }
      }
      if(name.endsWith(",")) {
        name = name.substring(0, name.length - 1);
      }
    } else {
      name = "...";
    }
    return name;
  }

  void showOwnerClicked() {
    _showOwnerPicker = true;
    show = "owner";
  }

  void showMembersClicked() {
    _showMembersPicker = true;
    show = "members";
  }

  void save() {
    _saveClicked = true;
    bool _valid = _validateName();
    _valid = _valid && _validateOwner();
    if(_valid) {
      //Logger.root.fine("Encoding: ${currentRoom.hashCode}: ${currentRoom.toJson()}");
      String jsonData = JSON.encode(currentRoom.asMap());
      // trim leading and trailing quotes; kinda hacky, but it works
      //jsonData = jsonData.substring(1, jsonData.length - 1);
      Logger.root.fine("Saving: $jsonData");
      //Logger.root.fine("Saving: ${currentRoom.toString()}");
      HttpRequest req = new HttpRequest();
      if(currentRoom.id == -1) {
        // create
        req.open('POST', api.url("rest/chatroom/${currentRoom.id}"));
        Logger.root.fine("POSTing");
      } else {
        // update
        req.open('PUT', api.url("rest/chatroom/${currentRoom.id}"));
        Logger.root.fine("PUTing");
      }
      req.setRequestHeader("Content-Type", "application/json");
      req.send(jsonData);
      //req.send(JSON.encode(currentRoom.toString()));
      req.onLoad.listen((ProgressEvent e) {
        if(DataLoader.checkResponse(msg, req)) {
          _reload();
        }
      }).onError((ProgressEvent e) {
        msg.error(e.toString());
      });
      show = "";
    }
  }

  void cancel() {
    show = "";
  }

  void firstPage() {
    _currentPage = 0;
    _reload();
  }

  void previousPage() {
    if(_currentPage > 0) {
      _currentPage--;
      _reload();
    }
  }

  void nextPage() {
    if(_currentPage < maxPages - 1) {
      _currentPage++;
      _reload();
    }
  }

  void lastPage() {
    if(_currentPage < maxPages - 1) {
      _currentPage = maxPages - 1;
      _reload();
    }
  }

  List<int> pages() {
    int cutoff = 2;
    Set<int> pagesTmpSet = new Set<int>();
    for(int i = 0; i < maxPages; i++) {
      if(i < cutoff || i >= maxPages - cutoff) {
        pagesTmpSet.add(i);
      } else if(i >= _currentPage - cutoff && i <= _currentPage + cutoff) {
        pagesTmpSet.add(i);
      }
    }
    List<int> pagesList = new List<int>();
    pagesList.addAll(pagesTmpSet);
    pagesList.sort();

    int elipsis = 0;
    for(int i = 1; i < pagesList.length; i++) {
      if(pagesList[i-1] < pagesList[i] - 1) {
        pagesList.insert(i++, --elipsis);
      }
    }
    //return [0, 1, -1, 3, 4, -2, 5, 6];
    return pagesList;
  }

  String pagingDisplay(int i) => i >=0 ? "${i + 1}" : "...";

  String footerStyle(int i) => i >=0 && i != _currentPage ? "footer-link" : "footer-fill";

  void loadPage(int i) {
    if(i >= 0) {
      _currentPage = i;
      _reload();
    }
  }
}
