module.exports = (grunt) ->
  """
  Right Track JavaScript build configuration.

  Test/Work/Release cycle:
  test - Concatonated JavaScript file in the target/js/righttrack directory with test code at the end.
  work - Concatonated and source mapped JavaScript file in the public/js directory.
  release - Optimized and uglified JavaScript file in the public/js directory.

  """

  grunt.initConfig

    pkg: grunt.file.readJSON 'package.json'

    bower:
      public:
        dest: 'public/js/lib'

    clean:
      app: [
        'public/js/app.js'
        'public/js/app.js.map'
        'public/js/app/**/*.js'
        'public/js/app/**/*.js.map'
      ]
      test: [
        'public/js/test.js'
        'public/js/test.js.map'
      ]
      lib: [
        'public/js/lib'
      ]

    ts:
      options:
        module: 'commonjs'
        sourceMap: true
        sourceRoot: '/typescript/righttrack/app'
      app:
        src: ['public/typescript/righttrack/app/**/*.ts']
        html: ['public/typescript/righttrack/app/**/*.html']
        reference: 'public/typescript/righttrack/app/reference.ts'
        out: 'public/js/app.js'
      watch:
        src: ['public/typescript/righttrack/app/**/*.ts']
        html: ['public/typescript/righttrack/app/**/*.html']
        reference: 'public/typescript/righttrack/app/reference.ts'
        out: 'public/js/app.js'
        watch: 'public/typescript'
      test:
        src: ['public/typescript/righttrack/test/**/*.ts']
        html: ['public/typescript/righttrack/test/**/*.html']
        reference: 'public/typescript/righttrack/test/reference.ts'
        out: 'public/js/test.js'
        options:
          sourceRoot: null

    jasmine:
      test:
        host: 'http://localhost:8888/'
        src: [
          # angular must be loaded first before any angular plugins
          'public/js/lib/angular.js'
          # unordered libraries
          'public/js/lib/*'
          # local code
          'public/js/test.js'
        ]
        options:
          keepRunner: true

    sass:
      public:
        files: [
          'public/css/main.css': 'src/main/sass/main.scss'
        ]

    watch:
      test:
        files: ['righttrack/**/*.ts']
        tasks: ['compile-test', 'run-test']

  grunt.loadNpmTasks 'grunt-bower'
  grunt.loadNpmTasks 'grunt-contrib-clean'
  grunt.loadNpmTasks 'grunt-contrib-copy'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-contrib-sass'
  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.loadNpmTasks 'grunt-ts'

  grunt.registerTask 'default', ['work']

  grunt.registerTask 'init', ['bower']

  grunt.registerTask 'compile-work', ['ts:app']
  grunt.registerTask 'compile-test', ['ts:test']

  grunt.registerTask 'work-and-watch', ['ts:watch']
  grunt.registerTask 'test-and-watch', ['compile-test', 'run-test', 'watch:test']

  grunt.registerTask 'run-test', ['jasmine:test']

  grunt.registerTask 'work', ['clean:app', 'work-and-watch']
  grunt.registerTask 'test', ['clean:test', 'test-and-watch']
