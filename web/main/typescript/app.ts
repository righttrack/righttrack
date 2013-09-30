///<reference path="lib/angular.js"/>
///<reference path="app/main.ts"/>

import config = module('./config/defaults');
import main = module('./app/main');

var app = new main.Main();
app.bootstrap();

//console.info("loaded development config");
//require(['lib/angular-route'], function() {
//  console.info("loaded angular plugins");
//  require(['lib/angular'], function() {
//    require(['app/main'], function() {
//      var main = new Main();
//      main.bootstrap();
//      return console.info("finished loading main module");
//    });
//  });
//});
