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
        dest: 'target/js/lib'
      public:
        dest: 'public/js/lib'

    clean:
      app: [
        'public/js/app/**/*.js'
        'public/js/app/**/*.js.map'
      ]
      lib: [
        'public/js/lib/**/*.js'
        'public/js/lib/**/*.js.map'
      ]
      target: [
        'target/js'
      ]
      typescript: [
        'public/typescript'
      ]
      misc: [
        'src/main/typescript/**/*.js'
        'src/main/typescript/**/*.js.map'
      ]

    copy:
      js:
        files: [
          expand: true
          cwd: 'target/js'
          dest: 'public/js'
          src: '**'
        ]
      ts:
        files: [
          expand: true
          cwd: 'src/main/typescript'
          dest: 'public/typescript'
          src: '**'
        ]

    ts:
      compile:
        src: ['src/main/typescript/app/**/*.ts']
        html: ['src/main/typescript/app/**/*.html']
        reference: 'src/main/typescript/app/reference.ts'
        out: 'target/js/app.js'
        options:
          sourcemap: false
      test:
        src: ['src/test/typescript/app/**/*.ts']
        html: ['src/test/typescript/app/**/*.html']
        reference: 'src/test/typescript/app/reference.ts'
        out: 'target/js/tests.js'
        options:
          module: "commonjs"
          sourcemap: false
      public:
        src: ['public/typescript/app/**/*.ts']
        html: ['public/typescript/app/**/*.html']
        reference: 'public/typescript/app/reference.ts'
        out: 'public/js/app.js'

    jasmine:
      test:
        host: 'http://127.0.0.1:8000/'
        src: 'target/js/**/*.js'
        options:
          specs: 'tests.js'
#          helpers: '*Helper.js'

    sass:
      public:
        files: [
          'public/css/main.css': 'src/main/sass/main.scss'
        ]

#    uglify:
#      options:
#        banner: '/*! <%= pkg.name %> <%= grunt.template.today("mm/dd/yyyy") %> */\n'
#        mangle: false
#      release:
#        options:
#          sourceMap: (fileName) ->
#            fileName + '.map'
#          sourceMappingURL: (path) ->
#            path.replace(/.*\/(.*)$/, '$1') + '.map'
#          sourceMapPrefix: 2  # public/js
#          sourceMapIn: 'public/js/src/app.js.map'
#        files: [
#          dest: 'public/js/app.js'
#          src: 'public/js/src/app.js'
#        ]

    watch:
      source:
        files: ['src/main/typescript/**/*.ts']
        tasks: ['source', 'compile']
#      test:
#        files: ['src/test/typescript/**/*.ts']
#        tasks: ['typescript:build', 'typescript:test', 'jasmine']

  grunt.loadNpmTasks 'grunt-bower'
  grunt.loadNpmTasks 'grunt-contrib-clean'
  grunt.loadNpmTasks 'grunt-contrib-copy'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
  grunt.loadNpmTasks 'grunt-contrib-sass'
  grunt.loadNpmTasks 'grunt-contrib-uglify'
  grunt.loadNpmTasks 'grunt-contrib-watch'
  grunt.loadNpmTasks 'grunt-ts'

#  grunt.registerTask 'do-test', ['stage-test', 'jasmine']
#  grunt.registerTask 'test', ['do-test', 'watch:test']

  grunt.registerTask 'source', ['clean:typescript', 'copy:ts']
  grunt.registerTask 'compile', ['ts:public']
  grunt.registerTask 'compile-test', ['ts:compile', 'ts:test']
  grunt.registerTask 'build', ['bower:build', 'ts:compile']
  grunt.registerTask 'stage', ['init', 'build']
  grunt.registerTask 'deploy', ['copy:js', 'bower:public']

  grunt.registerTask 'test', ['compile-test', 'jasmine']
#  grunt.registerTask 'work', ['source', 'compile', 'deploy', 'watch']
  grunt.registerTask 'work', ['source', 'compile', 'deploy', 'watch']
  grunt.registerTask 'release', ['stage', 'deploy']  # Tests here
