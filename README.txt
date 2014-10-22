Pearl Grlz Project

This project employs OpenMRS framework to create a modular web application which provides the users online surveys with 
 location tracking function, correlating where users spend time with their self-reported health risk behaviors.
 
 1. Requirements
 
     OpenMRS should be setup, and run well on your server.  Please refer to http://www.openmrs.org site for how to set up
     OpenMRS and Servlet Server and EIS.
     
     Set up the Chica module running on the server; or, if ATD and DSS modules were seperated from Chica module, then just
     set up the DSS and ATD modules running on server, since PearlGrlz project depends on DSS and ATD modules and their
     data tables. 
 
 
 2.  Configuration
 
 	2.1  Database setup
 	       Once set up the OpenMRS, run the pearlgrlz_setup.sql  in MySQL database to setup the CONCEPT dictionary for this
 	       project.   The "pearlgrlz_setup.sql" file is in the openmrs/module/pearlgrlz/metadata/scripts .
	
	2.2  Medical Logic Module (MLM) Rules setup
			All the four hundred MLM rules are in openmrs/module/pearlgrlz/ruleLibrary ;  copy all of them, and put them into 
			installation-drive\chica\mlmRuleDirectory ,  for instance, c:\chica\mlmRuleDirectory 
			Also create several sub-directories in installation-drive\chica :  these are mlmRuleArchive,   javaRuleDirectory,
			javaRuleArchive, ruleClassDirectory 
	
 3.  Run the survey
 
 	3.1  Once the above setup is done,  load the pearlgrlz module from OpenMRS web running on your server,  go to "manage
 			module" to load the compiled pearlgrlz.revision#.mod module.
 			
 	3.2	After making sure no errors from the server logs, can access the Survey application with the following link from either
 			desktop or Mobile phones.
 			http://installation-server:assigned-port/openmrs/module/pearlgrlz/surveyPearlgrlzPage1.form

4.	Contact
	
	4.1	For any issues, supports, please contact Children's Health Service Research at IUPUI.