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
        dest: 'src/main/typescript/lib/bower_source'
      release:
        dest: 'public/js/lib'

    clean:
      build: ['target/js/src']
      release: ['target/js/dist']
      work: ['public/js', 'public/src']
      bower: ['src/main/typescript/bower_source']

    copy:
      work:
        files: [
          expand: true
          cwd: 'src'
          dest: 'public/src'
          src: 'main/typescript/**/*.ts'
        ]
      release:
        files: [
          expand: true
          cwd: 'target/js/dist/src'
          dest: 'public/js/src'
          src: '**/*'
        ]

    typescript:
      work:
        src: ['src/main/typescript/**/*.ts']
        dest: 'public/js/app.js'
        options:
          module: 'amd'  # or commonjs
          target: 'es5'
          base_path: 'typescript'
          sourceRoot: 'public/typescript'
          sourcemap: true
          fullSourceMapPath: false
          declaration: false
          allowbool: true
          allowimportmodule: true
      build:
        src: ['src/main/typescript/**/*.ts']
        dest: 'target/js/src/app.js'
        options:
          module: 'amd'  # or commonjs
          target: 'es5'
          base_path: ''
          sourceRoot: 'target/js/src'
          sourcemap: true
          fullSourceMapPath: false
          declaration: false
          allowbool: true
          allowimportmodule: true
      test:
        src: ['src/test/typescript/**/*.ts']
        dest: 'target/js/src'
        options:
          module: 'amd'  # or commonjs
          target: 'es5'
          base_path: ''
          sourceRoot: 'target/js/src'
          sourcemap: true
          fullSourceMapPath: false
          declaration: false
          allowbool: true
          allowimportmodule: true
      release:
        src: ['src/main/typescript/**/*.ts']
        dest: 'target/js/dist/src/app.js'
        options:
          module: 'amd'  # or commonjs
          target: 'es5'
          base_path: ''
          sourceRoot: 'target/js/src'
          sourcemap: true
          fullSourceMapPath: false
          declaration: false
          allowbool: true
          allowimportmodule: true

    jasmine:
      test:
#        host: 'http://127.0.0.1:8000/'
        src: 'target/js/src/spec/**/*.js'
        options:
          specs: '**/*Spec.js'
#          helpers: '*Helper.js'

    sass:
      work:
        files: [
          'public/css/main.css': 'src/main/sass/main.scss'
        ]

    uglify:
      options:
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("mm/dd/yyyy") %> */\n'
        mangle: false
      release:
        options:
          sourceMap: (fileName) ->
            fileName + '.map'
          sourceMappingURL: (path) ->
            path.replace(/.*\/(.*)$/, '$1') + '.map'
          sourceMapPrefix: 2  # public/js
          sourceMapIn: 'public/js/src/app.js.map'
        files: [
          dest: 'public/js/app.js'
          src: 'public/js/src/app.js'
        ]

    watch:
      work:
        files: ['src/main/typescript/**/*.ts']
        tasks: ['compile-work']
      test:
        files: ['src/test/typescript/**/*.ts']
        tasks: ['typescript:build', 'typescript:test', 'jasmine']

  grunt.loadNpmTasks 'grunt-bower'
  grunt.loadNpmTasks 'grunt-contrib-clean'
  grunt.loadNpmTasks 'grunt-contrib-copy'
  grunt.loadNpmTasks 'grunt-contrib-requirejs'
  grunt.loadNpmTasks 'grunt-contrib-coffee'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-contrib-sass'
  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.loadNpmTasks 'grunt-typescript'

  grunt.registerTask 'compile-release', ['typescript:release']
  grunt.registerTask 'compile-test', ['typescript:build', 'typescript:test']
  grunt.registerTask 'compile-work', ['copy:work', 'typescript:work', 'sass:work']

  grunt.registerTask 'stage-release', ['clean', 'bower:release', 'compile-release']
  grunt.registerTask 'stage-test', ['clean', 'bower:build', 'compile-test']
  grunt.registerTask 'stage-work', ['clean:work', 'bower:release', 'compile-work']

  grunt.registerTask 'do-test', ['stage-test', 'jasmine']
  grunt.registerTask 'test', ['do-test', 'watch:test']
  grunt.registerTask 'do-work', ['stage-work']
  grunt.registerTask 'work', ['do-work', 'watch:work']

  grunt.registerTask 'release', ['stage-release', 'copy:release', 'uglify:release', 'copy:work']

  grunt.registerTask 'default', ['stage-work', 'do-test']
