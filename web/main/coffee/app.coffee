# Load all root dependencies
#define ['lib/require-order!config/devconf', 'lib/require-order!local/angular', 'lib/require-order!app/main'], ->
define ['config/defaults', 'lib/angular'], ->
  console.info "loaded development config"
  require ['lib/angular-route'], ->
    console.info "loaded angular plugins"
    # to define "angular"
    require ['local/angular'], ->
      require ['app/main'], ->
        console.info "finished loading main module"
