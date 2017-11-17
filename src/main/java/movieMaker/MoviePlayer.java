package movieMaker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class that can play movies from multiple frames Copyright Georgia Institute
 * of Technology 2007
 *
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class MoviePlayer {

	// /////////////// fields ///////////////////////////

	private final JFrame frame = new JFrame("Movie Player");
	private final JLabel frameLabel = new JLabel("No images yet!");
	private AnimationPanel animationPanel = null;
	private String dir = null;

	// ////////////////// constructors ////////////////////

	/**
	 * Constructor that takes a list of pictures
	 *
	 * @param pictureList
	 *            the list of pictures to show
	 */
	public MoviePlayer(final List pictureList) {
		animationPanel = new AnimationPanel(pictureList);
		final Picture p = (Picture) pictureList.get(0);
		final String fileName = p.getFileName();
		final File f = new File(fileName);
		dir = f.getParent() + "/";
		init();
	}

	/**
	 * Constructor that takes a directory and shows a movie from it
	 *
	 * @param directory
	 *            the directory with the frames
	 */
	public MoviePlayer(final String directory) {
		animationPanel = new AnimationPanel(directory);
		dir = directory;
		init();
	}

	/**
	 * Constructor to create a movie player by asking the user to pick the
	 * directory that contains the JPEG frames
	 */
	public MoviePlayer() {
		SimpleOutput.showInformation("Please pick a " + "directory that contains the JPEG frames");
		final String directory = FileChooser.pickADirectory();
		dir = directory;
		animationPanel = new AnimationPanel(directory);
		init();
	}

	// ///////////////////// methods ////////////////////////////

	/**
	 * Method to show the next image
	 */
	public void showNext() {
		animationPanel.showNext();
		frameLabel.setText("Frame Number " + animationPanel.getCurrIndex());
		frame.repaint();
	}

	/**
	 * Method to show the previous image
	 */
	public void showPrevious() {
		animationPanel.showPrev();
		frameLabel.setText("Frame Number " + animationPanel.getCurrIndex());
		frame.repaint();
	}

	/**
	 * Method to play the movie from the beginning
	 */
	public void playMovie() {
		frameLabel.setText("Playing Movie");
		frame.repaint();
		animationPanel.showAll();
		frameLabel.setText("Frame Number " + animationPanel.getCurrIndex());
		frame.repaint();
	}

	/**
	 * Method to play the movie from the beginning
	 *
	 * @param framesPerSecond
	 *            the number of frames to show per second
	 */
	public void playMovie(final int framesPerSecond) {
		animationPanel.setFramesPerSec(framesPerSecond);
		playMovie();
	}

	public void setFrameRate(final int rate) {
		animationPanel.setFramesPerSec(rate);
	}

	/**
	 * Method to delete all the frames before the current one
	 */
	public void delAllBefore() {
		animationPanel.removeAllBefore();
	}

	/**
	 * Method to delete all the frames after the current one
	 */
	public void delAllAfter() {
		animationPanel.removeAllAfter();
	}

	/**
	 * Method to write out the movie frames as a Quicktime movie
	 */
	public void writeQuicktime() {

		final MovieWriter writer = new MovieWriter(animationPanel.getFramesPerSec(), dir);
		writer.writeQuicktime();
	}

	/**
	 * Method to write out the movie frames as a Quicktime movie
	 */
	public void writeAVI() {
		final MovieWriter writer = new MovieWriter(animationPanel.getFramesPerSec(), dir);
		writer.writeAVI();
	}

	/**
	 * Method to add a picture to the movie
	 *
	 * @param picture
	 *            the picture to add
	 */
	public void addPicture(final Picture picture) {
		animationPanel.add(picture);
		showNext();
	}

	/**
	 * Method to set up the gui
	 */
	private void init() {
		// frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());
		final JPanel buttonPanel = new JPanel();

		// add the animation panel
		container.add(animationPanel, BorderLayout.CENTER);

		// add the frame label to the north
		final JPanel labelPanel = new JPanel();
		labelPanel.add(frameLabel);
		container.add(labelPanel, BorderLayout.NORTH);

		// add the button panel to the south
		container.add(new ButtonPanel(this), BorderLayout.SOUTH);

		// set the size of the frame
		frame.pack();

		// show the frame
		frame.setVisible(true);
	}

	/**
	 * Method to set the visibility of the frame
	 *
	 * @param flag
	 *            the visibility of the frame
	 */
	public void setVisible(final boolean flag) {
		frame.setVisible(flag);
	}

	public static void main(final String[] args) {
		final MoviePlayer moviePlayer = new MoviePlayer();
		// new MoviePlayer("c:/temp/movie4/");
		moviePlayer.playMovie(16);
	}

}
