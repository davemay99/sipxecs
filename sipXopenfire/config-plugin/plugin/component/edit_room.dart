library edit_room;

import 'dart:html';
import 'dart:convert';
import 'package:angular/angular.dart';
import 'package:logging/logging.dart';
import '../lib/chat_room.dart';
import '../sipxconfig.dart';


@NgComponent(
    selector: 'room-editor',
    templateUrl: 'component/edit_room.html',
    cssUrl: 'component/edit_room.css',
    publishAs: 'rEdit',
    map: const {
      'room' : '=>room',
      'show-edit' : '<=>showEdit',
    }
)
class EditRoomComponent {
  ChatRoom _room;
  bool showEdit;
  bool _saveClicked;
  bool showOwnerPicker = false;
  bool showMembersPicker = false;
  Api api = new Api();
  //UserMessage msg = new UserMessage(querySelector("#editMessage"));
  get room => _room;

  set room(ChatRoom room) {
    _saveClicked = false;
    _room = room;
  }

  void save() {
    _saveClicked = true;
    bool _valid = _validateName();
    _valid = _valid && _validateOwner();
    if(_valid) {
      Logger.root.info("Encoding: " + _room.toString());
      Logger.root.info("Encoding: " + _room.toJson());
      String jsonData = JSON.encode(_room);
      // trim leading and trailing quotes; kinda hacky, but it works 
      jsonData = jsonData.substring(1, jsonData.length - 1);
      Logger.root.info("Saving: " + jsonData);
      HttpRequest req = new HttpRequest();
      if(_room.id == -1) {
        // create
        req.open('POST', api.url("rest/chatrooms"));
        Logger.root.info("POSTing");
      } else {
        // update
        req.open('PUT', api.url("rest/chatroom/" + room.id.toString()));
        Logger.root.info("PUTing");
      }
      req.setRequestHeader("Content-Type", "application/json");
      req.send(jsonData);
      req.onLoad.listen((e) {
        //DataLoader.checkResponse(msg, req);
      }).onError((e) {
        //msg.error(e.toString());
      });
    }
    showEdit = !_valid;
  }

  void cancel() {
    showEdit = false;
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
    return _room == null || _room.name.isNotEmpty;
  }

  bool _validateOwner() {
    return _room.owner != null;
  }

  String ownerName() {
    String name;
    if(_room != null && _room.owner != null) {
      name = _room.owner.prettyName();
    } else {
      name = "...";
    }
    return name;
  }

  String membersNames() {
    String name = "";
    int maxLabelLen = 40;
    if(_room != null && _room.members != null && _room.members.isNotEmpty) {
      for(User member in _room.members) {
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
    showOwnerPicker = true;
    showMembersPicker = false;
  }

  void showMembersClicked() {
    showMembersPicker = true;
    showOwnerPicker = false;
  }
}
