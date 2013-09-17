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
            cwd: 'src/main/coffee'
            dest: 'src/main/js'
            src: '**/*.coffee'
            ext: '.js'
          ,
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
          mainConfigFile: 'src/main/coffee/config.coffee'
          baseUrl: 'src/main/js'
          name: 'app'
          out: 'public/js/app.js'
          optimize: 'none'
    jasmine:
      pivotal:
        src: 'src/test/js/*.js'
        options:
          specs: '*Spec.js'
          helpers: '*Helper.js'
#    concat:
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
#    watch:
#      files: ['<%= coffee.compile.files %>']
#      tasks: ['coffee']

  grunt.loadNpmTasks 'grunt-contrib-requirejs'
  grunt.loadNpmTasks 'grunt-contrib-coffee'
  grunt.loadNpmTasks 'grunt-contrib-jasmine'
#  grunt.loadNpmTasks 'grunt-contrib-uglify'
#  grunt.loadNpmTasks 'grunt-contrib-jshint'
#  grunt.loadNpmTasks 'grunt-contrib-watch'
#  grunt.loadNpmTasks 'grunt-contrib-concat'

#  grunt.registerTask 'test', ['jshint', 'qunit']
#  grunt.registerTask 'test', ['coffee', 'jasmine']
  grunt.registerTask 'default', ['coffee', 'jasmine']
#  grunt.registerTask 'default', ['coffee', 'requirejs']
