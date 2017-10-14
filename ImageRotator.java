// Alvin Ung
// 3-1-2017


// Imports
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.*;

// Class declaration
public class ImageRotator {

	// Global variables
	static int dimension;
	static File imageFile;

	public static void main(String args[]) throws FileNotFoundException {

		// Constructors
		char[][] image = new char[dimension][dimension];
		final char[][] image2;

		// Variables
		int numThreads = 8;
		int chunkSize;
		double startTime = 0;
		double endTime = 0;
		double loadTime = 0;
		double origPrintTime = 0;
		double rotatePrintTime = 0;
		double rotateTime = 0;
		double threadRotateTime = 0;

		if (handleArguments(args)) {
			
			// Load image from file
			startTime = System.nanoTime();
			image = loadImage();
			endTime = System.nanoTime();
			loadTime = endTime - startTime;
			
			// copying image into image2
			image2 = new char[dimension][dimension];
			for (int i = 0; i < image.length; i++) {
				for (int j = 0; j < image.length; j++) {
					image2[i][j] = image[i][j];
				}
			}
			
			chunkSize = (dimension / 2) / numThreads;

			// Print original image
			startTime = System.nanoTime();
			printImage(image);
			endTime = System.nanoTime();
			origPrintTime = endTime - startTime;

			// Rotating the image no threads
			startTime = System.nanoTime();
			rotateImage(image);
			endTime = System.nanoTime();
			rotateTime = endTime - startTime;

			// Print rotated image
			startTime = System.nanoTime();
			printImage(image);
			endTime = System.nanoTime();
			rotatePrintTime = endTime - startTime;

			// Rotating image with threads
			try {
				ExecutorService ex = Executors.newFixedThreadPool(numThreads);

				startTime = System.nanoTime();

				// create a number of threads
				for (int i = 0; i < numThreads; i++) {
					final int startLayer = i * chunkSize;
					final int endLayer = (i + 1) * chunkSize;
					ex.execute(new Runnable() {
						@Override
						public void run() {
													
							char temp;

							for (int layer = startLayer; layer < endLayer; layer++) {
								int start = layer;
								int end = dimension - layer - 1;
								for (int i = start; i < end; i++) {
									int shift = i - start;

									// begin swapping
									temp = image2[start][i];
									image2[start][i] = image2[end - shift][start];
									image2[end - shift][start] = image2[end][end - shift];
									image2[end][end - shift] = image2[i][end];
									image2[i][end] = temp;
								}
							}
						}
					});
				}
				ex.shutdown();
				ex.awaitTermination(1, TimeUnit.MINUTES);

				endTime = System.nanoTime();
				threadRotateTime = endTime - startTime;
			} catch (InterruptedException e) {
				System.out.println("Something went wrong with threading.");
			}

			printRuntimes(numThreads, loadTime, origPrintTime, rotateTime, rotatePrintTime, threadRotateTime);
		}
	}

	// Prints run times to console
	static void printRuntimes(int numThreads, double load, double origPrint, double rotate, 
								double rotatePrint, double threadRotate) {

		System.out.println("\n====================================================");
		System.out.println("| Number of threads                  : " + numThreads);
		System.out.println("| Time for loading image from file   : " + (load / 1000000000.0));
		System.out.println("| Time for printing orignal image    : " + (origPrint / 1000000000.0));
		System.out.println("| Time for printing rotated image    : " + (rotatePrint / 1000000000.0));
		System.out.println("====================================================");
		System.out.println("| Time for non-threaded image rotate : " + (rotate / 1000000000.0));
		System.out.println("| Time for threaded image rotate     : " + (threadRotate / 1000000000.0));
		System.out.println("====================================================\n");
	}

	// Rotates image by swaping matrix elements index by index
	static void rotateImage(char[][] image) {

		char temp;

		for (int layer = 0; layer < dimension / 2; layer++) {
			int start = layer;
			int end = dimension - layer - 1;
			for (int i = start; i < end; i++) {
				int shift = i - start;

				// begin swapping
				temp = image[start][i];
				image[start][i] = image[end - shift][start];
				image[end - shift][start] = image[end][end - shift];
				image[end][end - shift] = image[i][end];
				image[i][end] = temp;
			}
		}
	}

	// Reads image from file and load into matrix character by character
	static char[][] loadImage() throws FileNotFoundException {

		char[][] matrix = new char[dimension][dimension];
		Scanner input = new Scanner(imageFile);
		String line;

		for (int i = 0; i < dimension; i++) {
			line = input.nextLine();
			for (int j = 0; j < dimension; j++) {
				matrix[i][j] = line.charAt(j);
			}
		}

		return matrix;
	}

	// Checks for valid command line arguments, sets variables if valid
	static boolean handleArguments(String args[]) {

		// Check for correct number of arguments
		if (args.length != 2) {
			System.out.println("Invalid arguments, terminating program...");
			return false;
		}

		// Get dimension of matrix from command line argument
		try {
			dimension = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("Error: Second argument must be a number");
			return false;
		}

		// Open and check file
		imageFile = new File(args[0]);
		if (!imageFile.canRead()) {
			System.out.println("Error: The file " + args[0] + " cannot be opened.");
			return false;
		}

		return true;
	}

	// Print image
	static void printImage(char[][] image) {

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				System.out.print(image[i][j]);
			}
			System.out.print('\n');
		}
	}
}
