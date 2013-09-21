# Requires custom add-ons to angular and returns the global angular for any dependants
#define 'angular', ['lib/require-order!lib/angular', 'lib/require-order!lib/angular-route'] ->
define 'angular', ['lib/angular', 'lib/angular-route'], ->
  angular
