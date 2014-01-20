library user_ctrl;

import 'package:angular/angular.dart';
import 'package:logging/logging.dart';
import 'dart:html';
import 'dart:convert';
import '../sipxconfig.dart';
import '../lib/chat_room.dart';

@NgController(
    selector: '[ng-controller=user-picker]',
    publishAs: 'uPicker',
    map: const {
      'selected' : '<=>internalSelection',
      'show' : '<=>show',
      'single-selection' : '@singleSelection'
    }
)
class UserPicker {
  var _internalSelection;
  bool show = false;
  bool _singleSelection = false;

  // this is a list of all available users
  List<User> users = [];
  // this is the selection list for the above
  var selectedUsers;
  // this is the list of users that have been picked (i.e. selected and moved to the left)
  List<User> picked = [];
  // this is the selection list for the above
  List<User> selectedPicked = [];
  // filterName can't be null
  String filterName = "";

  Api api = new Api(test : false);
  UserMessage msg = new UserMessage(querySelector("#userMessage"));

  UserPicker() {
    Logger.root.fine("Creating user picker");
    Logger.root.fine("Msg is $msg");
    DataLoader loader = new DataLoader(msg, _loadData);
    loader.load(api.url("rest/users/", 'users_test.json'));
  }

  void _loadData(String data) {
    List<Map> tmpUsers = JSON.decode(data);
    Logger.root.fine("Loaded: ${tmpUsers}");
    tmpUsers.forEach((Map userMap) => users.add(new User(userMap)));
  }

  bool isAvailable(User u) {
    bool result = true;
//    Logger.root.info("User: " + u.toString());
//    Logger.root.info("Picked: " + picked.toString());
    if(u != null && picked != null && !picked.isEmpty) {
       result = !picked.contains(u);
    }
//    Logger.root.info("Available: " + result.toString());
    return result;
  }

  void addAll() {
    for(User u in users) {
      if(!picked.contains(u)) {
        picked.add(u);
      }
    }
  }

  void addSelected() {
    picked.addAll(selectedUsers);
    selectedUsers.clear();
  }

  void removeSelected() {
    picked.removeWhere((User u) => selectedPicked.contains(u));
    selectedPicked.clear();
  }

  void removeAll() {
    picked.clear();
    selectedPicked.clear();
  }

  void close() {
    Logger.root.fine("Setting selection ($singleSelection): ${singleSelection ? selectedUsers : picked}");
    internalSelection = singleSelection ? selectedUsers : picked;
    Logger.root.fine("Set selection: $internalSelection");
    show = false;
  }

  get singleSelection => _singleSelection;

  set singleSelection(String val) => _singleSelection = (val == "true");

  get internalSelection => _internalSelection;

  set internalSelection(var selection) {
    _internalSelection = selection;
    if(!singleSelection) {
      selectedUsers = [];
      picked = selection != null ? selection : [];
    }
  }

//  get show => _show;
//
//  set show(bool val) {
//    Logger.root.fine("Picker show: $_show, single: $singleSelection");
//    _show = val;
//  }
}
