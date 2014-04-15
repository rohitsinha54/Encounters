Encounters
==========

A java program which determines encounters between users according to different condition from their geographical coordinates present in log files.

Done as a coding challenge from [Highlight](highlig.ht)

## Problem Description
The file 'userdata.txt' contains a list of geographic points for 4 different
users sorted by the time they were recorded.  The format of each line is 4
fields separated by a pipe character.

`name|unixtime|latitude|longitude`

>example line:
>
>danny|1335324573|37.784372722982|-122.39083248497

Please write a program (any language you choose) that will read the
userdata.txt file and output a list of encounters for the 4 users using
these rules:

1. Generate an encounter if the users are <= 150 meters apart.

2. If a user has not generated a point for 6 hours, assume they are no longer
  active (don't generate any more encounters until they have a new point)

3. Do not generate more than one encounter per 24 hours for any two users (if
  unclejoey and danny had an encounter at 5pm on a Tuesday, they could not
  have another encounter until 5pm on Wednesday)

>Use the Haversine formula for geodesic distance calculation

The output format should be similar to the input file:

`unixtime|name1|latitude1|longitude1|name2|latitude2|longitude2`

where name1 is lexigraphically lower than name2 and lines are sorted in
ascending order by unixtime.  The ordering of encounters with the same
unixtime is unspecified.

So if 'danny' and 'unclejoey' encountered each other, it would look like this:

>1327418725|danny|37.77695245908|-122.39847741481|unclejoey|37.777335807234|-122.39812024905

## File Structure
| Filename        | Description          |
| ------------- |-------------|
| App.java      | The main class for Encounters application which find encounters between users from their geographic location |
| Encounter.java      | Class for encounters between users | 
| Location.java | Class for managing geographic locations of users |
| User.java | The user class|



##Dependencies
This project depends on the following:

1. [Maven](http://maven.apache.org/)

##How to use
Clone the repository on you local machine and from the command line excute to following command from the parent directory

>r******@R*****-MacBook-Pro> mvn package
>
>r******@R*****-MacBook-Pro> java -cp target/EncounterApp-1.0-BETA.jar com.rohitsinha.encounters.App [input log filepath] [output filepath]

## Testing
The program has been tested on following operating systems:

1. Mac OSX 10.9.2


##Version
1.0 beta

##Contact Information
Please report any bugs or issues to:
[talktorohit54@gmail.com](mailto:talktorohit54@gmail.com)

##License
[MIT License](https://github.com/rohitsinha54/Encounters/blob/master/LICENSE)



