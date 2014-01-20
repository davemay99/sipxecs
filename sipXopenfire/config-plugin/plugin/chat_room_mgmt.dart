import 'dart:async';
import 'package:angular/angular.dart';
import 'package:logging/logging.dart';
//import 'component/user_picker.dart';
import 'controller/RoomsController.dart';
import 'controller/UserPickerController.dart';

class RoomsAppModule extends Module {
  RoomsAppModule() {
    type(RoomsController);
    //type(UserPickerComponent);
    type(UserPicker);
    //type(RouteInitializer, implementedBy: RoomsRouteInitializer);
    //factory(NgRoutingUsePushState, (_) => new NgRoutingUsePushState.value(false));
    //type(Profiler, implementedBy: Profiler); // comment out to enable profiling
  }
}

main() {
  Logger.root.level = Level.FINEST;
  Logger.root.onRecord.listen((LogRecord r) { print(r.level.toString() + "\t" + r.message); });
  ngBootstrap(module: new RoomsAppModule());
}
