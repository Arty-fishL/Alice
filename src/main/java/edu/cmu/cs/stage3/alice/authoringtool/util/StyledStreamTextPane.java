/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class StyledStreamTextPane extends javax.swing.JTextPane {
	/**
	 *
	 */
	private static final long serialVersionUID = -4538231575533843116L;
	public javax.swing.text.Style defaultStyle = javax.swing.text.StyleContext.getDefaultStyleContext()
			.getStyle(javax.swing.text.StyleContext.DEFAULT_STYLE);
	public javax.swing.text.Style stdOutStyle;
	public javax.swing.text.Style stdErrStyle;

	javax.swing.text.DefaultStyledDocument document = new javax.swing.text.DefaultStyledDocument();
	javax.swing.text.Position endPosition;
	StyleStream defaultStream;

	public StyledStreamTextPane() {
		setDocument(document);
		endPosition = document.getEndPosition();

		javax.swing.text.StyleConstants.setFontFamily(defaultStyle, "Monospaced");
		stdOutStyle = addStyle("stdOut", defaultStyle);
		stdErrStyle = addStyle("stdErr", defaultStyle);
		javax.swing.text.StyleConstants.setForeground(stdErrStyle,
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("stdErrTextColor"));

		defaultStream = getNewStyleStream(defaultStyle);

		setDropTarget(new java.awt.dnd.DropTarget(this, new OutputDropTargetListener()));
		addMouseListener(mouseListener);
	}

	public StyleStream getNewStyleStream(final javax.swing.text.Style style) {
		return new StyleStream(this, style);
	}

	// prevent word wrapping

	@Override
	public boolean getScrollableTracksViewportWidth() {
		final java.awt.Component parent = getParent();
		if (parent != null) {
			final int parentWidth = parent.getSize().width;
			final int preferredWidth = getUI().getPreferredSize(this).width;
			return preferredWidth < parentWidth;
		} else {
			return false;
		}
	}

	// public java.awt.Dimension getPreferredScrollableViewportSize() {
	// return new java.awt.Dimension( 5000, 100 );
	// }
	//
	// public boolean getScrollableTracksViewportWidth() {
	// return false;
	// }
	//
	// public java.awt.Dimension getMinimumSize() {
	// return new java.awt.Dimension( 5000, 0 );
	// }
	//
	// public java.awt.Dimension getPreferredSize() {
	// return new java.awt.Dimension( 5000, 200 );
	// }

	// public class StyleStream extends java.io.PrintStream {
	// protected javax.swing.text.Style style;
	// protected StyledStreamTextPane styledStreamTextPane;
	//
	// public StyleStream( StyledStreamTextPane styledStreamTextPane,
	// javax.swing.text.Style style ) {
	// super( System.out );
	// this.styledStreamTextPane = styledStreamTextPane;
	// this.style = style;
	// }
	//
	// public void write( int b ) {
	// try {
	// styledStreamTextPane.document.insertString(
	// styledStreamTextPane.endPosition.getOffset() - 1, String.valueOf( b ),
	// style );
	// } catch( javax.swing.text.BadLocationException e ) {
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
	// "Error while printing.", e );
	// }
	// }
	//
	// public void write( byte buf[], int off, int len ) {
	// try {
	// styledStreamTextPane.document.insertString(
	// styledStreamTextPane.endPosition.getOffset() - 1, new String( buf, off,
	// len ), style );
	// } catch( javax.swing.text.BadLocationException e ) {
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
	// "Error while printing.", e );
	// }
	// }
	// }

	protected final java.awt.event.MouseListener mouseListener = new CustomMouseAdapter() {

		@Override
		protected void popupResponse(final java.awt.event.MouseEvent e) {
			final javax.swing.JPopupMenu popup = createPopup();
			popup.show(e.getComponent(), e.getX(), e.getY());
			PopupMenuUtilities.ensurePopupIsOnScreen(popup);
		}

		private javax.swing.JPopupMenu createPopup() {
			final Runnable clearAllRunnable = new Runnable() {
				@Override
				public void run() {
					StyledStreamTextPane.this.setText("");
				}
			};

			final java.util.Vector structure = new java.util.Vector();
			structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("Clear All", clearAllRunnable));

			return PopupMenuUtilities.makePopupMenu(structure);
		}
	};

	class OutputDropTargetListener implements java.awt.dnd.DropTargetListener {
		public OutputDropTargetListener() {
		}

		@Override
		public void dragEnter(final java.awt.dnd.DropTargetDragEvent dtde) {
			// java.awt.datatransfer.DataFlavor[] flavors =
			// dtde.getCurrentDataFlavors();
			// System.out.println( "flavors:" );
			// for( int i = 0; i < flavors.length; i++ ) {
			// System.out.println( flavors[i].getHumanPresentableName() );
			// System.out.println( "\t" + flavors[i].getMimeType() );
			// System.out.println( "\t" + flavors[i].getPrimaryType() );
			// System.out.println( "\t" + flavors[i] );
			// }
		}

		@Override
		public void dragOver(final java.awt.dnd.DropTargetDragEvent dtde) {
		}

		@Override
		public void drop(final java.awt.dnd.DropTargetDropEvent dtde) {
			final java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
			final java.awt.datatransfer.DataFlavor[] flavors = transferable.getTransferDataFlavors();

			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable,
					edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
				try {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_LINK);
					final ElementPrototype elementPrototype = (ElementPrototype) transferable.getTransferData(
							edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);

					if (elementPrototype.getDesiredProperties().length > 0) {
						final PopupItemFactory factory = new PopupItemFactory() {
							@Override
							public Object createItem(final Object object) {
								return new Runnable() {
									@Override
									public void run() {
										if (object instanceof ElementPrototype) {
											final edu.cmu.cs.stage3.alice.core.Element e = ((ElementPrototype) object)
													.createNewElement();
											if (e instanceof edu.cmu.cs.stage3.alice.core.Question) {
												defaultStream.println(
														((edu.cmu.cs.stage3.alice.core.Question) e).getValue());
											} else {
												defaultStream.println(e);
											}
										} else {
											defaultStream.println(object);
										}
									}
								};
							}
						};
						final java.util.Vector structure = PopupMenuUtilities.makePrototypeStructure(elementPrototype,
								factory, null);
						final javax.swing.JPopupMenu popup = PopupMenuUtilities.makePopupMenu(structure);
						popup.show(StyledStreamTextPane.this, (int) dtde.getLocation().getX(),
								(int) dtde.getLocation().getY());
						PopupMenuUtilities.ensurePopupIsOnScreen(popup);
					} else {
						final edu.cmu.cs.stage3.alice.core.Element e = elementPrototype.createNewElement();
						if (e instanceof edu.cmu.cs.stage3.alice.core.Question) {
							defaultStream.println(((edu.cmu.cs.stage3.alice.core.Question) e).getValue());
						} else {
							defaultStream.println(e);
						}
					}

					dtde.getDropTargetContext().dropComplete(true);
					return;
				} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: bad flavor",
							e);
				} catch (final java.io.IOException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: IOException",
							e);
				}
			}

			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable,
					java.awt.datatransfer.DataFlavor.stringFlavor)) {
				try {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
					final Object transferredObject = transferable
							.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
					defaultStream.println(transferredObject);
					dtde.getDropTargetContext().dropComplete(true);
					return;
				} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: bad flavor",
							e);
				} catch (final java.io.IOException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: IOException",
							e);
				}
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(
					transferable, java.awt.datatransfer.DataFlavor.getTextPlainUnicodeFlavor())) {
				try {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
					final java.io.Reader transferredObject = java.awt.datatransfer.DataFlavor
							.getTextPlainUnicodeFlavor().getReaderForText(transferable);
					defaultStream.println(transferredObject);

					final java.io.BufferedReader fileReader = new java.io.BufferedReader(transferredObject);
					final Thread fileReaderThread = new Thread() {

						@Override
						public void run() {
							String line;
							try {
								while (true) {
									line = fileReader.readLine();
									if (line == null) {
										break;
									} else {
										defaultStream.println(line);
									}
								}
								fileReader.close();
							} catch (final java.io.IOException e) {
								edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool
										.showErrorDialog("Error reading file.", e);
							}
						}
					};
					fileReaderThread.start();

					dtde.getDropTargetContext().dropComplete(true);
					return;
				} catch (final java.awt.datatransfer.UnsupportedFlavorException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: bad flavor",
							e);
				} catch (final java.io.IOException e) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: IOException",
							e);
				}
			}
			dtde.rejectDrop();
			dtde.getDropTargetContext().dropComplete(true);
		}

		@Override
		public void dropActionChanged(final java.awt.dnd.DropTargetDragEvent dtde) {
		}

		@Override
		public void dragExit(final java.awt.dnd.DropTargetEvent dte) {
		}
	}
}
