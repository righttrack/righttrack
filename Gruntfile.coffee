module.exports = (grunt) ->
  """
  Right Track config work, build, and release cycle:

  Build/Release cycle:
  build - JavaScript files in the target/js/src directory.
  stage - Concatonated JavaScript files in the target/js/dist directory source-mapped to *.js.map
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
        dest: 'target/js/src/lib'
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
      build:
        files: [
          dest: 'target/js/dist/src/lib/requirejs.js'
          src: 'target/js/src/lib/requirejs.js'
        ]
      release:
        files: [
          expand: true
          cwd: 'target/js/dist/src'
          dest: 'public/js/src'
          src: '**/*'
        ]

    coffee:
      build:
        options:
          bare: true
          sourceMap: false
        files: [
          expand: true
          cwd: 'web/main/coffee'
          dest: 'target/js/src'
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
          dest: 'target/js/src'
          src: '**/*.coffee'
          ext: '.js'
        ]

    requirejs:
      build:
        options:
          findNestedDependencies: true
          mainConfigFile: 'target/js/src/config/defaults.js'
          baseUrl: 'target/js/src'
          name: 'app'
          out: 'target/js/dist/src/app.js'
          optimize: 'none'
      work:
        options:
          findNestedDependencies: true
          mainConfigFile: 'target/js/config/defaults.js'

    jasmine:
      test:
#        host: 'http://127.0.0.1:8000/'
        src: 'target/js/src/spec/**/*.js'
        options:
          specs: '**/*Spec.js'
#          helpers: '*Helper.js'
          template: require 'grunt-template-jasmine-requirejs'
          templateOptions:
            requireConfigFile: ['target/js/src/config/defaults.js', 'target/js/src/config/test.js']

    uglify:
      options:
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("dd-mm-yyyy") %> */\n'
        mangle: false
      release:
        options:
          sourceMap: (fileName) ->
            fileName + '.map'
          sourceMappingURL: (path) ->
            path.replace(/.*\/(.*)$/, '$1') + '.map'
        files: [
          dest: 'public/js/app.js'
          src: 'public/js/src/app.js'
        ,
          dest: 'public/js/lib/requirejs.js'
          src: 'public/js/src/lib/requirejs.js'
        ]

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

  grunt.registerTask 'stage-build', ['clean', 'bower:build', 'compile-build', 'requirejs:build', 'copy:build']
  grunt.registerTask 'stage-test', ['clean', 'bower:build', 'compile-test', 'coffee:test']
  grunt.registerTask 'stage-work', ['clean', 'bower:source', 'compile-work']

  grunt.registerTask 'do-test', ['stage-test', 'jasmine']
  grunt.registerTask 'test', ['do-test', 'watch:test']
  grunt.registerTask 'do-work', ['stage-work']
  grunt.registerTask 'work', ['do-work', 'watch:work']

  grunt.registerTask 'release', ['stage-build', 'copy:release', 'uglify:release']

  grunt.registerTask 'default', ['stage-work', 'dotest']
