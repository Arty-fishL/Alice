package movieMaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class that holds the buttons for the movie player
 *
 * @author Barb Ericson
 */
public class ButtonPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1914968679598319337L;
	// ////////////// fields ////////////////////////
	/** list for the frame rate */
	private JList frameRateList = null;
	/** label for frame rate */
	private JLabel frameRateLabel = null;
	private final JButton nextButton = new JButton("Next");
	private final JButton playButton = new JButton("Play Movie");
	private final JButton prevButton = new JButton("Prev");
	private final JButton delBeforeButton = new JButton("Delete All Previous");
	private final JButton delAfterButton = new JButton("Delete All After");
	private final JButton writeQuicktimeButton = new JButton("Write Quicktime");
	private final JButton writeAVIButton = new JButton("Write AVI");
	private MoviePlayer moviePlayer = null;

	// /////////////// Constructors /////////////////

	/**
	 * Constructor that doesn't take any parameters
	 */
	public ButtonPanel(final MoviePlayer player) {
		moviePlayer = player;

		// add the previous and next buttons to this panel
		this.add(prevButton);
		this.add(nextButton);

		// set up the frame rate list
		frameRateLabel = new JLabel("Frames per Second: ");
		this.add(frameRateLabel);
		final String[] rates = { "16", "24", "30" };
		frameRateList = new JList(rates);
		final JScrollPane scrollPane = new JScrollPane(frameRateList);
		frameRateList.setSelectedIndex(0);
		frameRateList.setVisibleRowCount(1);
		frameRateList.setToolTipText("The number of frames per second in the movie");
		frameRateList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				final String rateS = (String) frameRateList.getSelectedValue();
				final int rate = Integer.parseInt(rateS);
				moviePlayer.setFrameRate(rate);
			}
		});
		this.add(scrollPane);

		this.add(playButton);
		this.add(delBeforeButton);
		this.add(delAfterButton);
		this.add(writeQuicktimeButton);
		this.add(writeAVIButton);

		// add the action listeners to the buttons
		nextButton.setToolTipText("Click to see the next frame");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				moviePlayer.showNext();
			}
		});
		prevButton.setToolTipText("Click to see the previous frame");
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				moviePlayer.showPrevious();
			}
		});
		playButton.setToolTipText("Click to play the movie");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				moviePlayer.playMovie();
			}
		});
		delBeforeButton.setToolTipText("Click to delete all frames before the current one");
		delBeforeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				moviePlayer.delAllBefore();
			}
		});
		delAfterButton.setToolTipText("Click to delete all frames after the current one");
		delAfterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				moviePlayer.delAllAfter();
			}
		});
		writeQuicktimeButton.setToolTipText("Click to write out a Quicktime movie from the frames");
		writeQuicktimeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				moviePlayer.writeQuicktime();
			}
		});
		writeAVIButton.setToolTipText("Click to write out an AVI movie from the frames");
		writeAVIButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				moviePlayer.writeAVI();
			}
		});
	}

}