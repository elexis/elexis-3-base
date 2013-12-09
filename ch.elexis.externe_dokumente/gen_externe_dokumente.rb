#! /usr/bin/env ruby 

# Copyright 2011 Niklaus Giger <niklaus.giger@member.fsf.org>
# License: Eclipse Public License 1.0
# Erzeugte (beliebig) viele externe Dateien, um das Elexis-Plugin Externe Dokumente einem
# Stresstest zu unterziehen.
# Falls ein demoDB exisitiert werden Dateien für die Patienten Dagobert und Donald Duck erzeugt.
#

# Anzpassen an den lokale MySQL-Datenbank
Host='jenkins-service'
User='elexis'
Pass='elexisTest'
MyDb='rgw'

# Für wieviele Patienten sollen wieviele Dateien erstellt werden
MaxPatients = 10;
DokumentsPerPatient = 100;
# wohin sollen die Dateien platziert werden

require "dbi" # gem install dbi dbd-mysql
require 'fileutils'

SecondsPerDay =  24*60*60
InSubDirs = false
# InSubDirs ? Ordner = "/opt/elexis-externe-dateien/" : Ordner  = '/opt/elexis-pharmapool/daten'
Ordner = '/data/test/1'

base = File.expand_path(File.dirname(__FILE__))
base = File.dirname(File.dirname(base))
demoPath = "#{base}/demoDB"
puts "demo #{demoPath}: #{File.exists?(demoPath)}"

Abwechslung = ['datei', 'file', 'fichier', 'papel']
def genDokument(name, vorname, geburtsdatum, id)
  m= /(.*)[-_. \t]/.match(vorname)
  vorname = m[1] if m
  fn = ''
  ordner = File.dirname(Ordner)+"/#{id.modulo(4)+1}"
  if InSubDirs
    fn = sprintf("%s/%-6.6s%s %s/%-6.6s%s %s_%d.txt", ordner, name, vorname, geburtsdatum, name, vorname, Abwechslung[id.modulo(Abwechslung.size)], id).gsub('//','/')
  else
    fn = sprintf("%s/%-6.6s%s %s_%d.txt", ordner, name, vorname, Abwechslung[id.modulo(Abwechslung.size)], id).gsub('//','/')
  end
  puts fn if $VERBOSE
  dir = File.dirname(fn)
  FileUtils.makedirs(dir)
  f = File.open(fn, "w+")
  f.puts("#Erzeugt durch #{__FILE__} am #{Time.now}")
  f.close
  modtime = Time.now  - 300*id # (2*SecondsPerDay*MaxPatients*DokumentsPerPatient) # + SecondsPerDay*id
  jahr =2000+id.modulo(10)
  monat= 1+ (id/10).modulo(12)
  tag =  1+ (id/120).modulo(28)
  modtime = Time.utc(jahr, monat, tag)
  File.utime(modtime, modtime, fn)
end

def generiere_fuer_demoDb
 1.upto(4).each { |id| dest = "#{File.dirname(Ordner)}/#{id}"; FileUtils.makedirs(dest) }
 1.upto(DokumentsPerPatient).each{ |id| genDokument("Duck", "Donald", "1927-07-03", id) }
 1.upto(DokumentsPerPatient).each{ |id| genDokument("Duck", "Dagobert", "1911-12-09", 500+id) }
end

def generiere_externe_dokumente

   begin
	 $dbh = DBI.connect("DBI:Mysql:#{MyDb}:#{Host}", User, Pass)
     # get server version string and display it
     row = $dbh.select_one("SELECT VERSION()")
     puts "Server version: " + row[0]
     j=0
     sth = $dbh.execute("SELECT bezeichnung1, bezeichnung2 FROM kontakt where istpatient = '1' order by bezeichnung1")
       sth.fetch do |row|
	 j +=1
	 name = row[0]
	 vorname = row[1]
	 printf "j %d bezeichnung1: %s bezeichnung1: %s\n", j, name, vorname
	 1.upto(DokumentsPerPatient).each{ |id| genDokument(name, vorname, 'datei', id) }
	 break if j >= MaxPatients
     end
     sth.finish
   rescue DBI::DatabaseError => e
     puts "An error occurred"
     puts "Error code: #{e.err}"
     puts "Error message: #{e.errstr}"
   ensure
     # disconnect from server
     $dbh.disconnect if $dbh
   end

end
if File.exists?(demoPath)
	generiere_fuer_demoDb
else
	generiere_externe_dokumente
end


