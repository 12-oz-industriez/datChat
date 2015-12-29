module.exports = function(grunt) {
    
    grunt.registerTask('sampleTask', 'Log some stuff.', function() {
        grunt.log.write('Logging some stuff.').ok();
    });

};
