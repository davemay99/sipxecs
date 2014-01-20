library rooms_route;

import 'package:angular/angular.dart';
import 'dart:html';
import 'dart:async';

class RoomsRouteInitializer implements RouteInitializer {
  init(Router router, ViewFactory view) {
    router.root
      ..addRoute(
          name: 'add',
          path: '/add',
          enter: view('edit_target.html')
      )
      ..addRoute(
          name: 'room',
          path: '/room/:id',
          mount: (Route route) => route
          ..addRoute(
              name: 'edit',
              path: '/edit',
              enter: view('edit_target.html'),
              leave: (RouteEvent evt) => evt.allowLeave(_confirm())
          )
//      ..addRoute(
//          name: 'default',
//          defaultRoute: true,
//          enter: (RouteEvent evt) { window.alert(evt.path); return view('error.html');}
//      )
    );
  }

  Future<bool> _confirm() {
    Completer comp = new Completer();
    bool opt = window.confirm("Leave page?");
    comp.complete(opt);

    return comp.future;
  }
}
