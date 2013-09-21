# Load all root dependencies
#define ['lib/require-order!config/devconf', 'lib/require-order!local/angular', 'lib/require-order!app/main'], ->
define ['config/devconf', 'lib/angular'], ->
  console.info "loaded development config"
  require ['lib/angular-route'], ->
    console.info "loaded angular plugins"
    require ['app/main'], ->
      console.info "finished loading main module"
