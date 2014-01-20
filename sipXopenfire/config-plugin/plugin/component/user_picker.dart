library user;

import 'package:angular/angular.dart';
import 'package:logging/logging.dart';
import 'dart:convert';
import '../sipxconfig.dart';
import '../lib/chat_room.dart';

@NgComponent(
    selector: 'user-picker',
    templateUrl: 'component/user_picker.html',
    cssUrl: 'component/user_picker.css',
    publishAs: 'uPicker',
    map: const {
      'selected' : '<=>internalSelection',
      'show' : '<=>show',
      'single-selection' : '@singleSelection'
    }
)
class UserPickerComponent {
  // this is a list of all available users
  List<User> users = [];
  // this is the selection list for the above
  var selectedUsers;
  // this is the list of users that have been picked (i.e. selected and moved to the left)
  List<User> picked = [];
  // this is the selection list for the above
  List<User> selectedPicked = [];
  var _internalSelection;
  bool _show = false;
  bool _singleSelection = false;
  Api api = new Api(test : false);
  // filterName can't be null
  String filterName = "";

  UserPickerComponent() {
    DataLoader loader = new DataLoader(null, _loadData);
    loader.load(api.url("rest/users/", 'users_test.json'));
  }

  void _loadData(String data) {
    List<Map> tmpUsers = JSON.decode(data.substring(data.indexOf("["))); // I get some garbage from the server first, dunno why
    for(Map tmpUser in tmpUsers) {
      User u = new User(tmpUser);
      users.add(u);
    }
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
    picked.removeWhere((_) => true);
    selectedPicked.clear();
  }

  void close() {
    Logger.root.fine("Setting selection: ${selectedUsers.toString()}");
    internalSelection = singleSelection ? selectedUsers : picked;
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

  get show => _show;

  set show(bool val) {
    Logger.root.info("Picker show: ${_show}");
    _show = val;
  }
}
