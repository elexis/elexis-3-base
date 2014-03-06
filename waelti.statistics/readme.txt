Statistics Plug-in 0.1

TOC:
	1. Plug-in Description
	2. Contact
	3. technical information
	4. License
	
*************************************
1. Plug-in Description

This plug-in is a framework for statistical analysis within Elexis. Through
the given extension points, other plug-ins can easily add other queries and
statistical queries which can be displayed in a simple table.

For further questions you may contact Michael Waelti (see 2.Contact).

*************************************
2. Contact

Do not hesitate to contact me for any questions or suggestions:

			m.waelti at gmx ch
			
*************************************
3. technical information

You may use the extension point ("Query") to write your own fragments which use
the graphical interface of this framework. Your classes have to extend
the waelti.statistics.queries.AbstractQuery class. Depending on what kind of
data you analyse, you may extend AbstractTimeSeries which already implements
everything necessary to define a time span.

Annotations (GetProperty, SetProperty) are used to describe the fields a user
may change. All setters may throw a SetDataException if the user input is not
valid.

QueryUtils provides some methods which can be useful to convert data.

PatientCosts is a good and simple example for further studies of the quite
simple mechanisms.


*************************************
4. License

Icons used in this software are creative commons and were published by:

		http://www.famfamfam.com/lab/icons/silk/ - Mark James
