Ontologies for Activity Recognition
===================================

This repository contains source code for a high-level semi-supervised activity recognition system.

The contents can be broken down into two main components (folders):

### OWL

Contains the ontologies that we used to support our system. Three are currently present:

* [facebook_life_events.owl](/OWL/facebook_life_events.owl): ontology based on the Facebook life events feature 
* [google_places.owl](/OWL/google_places.owl): flat ontology based on the Google Places API location types
* [loc2act-activities-final.owl](/OWL/loc2act-activities-final.owl): ontology containing around 750 distinct activities after merging everything from crowdsourcing together

### Crowdsourcing

Contains all the scripts we needed to interact with Amazon MTurk including scripts to publish, approve, and reject HITs, analysis of the data, as well as various helper functions. 

In the [web](/crowdsourcing/web/) folder one can also find the web application that we developed to use in the first crowdsourcing experiment (trying to map locations to activities that might occur in them).

### Further information

For more information, please see the wiki pages of the project.
