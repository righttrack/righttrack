# Requires custom add-ons to angular and returns the global angular for any dependants
define 'angular', ['lib/angular', 'lib/angular-route'], ->
  angular
