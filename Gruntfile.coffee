module.exports = (grunt) ->
  """
  Right Track config work, build, and release cycle:

  Build/Release cycle:
  build - JavaScript files in the src/main directory.
  release (build) - Optimized and uglified JavaScript files in the public directory.

  Test/Work cycle:
  test - Raw JavaScript specs and config files in the src/test directory.
  source - Raw JavaScript source files in the public directory.
  work (source) - Source-mapped Raw JavaScript with CoffeeScript in the public directory.

  """

  grunt.initConfig

    pkg: grunt.file.readJSON 'package.json'

    bower:
      build:
        dest: 'target/js/lib'
      source:
        dest: 'public/js/lib'

    clean:
      build: ['target/js']
      work: ['public/js', 'public/coffee']

    copy:
      work:
        files: [
          expand: true
          cwd: 'web/main/coffee'
          dest: 'public/coffee'
          src: '**/*.coffee'
        ]
      release:
        files: [
          dest: 'public/js/app.js'
          src: 'target/js/app.js'
        ]

    coffee:
      build:
        options:
          bare: true
          sourceMap: false
        files: [
          expand: true
          cwd: 'web/main/coffee'
          dest: 'target/js'
          src: '**/*.coffee'
          ext: '.js'
        ]
      work:
        options:
          bare: true
          sourceMap: true
        files: [
          expand: true
          cwd: 'public/coffee'
          dest: 'public/js'
          src: '**/*.coffee'
          ext: '.js'
        ]
      test:
        options:
          bare: true
          sourceMap: false
        files: [
          expand: true
          cwd: 'web/test/coffee'
          dest: 'target/js'
          src: '**/*.coffee'
          ext: '.js'
        ]

    requirejs:
      build:
        options:
          findNestedDependencies: true
          mainConfigFile: 'target/js/config/devconf.js'
          baseUrl: 'target/js'
          name: 'app'
          out: 'target/js/app.js'
          optimize: 'none'

    jasmine:
      test:
#        host: 'http://127.0.0.1:8000/'
        src: 'target/js/spec/**/*.js'
        options:
          specs: '**/*Spec.js'
#          helpers: '*Helper.js'
          template: require 'grunt-template-jasmine-requirejs'
          templateOptions:
            requireConfigFile: ['target/js/config/devconf.js', 'target/js/config/testconf.js']

    uglify:
      options:
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("dd-mm-yyyy") %> */\n'
      release:
        options:
          sourceMap: 'public/js/app.js.map'
        files:
          'public/js/app.min.js': 'public/js/app.js'

    watch:
      all:
        files: ['web/**/*.coffee']
        tasks: ['copy:source', 'coffee']
      work:
        files: ['web/main/coffee/**/*.coffee']
        tasks: ['build-work']
      test:
        files: ['web/**/*.coffee']
        tasks: ['coffee:build', 'coffee:test', 'jasmine']

  grunt.loadNpmTasks 'grunt-bower'
  grunt.loadNpmTasks 'grunt-contrib-clean'
  grunt.loadNpmTasks 'grunt-contrib-copy'
  grunt.loadNpmTasks 'grunt-contrib-requirejs'
  grunt.loadNpmTasks 'grunt-contrib-coffee'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-watch'

  grunt.registerTask 'compile-build', ['coffee:build']
  grunt.registerTask 'compile-test', ['coffee:build', 'coffee:test']
  grunt.registerTask 'compile-work', ['copy:work', 'coffee:work']

  grunt.registerTask 'build', ['clean', 'bower:build', 'compile-build']
  grunt.registerTask 'build-test', ['clean', 'bower:build', 'compile-test', 'coffee:test']
  grunt.registerTask 'build-work', ['clean', 'bower:source', 'compile-work']

  grunt.registerTask 'do-test', ['build-test', 'jasmine']
  grunt.registerTask 'test', ['do-test', 'watch:test']
  grunt.registerTask 'do-work', ['build-work']
  grunt.registerTask 'work', ['do-work', 'watch:work']

  grunt.registerTask 'release', ['build', 'requirejs:build', 'copy:release', 'uglify:release']

  grunt.registerTask 'default', ['build-work', 'dotest']
