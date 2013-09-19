module.exports = (grunt) ->

  grunt.initConfig
    pkg: grunt.file.readJSON 'package.json'
    coffee:
      compile:
        options:
          bare: true
          sourceMap: true
        files: [
            expand: true
            cwd: 'public/coffee'
            dest: 'public/js'
            src: '**/*.coffee'
            ext: '.js'
          ,
            # Create the public js files
            # TODO: Concatonate the results into a coffeescript file
#            expand: true
#            cwd: 'src/main/coffee'
#            dest: 'public/js'
#            src: '**/*.coffee'
#            ext: '.js'
#          ,
            # Main script files
#            expand: true
#            cwd: 'src/main/coffee'
#            dest: 'src/main/js'
#            src: '**/*.coffee'
#            ext: '.js'
#          ,
            # Test script files
            expand: true
            cwd: 'src/test/coffee'
            dest: 'src/test/js'
            src: '**/*.coffee'
            ext: '.js'
        ]
    requirejs:
      app:
        options:
          findNestedDependencies: true
          mainConfigFile: 'public/coffee/config/dev.coffee'
          baseUrl: 'src/main/js'
          name: 'app'
          out: 'public/js/app.js'
          optimize: 'none'
    jasmine:
      all:
        host: 'http://127.0.0.1:8000/'
        src: 'src/test/js/**/*.js'
        options:
          specs: '**/*Spec.js'
#          helpers: '*Helper.js'
          template: require 'grunt-template-jasmine-requirejs'
          templateOptions:
            requireConfigFile: ['src/main/js/config.js', 'src/test/js/config.js']
#    concat
#      options:
#        separator: ';'
#      dist:
#        src: ['src/**/*.js'],
#        dest: 'dist/<%= pkg.name %>.js'
#    uglify:
#      options:
#        banner: '/*! <%= pkg.name %> <%= grunt.template.today("dd-mm-yyyy") %> */\n'
#      dist:
#        files:
#          'dist/<%= pkg.name %>.min.js': ['<%= concat.dist.dest %>']
    watch:
      files: ['public/coffee/**/*.coffee', 'src/test/coffee/**/*.coffee']
      tasks: ['coffee', 'jasmine']

  grunt.loadNpmTasks 'grunt-contrib-requirejs'
  grunt.loadNpmTasks 'grunt-contrib-coffee'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-template-jasmine-requirejs'
  grunt.loadNpmTasks 'grunt-contrib-watch'
#  grunt.loadNpmTasks 'grunt-contrib-uglify'
#  grunt.loadNpmTasks 'grunt-contrib-jshint'
#  grunt.loadNpmTasks 'grunt-contrib-concat'

  grunt.registerTask 'default', ['coffee', 'watch']
  grunt.registerTask 'test', ['coffee', 'jasmine']

