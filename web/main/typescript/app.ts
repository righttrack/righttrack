define(['config/defaults', 'lib/angular'], function() {
  console.info("loaded development config");
  return require(['lib/angular-route'], function() {
    console.info("loaded angular plugins");
    return require(['local/angular'], function() {
      return require(['app/main'], function() {
        return console.info("finished loading main module");
      });
    });
  });
});
