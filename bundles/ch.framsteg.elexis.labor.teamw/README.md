# Requirements
* User Login delivered by LaborTeam W
* User Password, delivered by Labor Team W

# Configuration
In order to run this plugin the following configuration steps need to be done. To configure the plugin only the file teamw.properties must be modified

## Step 1
Extract the preconfigured teamw.properties from the Jar file (resources/teamw.properties). This file resides outside the Jar (default: /opt/elexis/teamw/teamw.properties) in order to make the plugin independent from changes triggered by LaborTeam W

## Step 2
Register the path to teamw.properties in application.properties (inside Jar)

## Step 3
Extract the RSA private key from Jar (key/Elexis-001_private2.pem) and copy it somewhere in the Filesystem

## Step 4
Register the PATH to the RSA key within teamw.properties (props.teamw.teamw.key.path)

## Step 5
Enter the user login in teamw.properties (props.teamw.message.property.user.login)

## Step 6
Base64 encode the user password and register it in teamw.properties (props.teamw.message.property.user.pw)

Thats it, have a nice day!

