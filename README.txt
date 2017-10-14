***** COMPILE AND RUN INSTRUCTIONS *****

	1. javac ImageRotator.java
	2. java ImageRotator [image filename] [dimension]

	*** NOTE: Image Must have square dimensions and in .txt format ***

	Ex: java ImageRotator image.txt 200

****************************************

PROGRAM DESCRIPTION

This program reads in image from a txt file and loads it into a matrix. It will then rotate the image by 90 degrees, timing how long it takes to perform the rotation. It will then time and perform the same rotation using the set number of threads allowed. At the end, it will print out runtimes for performing each test, along with some additional information.

****************************************

Line 25 : 	"int numThreads = 8;" allows you to set number of threads used for concurrent implementation.